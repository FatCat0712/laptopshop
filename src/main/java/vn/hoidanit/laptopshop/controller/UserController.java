package vn.hoidanit.laptopshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;

import java.util.List;

@Controller
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @RequestMapping(value = {"","/"})
    public String index() {
        List<User> listUsers = service.listAll();
        System.out.println(listUsers);
        return "index";
    }

    @GetMapping("/admin/user/create")
    public String createUser(Model model) {
        User user = new User();
        model.addAttribute("pageTitle", "Create Users");
        model.addAttribute("user", user);
        return "user_form";
    }

    @GetMapping("/admin/user")
    public String listUser(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("pageTitle", "Table Users");
        return "users";
    }

    @PostMapping("/admin/user/save")
    public String saveUser(User userInform) {
        service.save(userInform);
        return "redirect:/admin/user";
    }










}
