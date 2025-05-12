package com.poly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.poly.dao.OrderDetailDAO;
import com.poly.dao.OrderDAO;
import com.poly.model.Order;
import com.poly.model.OrderDetail;

@Controller
public class OrderManageController {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderDetailDAO orderDetailDAO;

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderDAO.findAllWithUsers(); // Sử dụng phương thức mới
        model.addAttribute("orders", orders);
        return "orders/list";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer orderId, Model model) {
        Order order = orderDAO.findByIdWithUser(orderId); // Sử dụng phương thức mới
        if (order == null) {
            return "redirect:/orders";
        }

        List<OrderDetail> orderDetails = orderDetailDAO.findByOrder(order);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "orders/details";
    }
}