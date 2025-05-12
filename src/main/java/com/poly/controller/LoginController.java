package com.poly.controller;

import com.poly.dao.UserDAO;
import com.poly.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = userDAO.findByUserNameAndPassword(userName, password);

        if (user != null) {
            session.setAttribute("user", user);

            // Kiểm tra roleID và chuyển hướng
            if (user.getRoleID() == 1) { // Admin
                return "redirect:/admin/dashboard"; // Trang dashboard của admin
            } else if (user.getRoleID() == 2) { // User
                return "redirect:/index"; // Trang danh sách người dùng
            } else {
                // Xử lý các roleID khác nếu cần
                return "redirect:/"; // Trang mặc định
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            return "redirect:/login";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Hủy session
        return "redirect:/login"; // Chuyển hướng đến trang đăng nhập
    }
}