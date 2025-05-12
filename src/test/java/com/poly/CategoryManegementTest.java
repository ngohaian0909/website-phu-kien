package com.poly;

import com.poly.controller.CategoryController;
import com.poly.dao.CategoryDAO;
import com.poly.model.Category;
import com.poly.model.Product;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CategoryManegementTest {

    @Mock
    private CategoryDAO categoryDAO;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Kiểm tra load dữ liệu danh mục
    @Test
    @DisplayName("Kiểm tra load dữ liệu danh mục")
    public void testLoadCategoryData() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Kính", null));
        categories.add(new Category(2, "Phụ kiện", null));
        
        when(categoryDAO.findAll()).thenReturn(categories);
        
        // Act
        String viewName = categoryController.listCategories(model);
        
        // Assert
        verify(categoryDAO).findAll();
        verify(model).addAttribute("categories", categories);
        assertEquals("category/list", viewName);
    }

    // 2. Kiểm tra chức năng thêm mới danh mục
    @Test
    @DisplayName("Kiểm tra chức năng thêm mới danh mục")
    public void testAddNewCategory() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setCategoryName("Kính");
        
        // Act - Test show create form
        String createFormView = categoryController.showCreateForm(model);
        
        // Assert - Verify create form is shown
        verify(model).addAttribute(eq("category"), any(Category.class));
        assertEquals("category/create", createFormView);
        
        // Act - Test save category
        String redirectView = categoryController.saveCategory(newCategory, redirectAttributes);
        
        // Assert - Verify category is saved
        verify(categoryDAO).save(newCategory);
        verify(redirectAttributes).addFlashAttribute("message", "Thêm danh mục thành công!");
        assertEquals("redirect:/category/list", redirectView);
    }

    // 3. Kiểm tra chức năng xóa thông tin danh mục
    @Test
    @DisplayName("Kiểm tra chức năng xóa thông tin danh mục")
    public void testDeleteCategory() {
        // Arrange - Category without products (can be deleted)
        Integer deleteId = 1;
        Category categoryToDelete = new Category(deleteId, "Kính", new ArrayList<>());
        when(categoryDAO.findById(deleteId)).thenReturn(Optional.of(categoryToDelete));
        
        // Act
        String redirectView = categoryController.deleteCategory(deleteId, redirectAttributes);
        
        // Assert
        verify(categoryDAO).findById(deleteId);
        verify(categoryDAO).delete(categoryToDelete);
        verify(redirectAttributes).addFlashAttribute("message", "Xóa danh mục thành công!");
        assertEquals("redirect:/category/list", redirectView);
        
        // Arrange - Category with products (cannot be deleted)
        Integer categoryWithProductsId = 2;
        List<Product> products = new ArrayList<>();
        products.add(new Product()); // Add dummy product
        Category categoryWithProducts = new Category(categoryWithProductsId, "Phụ kiện", products);
        when(categoryDAO.findById(categoryWithProductsId)).thenReturn(Optional.of(categoryWithProducts));
        
        // Act
        String redirectView2 = categoryController.deleteCategory(categoryWithProductsId, redirectAttributes);
        
        // Assert
        verify(categoryDAO).findById(categoryWithProductsId);
        verify(categoryDAO, never()).delete(categoryWithProducts);
        verify(redirectAttributes).addFlashAttribute("error", "Danh mục này có sản phẩm, không thể xóa!");
        assertEquals("redirect:/category/list", redirectView2);
    }

    // 4. Kiểm tra chức năng sửa thông tin danh mục
    @Test
    @DisplayName("Kiểm tra chức năng sửa thông tin danh mục")
    public void testEditCategoryInformation() {
        // Arrange
        Integer categoryId = 1;
        Category existingCategory = new Category(categoryId, "Kính", null);
        Category updatedCategory = new Category(categoryId, "Kính mát", null);
        
        when(categoryDAO.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        
        // Act - Test show edit form
        String editFormView = categoryController.showEditForm(categoryId, model);
        
        // Assert - Verify edit form is shown
        verify(categoryDAO).findById(categoryId);
        verify(model).addAttribute("category", existingCategory);
        assertEquals("category/edit", editFormView);
        
        // Act - Test update category
        String redirectView = categoryController.updateCategory(updatedCategory, redirectAttributes);
        
        // Assert - Verify category is updated
        verify(categoryDAO).save(updatedCategory);
        verify(redirectAttributes).addFlashAttribute("message", "Cập nhật danh mục thành công!");
        assertEquals("redirect:/category/list", redirectView);
    }
}
