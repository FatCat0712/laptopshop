package vn.hoidanit.laptopshop.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.service.OrderService;

import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getOrders(Model model) {
        List<Order> listOrders = orderService.listAllOrders();
        model.addAttribute("listOrders", listOrders);
        return "admin/order/show";
    }

    @GetMapping("/admin/order/detail/{id}")
    public String getOrderDetail(@PathVariable(name = "id") Long orderId, Model model) {
        Order order = orderService.findOrderById(orderId);
        model.addAttribute("order", order);
        return "admin/order/detail";
    }

    @GetMapping("/admin/order/update/{id}")
    public String updateOrderStatus(@PathVariable(name = "id") Long orderId, Model model) {
        Order order = orderService.findOrderById(orderId);
        model.addAttribute("pageTitle", String.format("Update order (ID: %d)", order.getId()));
        model.addAttribute("order", order);
        return "admin/order/update";
    }

    @PostMapping("/admin/order/save")
    public String saveOrderStatus(Order order) {
       orderService.saveOrder(order);
        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String getDeletePage(@PathVariable(name = "id") Long orderId, Model model) {
        model.addAttribute("id", orderId);
        return "admin/order/confirm_delete";
    }

    @PostMapping("/admin/order/delete")
    public String deleteOrder(@RequestParam(name = "id") Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin/order";
    }

}
