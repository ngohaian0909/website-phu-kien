package com.poly.controller;

import com.poly.dao.*;
import com.poly.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private OrderDAO orderDAO;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userDAO.findAll();
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/users/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/create";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userDAO.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm người dùng thất bại: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        User user = userDAO.findById(id).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            return "user/edit";
        } else {
            return "redirect:/users";
        }
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userDAO.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật người dùng thất bại: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            User user = userDAO.findById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng!");
                return "redirect:/users";
            }

            List<Order> orders = orderDAO.findByUser(user); // Check for associated orders.  Assumes you have a findByUser method in your OrderDAO.
            if (!orders.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Người dùng đã từng mua hàng, không thể xóa!");
                return "redirect:/users";
            }

            userDAO.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xóa người dùng thất bại: " + e.getMessage());
        }
        return "redirect:/users";
    }
}