package vn.hoidanit.laptopshop.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {
    @GetMapping("/admin/order")
    public String getOrders() {
        return "admin/order/show";
    }
}
