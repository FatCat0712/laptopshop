package vn.hoidanit.laptopshop.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        return "admin/user/create";
    }

    @GetMapping("/admin/user")
    public String listUser(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("pageTitle", "Table Users");
        return "admin/user/show";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetail(@PathVariable Long id, Model model) {
        User user = service.get(id);
        if(user != null) {
            model.addAttribute("pageTitle", String.format("User Detail - ID %d", id));
            model.addAttribute("id", id);
            model.addAttribute("user", user);
            return "admin/user/detail";
        }
        return "admin/user/user_not_found";
    }

    @GetMapping("/admin/user/update/{id}")
    public String updateUser(@PathVariable Long id, Model model) {
        User user = service.get(id);
        if(user != null) {
            model.addAttribute("pageTitle", "Update a user");
            model.addAttribute("user", user);
            return "admin/user/create";
        }
        return "admin/user/user_not_found";
    }

    @PostMapping("/admin/user/save")
    public String saveUser(User userInform) {
        service.save(userInform);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeletePage(@PathVariable Long id, Model model) {
        User user = new User();
        model.addAttribute("id", id);
        model.addAttribute("pageTitle", String.format("Delete User: %d", id));
        model.addAttribute("user", user);
        return "admin/user/confirm_delete";
    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(@ModelAttribute("user") User user) {
        service.deleteUser(user.getId());
        return "redirect:/admin/user";
    }










}
