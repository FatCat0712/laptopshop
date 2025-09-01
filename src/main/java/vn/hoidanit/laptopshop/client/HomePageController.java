package vn.hoidanit.laptopshop.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.service.OrderService;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@Controller
public class HomePageController {
    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;


    @Autowired
    public HomePageController(
            ProductService productService,
            UserService userService,
            OrderService orderService
    ) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(0,10);
        Page<Product> page = productService.listByPage(pageable);
        List<Product> listProducts = page.getContent();
        model.addAttribute("listProducts", listProducts);
        return "client/homepage/show";
    }

    @GetMapping(value = {"/",""})
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        return "client/auth/login";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        RegisterDTO registerDTO = new RegisterDTO();
        model.addAttribute("registerUser", registerDTO);
        return "client/auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute(name = "registerUser") RegisterDTO registerDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "client/auth/register";
        }
        User user = userService.registerDTOtoUser(registerDTO);
        user.setRole(userService.getRoleByName("USER"));
        userService.save(user);

        return "redirect:/login";
    }

    @GetMapping("/access_denied")
    public String getDeniedPage() {
        return "client/auth/deny";
    }

    @GetMapping("/order_history")
    public String getOrderHistory(HttpServletRequest request, Model model) {
            HttpSession session = request.getSession(false);
            User currentUser = new User();
            Long id = (Long) session.getAttribute("id");
            currentUser.setId(id);
            List<Order> listOrders = orderService.findOrderByUser(currentUser);
            model.addAttribute("listOrders", listOrders);
            return "client/cart/order_history";
    }


}
