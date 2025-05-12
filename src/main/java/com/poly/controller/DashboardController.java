package com.poly.controller;

import com.poly.dao.ProductDAO; // Import ProductDAO
import com.poly.dao.UserDAO;
import com.poly.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserDAO userDAO;

    @Autowired // Autowire ProductDAO
    private ProductDAO productDAO;

    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRoleID() != 1) {
            return "redirect:/login";
        }

        long totalUsers = userDAO.count();
        model.addAttribute("totalUsers", totalUsers);

        long totalProducts = productDAO.count(); // Get total products
        model.addAttribute("totalProducts", totalProducts); // Add to model

        List<User> users = userDAO.findAll();
        model.addAttribute("users", users);

        return "admin/dashboard";
    }
}