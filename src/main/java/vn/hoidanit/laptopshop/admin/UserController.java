package vn.hoidanit.laptopshop.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {
    private final UserService userService;
    private final UploadService uploadService;

    @Autowired
    public UserController(UserService userService, UploadService uploadService) {
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @RequestMapping(value = {"","/"})
    public String index() {
        List<User> listUsers = userService.listAll();
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
        List<User> listUsers = userService.listAll();
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("pageTitle", "Table Users");
        return "admin/user/show";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetail(@PathVariable Long id, Model model) {
        User user = userService.get(id);
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
        User user = userService.get(id);
        if(user != null) {
            model.addAttribute("pageTitle", "Update a user");
            model.addAttribute("user", user);
            return "admin/user/create";
        }
        return "admin/user/user_not_found";
    }

    @PostMapping("/admin/user/save")
    public String saveUser(
            @Valid User userInform,
            BindingResult bindingResult,
            @RequestParam(name = "avatarFile", required = false) MultipartFile imageFile
    ) throws IOException {
            //validate
            if(bindingResult.hasErrors()) {
                return "admin/user/create";
            }

            if(!imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
                userInform.setAvatar(fileName);
                String uploadDir = "avatar/";
                uploadService.saveFile(uploadDir, fileName, imageFile);
            }
            userService.save(userInform);
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
        userService.deleteUser(user.getId());
        return "redirect:/admin/user";
    }










}
