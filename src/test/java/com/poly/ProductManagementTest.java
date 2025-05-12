package com.poly;

import com.poly.controller.ProductController;
import com.poly.dao.CategoryDAO;
import com.poly.dao.ProductDAO;
import com.poly.dao.ProductSizeDAO;
import com.poly.dao.SizeDAO;
import com.poly.model.Category;
import com.poly.model.Product;
import com.poly.model.ProductSize;
import com.poly.model.Size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProductManagementTest {

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ProductSizeDAO productSizeDAO;

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private SizeDAO sizeDAO;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("TC_27: Kiểm tra load đủ liệu sản phẩm")
    public void testListProducts() throws Exception {
        // Arrange
        List<Product> products = new ArrayList<>();
        Category category1 = new Category();
        category1.setCategoryId(1);
        category1.setCategoryName("Áo");
        
        Category category2 = new Category();
        category2.setCategoryId(2);
        category2.setCategoryName("Quần");
        
        Product product1 = new Product(1, "Áo mới", "Áo đẹp", "anh1.jpg", 50000.0, category1);
        Product product2 = new Product(2, "Quần jean", "Quần đẹp", "anh2.jpg", 60000.0, category2);
        products.add(product1);
        products.add(product2);

        when(productDAO.findAll()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("products", products));

        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("TC_28: Kiểm tra thêm mới sản phẩm")
    public void testSaveProduct() throws Exception {
        // Arrange
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Áo");
        
        Size size1 = new Size();
        size1.setId(1);
        Size size2 = new Size();
        size2.setId(2);
        
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Áo thun");
        product.setDescription("Áo màu xanh tươi chọn");
        product.setPrice(50000.0);
        product.setImage("anh.jpg");
        product.setCategory(category);
        
        List<Integer> sizeIds = Arrays.asList(1, 2);
        List<Integer> stockQuantities = Arrays.asList(5, 5);
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "anh.jpg", "image/jpeg", "test image content".getBytes());
        
        when(categoryDAO.findById(1)).thenReturn(Optional.of(category));
        when(productDAO.save(any(Product.class))).thenReturn(product);
        when(sizeDAO.findById(1)).thenReturn(Optional.of(size1));
        when(sizeDAO.findById(2)).thenReturn(Optional.of(size2));
        
        // Act
        String viewName = productController.saveProduct(
                "Áo thun", 
                "Áo màu xanh tươi chọn", 
                50000.0, 
                1, 
                sizeIds, 
                stockQuantities, 
                file);
        
        // Assert
        assertEquals("redirect:/product/list", viewName);
        verify(productDAO, times(1)).save(any(Product.class));
        verify(productSizeDAO, times(2)).save(any(ProductSize.class));
    }

    @Test
    @DisplayName("TC_29: Kiểm tra chức năng xóa thông tin sản phẩm")
    public void testDeleteProduct() throws Exception {
        // Arrange
        Integer productId = 1;
        doNothing().when(productDAO).deleteById(productId);

        // Act
        String viewName = productController.deleteProduct(productId, redirectAttributes);

        // Assert
        assertEquals("redirect:/product/list", viewName);
        verify(productDAO, times(1)).deleteById(productId);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), anyString());
    }

    @Test
    @DisplayName("TC_30: Kiểm tra chức năng sửa thông tin sản phẩm")
    public void testEditProduct() throws Exception {
        // Arrange
        int productId = 1;
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Phụ kiện");
        
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Kính");
        product.setDescription("Kính mát");
        product.setPrice(250000.0);
        product.setImage("anh.jpg");
        product.setCategory(category);
        
        Size size1 = new Size();
        size1.setId(1);
        Size size2 = new Size();
        size2.setId(2);
        List<Size> allSizes = Arrays.asList(size1, size2);
        
        ProductSize ps1 = new ProductSize();
        ps1.setId(1);
        ps1.setProduct(product);
        ps1.setSize(size1);
        ps1.setStockQuantity(5);
        
        ProductSize ps2 = new ProductSize();
        ps2.setId(2);
        ps2.setProduct(product);
        ps2.setSize(size2);
        ps2.setStockQuantity(3);
        
        List<ProductSize> productSizes = Arrays.asList(ps1, ps2);
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        
        when(productDAO.findById(productId)).thenReturn(Optional.of(product));
        when(sizeDAO.findAll()).thenReturn(allSizes);
        when(productSizeDAO.findByProduct(product)).thenReturn(productSizes);
        when(categoryDAO.findAll()).thenReturn(categories);
        
        // Act
        String viewName = productController.editProduct(productId, model);
        
        // Assert
        assertEquals("product/edit", viewName);
        verify(productDAO, times(1)).findById(productId);
        verify(sizeDAO, times(1)).findAll();
        verify(productSizeDAO, times(1)).findByProduct(product);
        verify(categoryDAO, times(1)).findAll();
        
        verify(model, times(1)).addAttribute(eq("product"), eq(product));
        verify(model, times(1)).addAttribute(eq("sizes"), eq(allSizes));
        verify(model, times(1)).addAttribute(eq("categories"), eq(categories));
        verify(model, times(1)).addAttribute(eq("sizeQuantityMap"), any(Map.class));
    }
}