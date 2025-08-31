package vn.hoidanit.laptopshop.client.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@ControllerAdvice
public class GlobalCartAdvice {
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public GlobalCartAdvice(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @ModelAttribute("listCartDetails")
    public List<CartDetail> populateCart(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) return List.of();

       Object email = session.getAttribute("email");
        if(email == null) return List.of();

        User user = userService.getUserByEmail(email.toString());
        if(user == null) return List.of();

        List<CartDetail> listCartDetails = productService.fetchByUser(user);

        return listCartDetails.isEmpty() ? List.of() : listCartDetails;

    }
}
