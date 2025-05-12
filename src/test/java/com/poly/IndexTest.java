package com.poly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poly.controller.ProductController;
import com.poly.dao.CategoryDAO;
import com.poly.dao.ProductDAO;
import com.poly.dao.ProductSizeDAO;
import com.poly.dao.SizeDAO;
import com.poly.model.Category;
import com.poly.model.Product;
import com.poly.model.ProductSize;
import com.poly.model.Size;

@SpringBootTest
public class IndexTest {

    private MockMvc mockMvc;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }
    
    @Test
    @DisplayName("TC_09: Kiểm tra load dữ liệu cho sản phẩm")
    public void testListProductsForHomePage() throws Exception {
        // Arrange
        List<Product> productList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setProductId(i);
            product.setProductName("Product " + i);
            product.setPrice(100.0 * i);
            product.setImage("image" + i + ".jpg");
            
            Category category = new Category();
            category.setCategoryId(1);
            category.setCategoryName("Category 1");
            product.setCategory(category);
            
            productList.add(product);
        }
        
        Page<Product> productPage = new PageImpl<>(productList, PageRequest.of(0, 8), productList.size());
        
        when(productDAO.findAll(any(PageRequest.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("productPage"))
                .andExpect(model().attribute("productPage", productPage));

        verify(productDAO, times(1)).findAll(any(PageRequest.class));
    }
    
    @Test
    @DisplayName("TC_09: Kiểm tra các chức năng tìm kiếm sản phẩm")
    public void testSearchProducts() throws Exception {
        // Arrange
        String keyword = "Product 1";
        List<Product> searchResults = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Product 1");
        product.setPrice(100.0);
        product.setImage("image1.jpg");
        
        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Category 1");
        product.setCategory(category);
        
        searchResults.add(product);

        Page<Product> productPage = new PageImpl<>(searchResults, PageRequest.of(0, 8), 1);
        
        when(productDAO.findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/index")
                .param("keyword", keyword)
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("productPage"))
                .andExpect(model().attribute("productPage", productPage))
                .andExpect(model().attribute("keyword", keyword));

        verify(productDAO, times(1)).findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class));
    }
    
    @Test
    @DisplayName("TC_10: Kiểm tra chức năng tìm kiếm sản phẩm - Nhập từ khóa có giá trị")
    public void testSearchProductsWithValidKeyword() throws Exception {
        // Arrange
        String keyword = "giày";
        List<Product> searchResults = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Data test Product");
        product.setPrice(100.0);
        product.setImage("image1.jpg");
        searchResults.add(product);

        Page<Product> productPage = new PageImpl<>(searchResults, PageRequest.of(0, 8), 1);
        
        when(productDAO.findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/index")
                .param("keyword", keyword)
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("productPage"))
                .andExpect(model().attribute("productPage", productPage));

        verify(productDAO, times(1)).findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class));
    }
    
    @Test
    @DisplayName("TC_10: Kiểm tra chức năng tìm kiếm sản phẩm")
    public void testSearchProductsWithTimKiemKeyword() throws Exception {
        // Arrange
        String keyword = "giày";
        List<Product> searchResults = new ArrayList<>(); // Empty results expected
        Page<Product> productPage = new PageImpl<>(searchResults, PageRequest.of(0, 8), 0);
        
        when(productDAO.findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class))).thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/index")
                .param("keyword", keyword)
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("productPage"))
                .andExpect(model().attribute("productPage", productPage));

        verify(productDAO, times(1)).findByProductNameContainingIgnoreCase(eq(keyword), any(PageRequest.class));
    }
    
    @Test
    @DisplayName("TC_11: Kiểm tra load dữ liệu cho chi tiết sản phẩm")
    public void testProductDetails() throws Exception {
        // Arrange
        int productId = 1;
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Product");
        product.setPrice(100.0);
        product.setDescription("Test Description");
        product.setImage("test.jpg");

        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
        product.setCategory(category);

        List<ProductSize> productSizes = new ArrayList<>();
        Size size1 = new Size();
        size1.setId(1);
        size1.setSizeName("S");
        
        ProductSize productSize1 = new ProductSize();
        productSize1.setProduct(product);
        productSize1.setSize(size1);
        productSize1.setStockQuantity(10);
        productSizes.add(productSize1);

        when(productDAO.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeDAO.findByProduct(product)).thenReturn(productSizes);

        // Act & Assert
        mockMvc.perform(get("/product-detail/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("product/detail"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("productSizes"))
                .andExpect(model().attribute("productSizes", productSizes));

        verify(productDAO, times(1)).findById(productId);
        verify(productSizeDAO, times(1)).findByProduct(product);
    }
    
    @Test
    @DisplayName("TC_11: Kiểm tra chi tiết sản phẩm bất kỳ")
    public void testProductDetailsForRandomProduct() throws Exception {
        // Arrange
        int productId = 5; // Random product ID
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Random Product");
        product.setPrice(250.0);
        product.setDescription("Some description for random product");
        product.setImage("random.jpg");

        Category category = new Category();
        category.setCategoryId(2);
        category.setCategoryName("Random Category");
        product.setCategory(category);

        List<ProductSize> productSizes = new ArrayList<>();
        Size size1 = new Size();
        size1.setId(1);
        size1.setSizeName("S");
        
        Size size2 = new Size();
        size2.setId(2);
        size2.setSizeName("M");
        
        ProductSize productSize1 = new ProductSize();
        productSize1.setProduct(product);
        productSize1.setSize(size1);
        productSize1.setStockQuantity(5);
        productSizes.add(productSize1);
        
        ProductSize productSize2 = new ProductSize();
        productSize2.setProduct(product);
        productSize2.setSize(size2);
        productSize2.setStockQuantity(8);
        productSizes.add(productSize2);

        when(productDAO.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeDAO.findByProduct(product)).thenReturn(productSizes);

        // Act & Assert
        mockMvc.perform(get("/product-detail/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("product/detail"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("productSizes"))
                .andExpect(model().attribute("productSizes", productSizes));

        verify(productDAO, times(1)).findById(productId);
        verify(productSizeDAO, times(1)).findByProduct(product);
    }
    
    @Test
    @DisplayName("TC_11: Kiểm tra thông tin chi tiết của sản phẩm")
    public void testProductDetailsInformation() throws Exception {
        // Arrange
        int productId = 1;
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("Test Detail Product");
        product.setPrice(150.0);
        product.setDescription("Detailed product description for testing");
        product.setImage("detail.jpg");

        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Test Category");
        product.setCategory(category);

        List<ProductSize> productSizes = new ArrayList<>();
        Size size1 = new Size();
        size1.setId(1);
        size1.setSizeName("S");
        
        Size size2 = new Size();
        size2.setId(2);
        size2.setSizeName("M");
        
        Size size3 = new Size();
        size3.setId(3);
        size3.setSizeName("L");
        
        ProductSize productSize1 = new ProductSize();
        productSize1.setProduct(product);
        productSize1.setSize(size1);
        productSize1.setStockQuantity(10);
        productSizes.add(productSize1);
        
        ProductSize productSize2 = new ProductSize();
        productSize2.setProduct(product);
        productSize2.setSize(size2);
        productSize2.setStockQuantity(15);
        productSizes.add(productSize2);
        
        ProductSize productSize3 = new ProductSize();
        productSize3.setProduct(product);
        productSize3.setSize(size3);
        productSize3.setStockQuantity(5);
        productSizes.add(productSize3);

        when(productDAO.findById(productId)).thenReturn(Optional.of(product));
        when(productSizeDAO.findByProduct(product)).thenReturn(productSizes);

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/product-detail/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("product/detail"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", product))
                .andExpect(model().attributeExists("productSizes"))
                .andExpect(model().attribute("productSizes", productSizes))
                .andReturn();

        // Verify all product details are passed to the view
        Product modelProduct = (Product) result.getModelAndView().getModel().get("product");
        assertNotNull(modelProduct);
        assertEquals(productId, modelProduct.getProductId());
        assertEquals("Test Detail Product", modelProduct.getProductName());
        assertEquals(150.0, modelProduct.getPrice());
        assertEquals("Detailed product description for testing", modelProduct.getDescription());
        assertEquals("detail.jpg", modelProduct.getImage());
        assertEquals("Test Category", modelProduct.getCategory().getCategoryName());
        
        @SuppressWarnings("unchecked")
        List<ProductSize> modelProductSizes = (List<ProductSize>) result.getModelAndView().getModel().get("productSizes");
        assertNotNull(modelProductSizes);
        assertEquals(3, modelProductSizes.size());
    }
}
