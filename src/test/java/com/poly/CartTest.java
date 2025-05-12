package com.poly;

import com.poly.model.*;
import com.poly.controller.CartController;
import com.poly.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ProductSizeDAO productSizeDAO;

    @Mock
    private SizeDAO sizeDAO;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    private User testUser;
    private Product testProduct;
    private Size testSize;
    private ProductSize testProductSize;
    private Cart testCart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("testuser");

        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductName("Áo thun nam đơn giản");
        testProduct.setPrice(100.0);

        testSize = new Size();
        testSize.setId(1);
        testSize.setSizeName("M");

        testProductSize = new ProductSize();
        testProductSize.setProduct(testProduct);
        testProductSize.setSize(testSize);
        testProductSize.setStockQuantity(10);

        testCart = new Cart();
        testCart.setCartID(1);
        testCart.setUser(testUser);
        testCart.setProduct(testProduct);
        testCart.setSize(testSize);
        testCart.setQuantity(1);
    }

    /**
     * TC_14: Kiểm tra chức năng thêm sản phẩm vào giỏ hàng
     */
    @Test
    public void testAddToCart_Success() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(testUser);
        when(productDAO.findById(1)).thenReturn(Optional.of(testProduct));
        when(sizeDAO.findById(1)).thenReturn(Optional.of(testSize));
        when(productSizeDAO.findByProduct_ProductIdAndSize_Id(1, 1)).thenReturn(Optional.of(testProductSize));
        when(cartDAO.findByUserAndProductAndSize(testUser, testProduct, testSize)).thenReturn(null);

        // Act
        String result = cartController.addToCart(1, 1, 1, session, redirectAttributes);

        // Assert
        verify(cartDAO, times(1)).save(any(Cart.class));
        verify(redirectAttributes).addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
        assertEquals("redirect:/cart", result);
    }

    /**
     * Kiểm tra chức năng xóa sản phẩm khỏi giỏ hàng
     */
    @Test
    public void testRemoveFromCart_Success() {
        // Arrange
        when(session.getAttribute("user")).thenReturn(testUser);
        when(cartDAO.findById(1)).thenReturn(Optional.of(testCart));

        // Act
        String result = cartController.removeFromCart(1, session, redirectAttributes);

        // Assert
        verify(cartDAO).delete(testCart);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        assertEquals("redirect:/cart", result);
    }
}
