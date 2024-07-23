package com.vanlang.webbanhang.service;

import com.vanlang.webbanhang.model.CartItem;
import com.vanlang.webbanhang.model.Order;
import com.vanlang.webbanhang.model.OrderDetail;
import com.vanlang.webbanhang.model.User;
import com.vanlang.webbanhang.repository.OrderDetailRepository;
import com.vanlang.webbanhang.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService; // Assuming you have a CartService

    @Transactional
    public Order createOrder(String customerName, List<CartItem> cartItems, String customerAddress, String customerEmail, Integer customerPhone, String customerNote, String customerPay) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerAddress(customerAddress);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setCustomerNote(customerNote);
        order.setCustomerPay(customerPay);
        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
        // Optionally clear the cart after order placement
        cartService.clearCard();
        return order;
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order getOrderById(Long id) {
        // Lấy người dùng từ cơ sở dữ liệu dựa trên tên đăng nhập
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
    public void updateOrder(Order order) {
        // Cập nhật thông tin người dùng dựa trên id đăng nhập
        Order existingOrder = orderRepository.findById(order.getId()).orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setCustomerName(order.getCustomerName());
        existingOrder.setCustomerAddress(order.getCustomerAddress());
        existingOrder.setCustomerEmail(order.getCustomerEmail());
        existingOrder.setCustomerNote(order.getCustomerNote());
        existingOrder.setCustomerPay(order.getCustomerPay());
        existingOrder.setCustomerPhone(order.getCustomerPhone());
        orderRepository.save(existingOrder);
    }
    public void deleteOrderById(Long id) {
        // Xóa người dùng từ cơ sở dữ liệu dựa trên id đăng nhập
        orderRepository.deleteById(id);
    }
}

