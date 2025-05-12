package com.poly.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.poly.dao.CartDAO;
import com.poly.dao.OrderDAO;
import com.poly.dao.OrderDetailDAO;
import com.poly.model.Cart;
import com.poly.model.Order;
import com.poly.model.OrderDetail;
import com.poly.model.User;
import jakarta.servlet.http.HttpSession;
@Controller
public class CheckoutController {

    @Autowired
    CartDAO cartDAO;

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    OrderDetailDAO orderDetailDAO;

   

    @PostMapping("/checkout")
    public String checkout(Model model, HttpSession session) { 
        User user = (User) session.getAttribute("user");  
        if (user == null) {
            return "redirect:/login"; 
        }

        List<Cart> cartItems = cartDAO.findByUser(user);
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; 
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(calculateTotal(cartItems)); 
        order.setStatusId(1); // Đã đặt hàng
        orderDAO.save(order);

        for (Cart cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setPrice(cartItem.getProduct().getPrice()); 

            // 🔴 Cần set size cho OrderDetail nếu có
            if (cartItem.getSize() != null) {
                orderDetail.setSize(cartItem.getSize());
            } else {
                throw new RuntimeException("Size is missing for product: " + cartItem.getProduct().getProductName());
            }

            orderDetailDAO.save(orderDetail);
            cartDAO.delete(cartItem); 
        }

        return "redirect:/checkout-success"; 
    }


    private double calculateTotal(List<Cart> cartItems) {
        double total = 0;
        for (Cart item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    @GetMapping("/checkout-success")
    public String checkoutSuccess(Model model,HttpSession session) {
    	 User user = (User) session.getAttribute("user"); 
        if (user == null) {
            return "redirect:/login";
        }

        List<Cart> cartItems = cartDAO.findByUser(user); // Lấy tất cả sản phẩm trong giỏ hàng

        if (!cartItems.isEmpty()) {
            model.addAttribute("cartItems", cartItems); // Truyền danh sách sản phẩm vào model
        } else {
            model.addAttribute("message", "Giỏ hàng của bạn trống.");
        }

        return "checkout-success";
    }
}