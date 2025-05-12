package com.poly;

import com.poly.controller.UserController;
import com.poly.dao.OrderDAO;
import com.poly.dao.UserDAO;
import com.poly.model.Order;
import com.poly.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserManagementTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_22 - Kiểm tra load dữ liệu người dùng")
    public void testListUsers() {
        // Arrange
        List<User> mockUsers = new ArrayList<>();
        User testUser = new User();
        testUser.setUserName("testuser");
        mockUsers.add(testUser);
        when(userDAO.findAll()).thenReturn(mockUsers);

        // Act
        String viewName = userController.listUsers(model);

        // Assert
        verify(userDAO).findAll();
        verify(model).addAttribute("users", mockUsers);
        assertEquals("user/list", viewName);
    }

    @Test
    @DisplayName("TC_23 - Kiểm tra thêm mới người dùng khi nhập dữ liệu hợp lệ")
    public void testSaveUser() {
        // Arrange
        User testUser = new User();
        testUser.setUserName("vũ");
        testUser.setPassword("0909");
        testUser.setFullName("Nguyễn Vũ");
        testUser.setEmail("vu@mail.com");
        testUser.setPhone("0555627138");
        testUser.setAddress("Quận 12, TP.HCM");

        // Act
        String viewName = userController.saveUser(testUser, redirectAttributes);

        // Assert
        verify(userDAO).save(testUser);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Thêm người dùng thành công!");
        assertEquals("redirect:/users", viewName);
    }

    @Test
    @DisplayName("TC_25 - Kiểm tra chức năng sửa thông tin người dùng")
    public void testUpdateUser() {
        // Arrange
        User testUser = new User();
        testUser.setUserName("ân");
        testUser.setPassword("123");
        testUser.setFullName("Ngô Hải Ân");
        testUser.setEmail("an059@gmail.com");
        testUser.setPhone("0392956578");
        testUser.setAddress("Quận 12, TP.HCM");

        // Act
        String viewName = userController.updateUser(testUser, redirectAttributes);

        // Assert
        verify(userDAO).save(testUser);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Cập nhật người dùng thành công!");
        assertEquals("redirect:/users", viewName);
    }

    @Test
    @DisplayName("TC_26 - Kiểm tra chức năng xóa thông tin người dùng")
    public void testDeleteUserSuccess() {
        // Arrange
        Integer userId = 1;
        User testUser = new User();
        testUser.setUserId(userId);
        
        when(userDAO.findById(userId)).thenReturn(Optional.of(testUser));
        when(orderDAO.findByUser(testUser)).thenReturn(new ArrayList<>()); // Không có đơn hàng

        // Act
        String viewName = userController.deleteUser(userId, redirectAttributes);

        // Assert
        verify(userDAO).findById(userId);
        verify(orderDAO).findByUser(testUser);
        verify(userDAO).deleteById(userId);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        assertEquals("redirect:/users", viewName);
    }
}