package vn.hoidanit.laptopshop.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    public Order findOrderById(Long orderId) {
       Optional<Order> order = orderRepository.findById(orderId);
       return order.orElse(null);
    }

    public void saveOrder(Order orderInForm) {
        Long orderId = orderInForm.getId();
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()) {
            Order savedOrder = order.get();
            savedOrder.setStatus(orderInForm.getStatus());
            orderRepository.save(savedOrder);
        }
    }

    public void deleteOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()) {
            Order savedOrder = order.get();
            List<OrderDetail> listOrderDetails = savedOrder.getOrderDetails();
            for(OrderDetail od : listOrderDetails) {
                orderDetailRepository.deleteById(od.getId());
            }
            orderRepository.deleteById(savedOrder.getId());
        }
    }

}
