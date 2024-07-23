package com.vanlang.webbanhang.controller;

import com.vanlang.webbanhang.model.CartItem;
import com.vanlang.webbanhang.model.Order;
import com.vanlang.webbanhang.model.User;
import com.vanlang.webbanhang.service.CartService;
import com.vanlang.webbanhang.service.CategoryService;
import com.vanlang.webbanhang.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping("/")
public class OrderController {
    @Autowired
    private CategoryService categoryService;// Đảm bảo bạn đã inject CategoryService
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());//
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam("customerName") String customerName,
                              @RequestParam("customerAddress") String customerAddress,
                              @RequestParam("customerEmail") String customerEmail,
                              @RequestParam("customerPhone") Integer customerPhone,
                              @RequestParam("customerNote") String customerNote,
                              @RequestParam("customerPay") String customerPay, Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName,cartItems,customerAddress,customerEmail,customerPhone,customerNote,customerPay);
        return "redirect:/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("categories",categoryService.getAllCategories());//
        model.addAttribute("message", "Đơn hàng của bạn đã được đặt.");
        return "cart/order-confirmation";
    }
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> order = orderService.getAllOrders();
        model.addAttribute("categories",categoryService.getAllCategories());//
        model.addAttribute("orders",order );
        return "cart/order-list";
    }
    @GetMapping("/orders/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("orders", order);
        return "/cart/order-edit";
    }

    // POST request to update category
    @PostMapping("/orders/update/{id}")
    public String updateOrder(@PathVariable("id") Long id, @Valid Order order, BindingResult result, Model model) {
        if (result.hasErrors()) {
            order.setId(id);
            return "/cart/order-edit";
        }
        orderService.updateOrder(order);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/orders";
    }

    // GET request for deleting category
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        orderService.deleteOrderById(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/orders";
    }
}

