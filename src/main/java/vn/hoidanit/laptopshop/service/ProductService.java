package vn.hoidanit.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.*;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static vn.hoidanit.laptopshop.service.specification.ProductSpecs.*;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public ProductService(
            ProductRepository productRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository
    ) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Page<Product> listByPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    public Page<Product> fetchProductsWithSpec(ProductCriteriaDTO productCriteriaDTO, Pageable pageable) {
        Specification<Product> combinedSpec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        if(productCriteriaDTO.getTarget() != null) {
            String[] targets = productCriteriaDTO.getTarget().split(",");
            List<String> targetList = Arrays.asList(targets);
            Specification<Product> currentSpecs = matchListTarget(targetList);
           combinedSpec = combinedSpec.and(currentSpecs);
        }

        if(productCriteriaDTO.getFactory() != null) {
            String[] targets = productCriteriaDTO.getFactory().split(",");
            List<String> targetList = Arrays.asList(targets);
            Specification<Product> currentSpecs = matchListFactory(targetList);
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if(productCriteriaDTO.getPrice() != null) {
            String[] prices = productCriteriaDTO.getPrice().split(",");
            Specification<Product> priceSpecs = (((root, query, criteriaBuilder) -> criteriaBuilder.disjunction()));
            for(String price : prices) {
                double min = 0;
                double max = 0;
                switch (price) {
                    case "duoi-10-trieu":
                        max = 10000000;
                        break;
                    case "10-toi-15-trieu":
                        min = 10000000;
                        max = 15000000;
                        break;
                    case "15-toi-20-trieu":
                        min = 15000000;
                        max = 20000000;
                        break;
                    case "tren-20-trieu":
                        min = 20000000;
                        break;
                }

                if(min == 0) {
                    priceSpecs = priceSpecs.or(maxPrice(max));
                }
                else if(max == 0) {
                    priceSpecs = priceSpecs.or(minPrice(min));
                }
                else {
                    priceSpecs = priceSpecs.or(matchListPrice(min,max));
                }
            }
            combinedSpec = combinedSpec.and(priceSpecs);
        }

        return productRepository.findAll(combinedSpec, pageable);


    }

    public Product get(Long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("Could not find any products with ID " + productId);
        }
        return product.get();
    }

    public void save(Product productInForm) throws ProductNotFoundException {
        Long productId = productInForm.getId();
        Product product;
        if (productId != null) {
            product = get(productId);
        } else {
            product = new Product();
        }
        product.setName(productInForm.getName());
        product.setPrice(productInForm.getPrice());
        product.setDetailDesc(productInForm.getDetailDesc());
        product.setShortDesc(productInForm.getShortDesc());
        product.setQuantity(productInForm.getQuantity());
        product.setFactory(productInForm.getFactory());
        product.setTarget(productInForm.getTarget());

        if (productInForm.getImage() != null && !productInForm.getImage().isEmpty()) {
            product.setImage(productInForm.getImage());
        }

        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public void handleAddProductToCart(String email, Long productId, Long quantity, HttpSession session) throws ProductNotFoundException {
//        check if user already had a cart or else create new cart
        User user = userService.getUserByEmail(email);
        if (user != null) {
            Product product = get(productId);
            CartDetail detail = new CartDetail();
//          update the quantity if the product is already in cart
            Optional<CartDetail> cartDetail = cartDetailRepository.findByUserAndProduct(user, product);
            if(cartDetail.isPresent()) {
                detail = cartDetail.get();
                long currentQuantity = detail.getQuantity();
                detail.setQuantity(currentQuantity + quantity);
                detail.setPrice(detail.getPrice() * detail.getQuantity());
            }
            else {
                detail.setUser(user);
                detail.setProduct(product);
                detail.setPrice(product.getPrice());
                detail.setQuantity(quantity);
            }

            cartDetailRepository.save(detail);

             session.setAttribute("sum",cartDetailRepository.findByUser(user).size());

        }
    }

    public void handleDeleteCartDetail(Long detailId, HttpSession session) {
        String email = session.getAttribute("email").toString();
        User user = userService.getUserByEmail(email);
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(detailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();

            int currentSum = cartDetailRepository.findByUser(user).size();

            int newSum = 0;
            // update cart
            if (currentSum > 1) {
                // update current cart
                newSum = currentSum - 1;
            }
            session.setAttribute("sum", newSum);
            cartDetailRepository.deleteById(cartDetail.getId());
        }
    }

    public List<CartDetail> fetchByUser(User user) {
        return cartDetailRepository.findByUser(user);
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> listCartDetails) {
        for(CartDetail cartDetail : listCartDetails) {
            Optional<CartDetail> cdOptional = cartDetailRepository.findById(cartDetail.getId());
            if(cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                currentCartDetail.setPrice(currentCartDetail.getQuantity() * currentCartDetail.getProduct().getPrice());
                cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(
            User user, HttpSession session ,
            String receiverName, String receiverAddress, String receiverPhone
    ) {

//        step 1: get cart by user
            List<CartDetail> listCartDetails = fetchByUser(user);
            if(!listCartDetails.isEmpty()) {
                //        Create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setStatus("PENDING");
                double totalPrice = 0;
                for(CartDetail cd : listCartDetails) {
                    totalPrice += cd.getPrice();
                }
                order.setTotalPrice(totalPrice);
                order = orderRepository.save(order);

                //        create orderDetail
                for(CartDetail cd : listCartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setQuantity(cd.getQuantity());
                    orderDetail.setPrice(cd.getProduct().getPrice());
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetailRepository.save(orderDetail);
                }

//                step 2: delete cart_detail
                for(CartDetail cd : listCartDetails) {
                    cartDetailRepository.deleteById(cd.getId());
                }

//                step 3: update session
                session.setAttribute("sum", 0);

            }
    }






}

