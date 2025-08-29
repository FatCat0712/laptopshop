package vn.hoidanit.laptopshop.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.service.ProductService;

@Controller
public class ItemController {
    private final ProductService service;

    @Autowired
    public ItemController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/product/{id}")
    public String getProductPage(Model model, @PathVariable(name = "id") Long id) throws ProductNotFoundException {
        Product product = service.get(id);
        model.addAttribute("product", product);
        return "client/product/detail";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductCart(@PathVariable(name = "id") Long productId, HttpServletRequest request) throws ProductNotFoundException {
        HttpSession session = request.getSession(false);

        String email = session.getAttribute("email").toString();

        service.handleAddProductToCart(email, productId, session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(Model model) {
        return "client/cart/show";
    }
}
