package vn.hoidanit.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    @Autowired
    public ProductService(
            ProductRepository productRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService
    ) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
    }

    public List<Product> listAll() {
        return productRepository.findAll();
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

    public void handleAddProductToCart(String email, Long productId, HttpSession session) throws ProductNotFoundException {
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
                detail.setQuantity(currentQuantity + 1);
            }
            else {
                detail.setUser(user);
                detail.setProduct(product);
                detail.setPrice(product.getPrice());
                detail.setQuantity(1);
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
}

