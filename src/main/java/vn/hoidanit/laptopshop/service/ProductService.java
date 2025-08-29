package vn.hoidanit.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;

    @Autowired
    public ProductService(
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository,
            UserService userService
    ) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.userService = userService;
    }

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public Product get(Long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isEmpty()) {
            throw new ProductNotFoundException("Could not find any products with ID " + productId);
        }
        return product.get();
    }

    public void save(Product productInForm) throws ProductNotFoundException {
        Long productId = productInForm.getId();
        Product product;
        if(productId != null) {
            product = get(productId);
        }
        else {
            product = new Product();
        }
        product.setName(productInForm.getName());
        product.setPrice(productInForm.getPrice());
        product.setDetailDesc(productInForm.getDetailDesc());
        product.setShortDesc(productInForm.getShortDesc());
        product.setQuantity(productInForm.getQuantity());
        product.setFactory(productInForm.getFactory());
        product.setTarget(productInForm.getTarget());

        if(productInForm.getImage() != null && !productInForm.getImage().isEmpty()) {
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
        if(user != null) {
            Cart cart = cartRepository.findByUser(user);

            if(cart == null) {
                cart = new Cart();
                cart.setUser(user);
                cart.setSum(0);
                cart.setCartDetails(new ArrayList<>());
                cart = cartRepository.save(cart);
            }

            Product product = get(productId);

            CartDetail cartDetail = cartDetailRepository.findByCartAndProduct(cart, product);
            if(cartDetail != null) {
                long currentQuantity = cartDetail.getQuantity();
                cartDetail.setQuantity(currentQuantity + 1);

            }else {
                int currentCartSum = cart.getSum();
                cart.setSum(currentCartSum + 1);
                cart = cartRepository.save(cart);

                session.setAttribute("sum", cart.getSum());

                cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProduct(product);
                cartDetail.setPrice(product.getPrice());
                cartDetail.setQuantity(1);
                cart.getCartDetails().add(cartDetail);
            }

            //    update cart (sum)


            cartDetailRepository.save(cartDetail);




        }
    }
}
