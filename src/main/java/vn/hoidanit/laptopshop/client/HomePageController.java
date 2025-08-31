package vn.hoidanit.laptopshop.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@Controller
public class HomePageController {
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public HomePageController(
            ProductService productService,
            UserService userService
    ) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String getHomePage(Model model, HttpServletRequest request) {
        List<Product> listProducts = productService.listAll();
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


}
