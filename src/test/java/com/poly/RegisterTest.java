package com.poly;

import com.poly.controller.RegisterController;
import com.poly.dao.UserDAO;
import com.poly.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RegisterTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowRegisterForm() {
        String viewName = registerController.showRegisterForm(model);

        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("register", viewName);
    }

    @Test
    void testTC_03_ValidPhoneRegistration() {
        // Kiểm tra đăng ký với nhập số điện thoại hợp lệ
        User user = new User();
        user.setUserName("Nguyễn Vũ");
        user.setEmail("vu@gmail.com");
        user.setPhone("0958627138");
        user.setAddress("Hoc Mon");
        user.setPassword("Pass@123"); // Mật khẩu hợp lệ để vượt qua validation

        when(userDAO.existsByUserName("Nguyễn Vũ")).thenReturn(false);
        when(userDAO.existsByPhone("0958627138")).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO).save(user);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Đăng ký thành công, chuyển hướng đến trang đăng nhập");
        assertEquals("redirect:/login", result);
    }

    @Test
    void testTC_04_ValidAccountRegistration() {
        // Kiểm tra đăng ký với tài khoản hợp lệ
        User user = new User();
        user.setUserName("Ngô Hải An");
        user.setEmail("an095@gmail.com");
        user.setPhone("0932995578");
        user.setAddress("Quận 12, TP.HCM");
        user.setPassword("Pass@123"); // Mật khẩu hợp lệ để vượt qua validation

        when(userDAO.existsByUserName("Ngô Hải An")).thenReturn(false);
        when(userDAO.existsByEmail("an095@gmail.com")).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO).save(user);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Đăng ký thành công, thông báo người dùng đã tồn tại và yêu cầu nhập lại các trường");
        assertEquals("redirect:/login", result);
    }

    @Test
    void testTC_05_InvalidPhoneRegistration() {
        // Kiểm tra đăng ký với số điện thoại không hợp lệ
        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("test@example.com");
        user.setPhone("088795"); // Số điện thoại không hợp lệ
        user.setAddress("Test Address");
        user.setPassword("Pass@123");

        when(userDAO.existsByUserName(any())).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO, never()).save(any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Số điện thoại phải có 10 chữ số");
        assertEquals("redirect:/register", result);
    }

    @Test
    void testTC_06_InvalidEmailRegistration() {
        // Kiểm tra đăng ký với email không hợp lệ
        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("an123"); // Email không hợp lệ
        user.setPhone("0987654321");
        user.setAddress("Test Address");
        user.setPassword("Pass@123");

        when(userDAO.existsByUserName(any())).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO, never()).save(any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Địa chỉ email không hợp lệ");
        assertEquals("redirect:/register", result);
    }

    @Test
    void testTC_07_EmptyFieldRegistration() {
        // Kiểm tra đăng ký với thông tin nhập dữ liệu trống
        User user = new User();
        user.setUserName(""); // Trường rỗng
        user.setEmail("test@example.com");
        user.setPhone("0987654321");
        user.setAddress("Test Address");
        user.setPassword("Pass@123");

        when(userDAO.existsByUserName(any())).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO, never()).save(any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Hãy nhập thông tin đầy đủ");
        assertEquals("redirect:/register", result);
    }

    @Test
    void testTC_08_InvalidPasswordRegistration() {
        // Kiểm tra đăng ký với mật khẩu không hợp lệ
        User user = new User();
        user.setUserName("TestUser");
        user.setEmail("test@example.com");
        user.setPhone("0987654321");
        user.setAddress("Test Address");
        user.setPassword("123"); // Mật khẩu không hợp lệ

        when(userDAO.existsByUserName(any())).thenReturn(false);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO, never()).save(any());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất 1 ký tự đặc biệt");
        assertEquals("redirect:/register", result);
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        User user = new User();
        user.setUserName("ân");
        user.setEmail("an509@gmail.com");
        user.setPhone("0392956578");
        user.setAddress("Quận 12, TP.HCM");
        user.setPassword("123");

        when(userDAO.existsByUserName("ân")).thenReturn(true);

        String result = registerController.registerUser(user, redirectAttributes);

        verify(userDAO).existsByUserName("ân");
        verify(userDAO, never()).save(any(User.class));
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Tên người dùng đã tồn tại!");
        assertEquals("redirect:/register", result);
    }
}