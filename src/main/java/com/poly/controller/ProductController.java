package com.poly.controller;

import com.poly.dao.CategoryDAO;
import com.poly.dao.ProductDAO;
import com.poly.dao.ProductSizeDAO;
import com.poly.dao.SizeDAO;
import com.poly.model.Category;
import com.poly.model.Product;
import com.poly.model.ProductSize;
import com.poly.model.Size;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/images/";

    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private ProductSizeDAO productSizeDAO;
    @Autowired
    private CategoryDAO categoryDAO;
    
    @Autowired
    private SizeDAO sizeDAO;
    @GetMapping("/list")
    public String listProducts(Model model) {
        List<Product> products = productDAO.findAll();
        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories",categoryDAO.findAll());
     // Lấy danh sách size từ database
        List<Size> sizes = sizeDAO.findAll();
        model.addAttribute("sizes", sizes); // Load danh sách size
        return "product/create";
    }

    @PostMapping("/save")
    public String saveProduct(
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("sizeIds") List<Integer> sizeIds,
            @RequestParam("stockQuantities") List<Integer> stockQuantities,
            @RequestParam("file") MultipartFile file) {

        // Lưu sản phẩm
        Product product = new Product();
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(categoryDAO.findById(categoryId).orElse(null));

        // Xử lý lưu ảnh
        String fileName = file.getOriginalFilename();
        product.setImage(fileName);
        Product savedProduct = productDAO.save(product);

        // Lưu từng size và số lượng vào ProductSizes
        for (int i = 0; i < sizeIds.size(); i++) {
            ProductSize productSize = new ProductSize();
            productSize.setProduct(savedProduct);
            productSize.setSize(sizeDAO.findById(sizeIds.get(i)).orElse(null));
            productSize.setStockQuantity(stockQuantities.get(i));
            productSizeDAO.save(productSize);
        }

        return "redirect:/product/list";
    }
    @PostMapping("/update")
    public String updateProduct(
            @RequestParam("productId") Integer productId,
            @RequestParam("productName") String productName,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("sizeIds") List<Integer> sizeIds,
            @RequestParam("stockQuantities") List<Integer> stockQuantities,
            @RequestParam("file") MultipartFile file) {

        Optional<Product> productOptional = productDAO.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setProductName(productName);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(categoryDAO.findById(categoryId).orElse(null));

            // Xử lý cập nhật ảnh nếu có ảnh mới
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                product.setImage(fileName);
            }

            Product updatedProduct = productDAO.save(product);

            // Xóa các ProductSize cũ
            productSizeDAO.deleteByProduct(updatedProduct);

            // Cập nhật danh sách ProductSize mới
            for (int i = 0; i < sizeIds.size(); i++) {
                ProductSize productSize = new ProductSize();
                productSize.setProduct(updatedProduct);
                productSize.setSize(sizeDAO.findById(sizeIds.get(i)).orElse(null));
                productSize.setStockQuantity(stockQuantities.get(i));
                productSizeDAO.save(productSize);
            }
        }

        return "redirect:/product/list";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") int productId, Model model) {
        // Lấy thông tin sản phẩm cần chỉnh sửa
        Product product = productDAO.findById(productId)
                              .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

        // Lấy danh sách tất cả kích thước có trong hệ thống
        List<Size> allSizes = sizeDAO.findAll();

        // Lấy danh sách kích thước đã được thêm vào sản phẩm này
        List<ProductSize> productSizes = productSizeDAO.findByProduct(product);

        // Tạo danh sách kích thước kèm số lượng (nếu chưa có thì mặc định là 0)
        Map<Integer, Integer> sizeQuantityMap = new HashMap<>();
        for (Size size : allSizes) {
            sizeQuantityMap.put(size.getId(), 0); // Mặc định số lượng là 0
        }
        for (ProductSize ps : productSizes) {
            sizeQuantityMap.put(ps.getSize().getId(), ps.getStockQuantity()); // Cập nhật số lượng thực tế
        }

        // Truyền dữ liệu xuống View
        model.addAttribute("product", product);
        model.addAttribute("sizes", allSizes);
        model.addAttribute("sizeQuantityMap", sizeQuantityMap);
        model.addAttribute("categories", categoryDAO.findAll());

        return "product/edit";  // Trả về trang chỉnh sửa sản phẩm
    }



    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productDAO.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm!");
        }
        return "redirect:/product/list";
    }



}
