package vn.hoidanit.laptopshop.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.hoidanit.laptopshop.client.advice.GlobalCartAdvice;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.exception.ProductNotFoundException;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {
    private final ProductService productService;
    private final GlobalCartAdvice globalCartAdvice;

    @Autowired
    public ItemController(
            ProductService productService,
            UserService userService,
            GlobalCartAdvice globalCartAdvice
    ) {
        this.productService = productService;
        this.globalCartAdvice = globalCartAdvice;
    }

    @GetMapping("/product/{id}")
    public String getProductPage(Model model, @PathVariable(name = "id") Long id) throws ProductNotFoundException {
        Product product = productService.get(id);
        model.addAttribute("product", product);
        return "client/product/detail";
    }

    @PostMapping("/add-product-to-cart/{id}")
    public String addProductCart(@PathVariable(name = "id") Long productId, HttpServletRequest request) throws ProductNotFoundException {
        HttpSession session = request.getSession(false);

        String email = session.getAttribute("email").toString();

        productService.handleAddProductToCart(email, productId, session);
        return "redirect:/";
    }

    @GetMapping("/cart")
    public String getCartPage(HttpServletRequest request, Model model) {
        List<CartDetail> listCartDetails = globalCartAdvice.populateCart(request);
        long total = 0;
        for(CartDetail detail: listCartDetails) {
            total += (long) (detail.getQuantity() * detail.getProduct().getPrice());
        }
        model.addAttribute("total", total);

        return "client/cart/show";
    }

    @PostMapping("/delete-cart-product/{detailId}")
    public String deleteCartDetail(@PathVariable(name = "detailId") Long detailId, HttpSession session) {
         productService.handleDeleteCartDetail(detailId, session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String getCheckOutPage(HttpServletRequest request, Model model) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        Long id = (Long) session.getAttribute("id");
        currentUser.setId(id);

        List<CartDetail> listCardDetails = productService.fetchByUser(currentUser);

        double totalPrice = 0;
        for(CartDetail cd : listCardDetails) {
            totalPrice += cd.getPrice() * cd.getQuantity();
        }

        model.addAttribute("listCartDetails", listCardDetails);
        model.addAttribute("totalPrice", totalPrice);

        return "client/cart/checkout";

    }

    @PostMapping("/confirm-checkout")
    public String getCheckOutPage(HttpServletRequest request) {
        List<CartDetail> listCartDetails = updateCartDetailsList(request);
        productService.handleUpdateCartBeforeCheckout(listCartDetails);
        return "redirect:/checkout";
    }

    @PostMapping("/place-order")
    private String handlePlaceOrder(
            HttpServletRequest request,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone
    ) {
        User currentUser = new User();
        HttpSession session = request.getSession(false);
        Long id = (Long)session.getAttribute("id");
        currentUser.setId(id);
        productService.handlePlaceOrder(currentUser, session, receiverName, receiverAddress, receiverPhone);

        return "redirect:/thanks";
    }

    @GetMapping("/thanks")
    public String getThankYouPage() {
        return "client/cart/thanks";
    }

    private List<CartDetail> updateCartDetailsList(HttpServletRequest request) {
        String[] detailIDs = request.getParameterValues("detailId");
        String[] detailQuantities = request.getParameterValues("detailQuantity");

        List<CartDetail> cartDetails = new ArrayList<>();

        if(detailIDs != null && detailIDs.length > 0) {
            for(int i = 0; i < detailIDs.length; i++) {
                Long cartDetailId = Long.parseLong(detailIDs[i]);
                long detailQuantity = Long.parseLong(detailQuantities[i]);
                CartDetail cartDetail = new CartDetail(cartDetailId, detailQuantity);
                cartDetails.add(cartDetail);
            }
        }

        return cartDetails;
    }
}
