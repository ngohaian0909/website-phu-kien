package com.poly;

import com.poly.controller.OrderHistoryController;
import com.poly.dao.OrderDetailDAO;
import com.poly.model.Order;
import com.poly.model.OrderDetail;
import com.poly.model.Product;
import com.poly.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderHistoryTest {

    @Mock
    private OrderDetailDAO orderDetailDAO;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private OrderHistoryController orderHistoryController;

    private User testUser;
    private List<OrderDetail> mockOrderDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Create test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");
        
        // Create mock order details
        mockOrderDetails = new ArrayList<>();
        
        // Create order
        Order order = new Order();
        order.setOrderID(1);
        order.setUser(testUser);
        
        // Create product
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Áo thun nam đơn giản");
        product.setPrice(100000.0);
        
        // Create order detail
        OrderDetail detail = new OrderDetail();
        detail.setOrderDetailID(1);
        detail.setOrder(order);
        detail.setProduct(product);
        detail.setQuantity(2);
        detail.setPrice(100000.0);
        
        mockOrderDetails.add(detail);
    }

    @Test
    public void testLoadOrderInfoWhenLoggedIn() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(testUser);
        when(orderDetailDAO.findByOrder_User(testUser)).thenReturn(mockOrderDetails);
        
        // Act
        String viewName = orderHistoryController.showOrderHistory(session, model);
        
        // Assert
        assertEquals("customer/order-history", viewName);
        verify(model).addAttribute("orderDetails", mockOrderDetails);
        verify(orderDetailDAO).findByOrder_User(testUser);
    }
}
