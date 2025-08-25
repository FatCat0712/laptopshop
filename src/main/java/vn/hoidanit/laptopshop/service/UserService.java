package vn.hoidanit.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> listAll() {
        return repo.findAll();
    }

    public User listByEmail(String email) {
        return repo.findByEmail(email);
    }

    public User get(Long id) {
        Optional<User> user = repo.findById(id);
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
            user.setPassword(userInForm.getPassword());
        }
        user.setFullName(userInForm.getFullName());
        user.setAddress(userInForm.getAddress());
        user.setPhone(userInForm.getPhone());
        repo.save(user);
    }

    public void deleteUser(long id) {
        repo.deleteById(id);
    }
}
