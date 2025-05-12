package com.poly.controller;

import com.poly.dao.OrderDetailDAO;
import com.poly.model.OrderDetail;
import com.poly.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors; // Import for Java 8 streams

@Controller
public class OrderHistoryController {

    @Autowired
    private OrderDetailDAO orderDetailDAO;

    @GetMapping("/order-history")
    public String showOrderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<OrderDetail> orderDetails = orderDetailDAO.findByOrder_User(user);
        model.addAttribute("orderDetails", orderDetails);
        return "customer/order-history"; // Correct template name
    }

    @GetMapping("/order-history/{orderId}")
    public String showOrderDetail(@PathVariable Integer orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<OrderDetail> orderDetails = orderDetailDAO.findByOrder_User(user).stream()
                .filter(od -> od.getOrder().getOrderID().equals(orderId)) // Use .equals() for Integer comparison
                .collect(Collectors.toList()); // Use collect(Collectors.toList())

        if (orderDetails.isEmpty()) {
            return "redirect:/order-history"; // Or show an error message
        }

        model.addAttribute("orderDetails", orderDetails);
        return "customer/purchases"; // Correct template for details
    }
}