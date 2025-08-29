package vn.hoidanit.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.Role;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.RegisterDTO;
import vn.hoidanit.laptopshop.repository.RoleRepository;
import vn.hoidanit.laptopshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> listAll() {
        return userRepo.findAll();
    }

    public User listByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public User get(Long id) {
        Optional<User> user = userRepo.findById(id);
        return user.orElse(null);
    }


    public void save(User userInForm) {
        Long userId = userInForm.getId();
        User user;
        if(userId != null) {
            user = get(userId);
        }
        else {
            user = new User();
            user.setEmail(userInForm.getEmail());
        }


        if(userInForm.getPassword() != null && !userInForm.getPassword().isEmpty())  {
            user.setPassword(encodePassword(userInForm.getPassword()));
        }

        Role role = getRoleByName(userInForm.getRole().getName());
        user.setRole(role);
        user.setAvatar(userInForm.getAvatar());
        user.setFullName(userInForm.getFullName());
        user.setAddress(userInForm.getAddress());
        user.setPhone(userInForm.getPhone());
        userRepo.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }


    public void deleteUser(long id) {
        userRepo.deleteById(id);
    }

    public Role getRoleByName(String name) {
        return roleRepo.findByName(name);
    }

    public User registerDTOtoUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFirstName() + "-" + registerDTO.getLastName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        return user;
    }

    public boolean checkEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }


}
