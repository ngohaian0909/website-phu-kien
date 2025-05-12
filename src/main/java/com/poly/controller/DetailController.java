package com.poly.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.poly.dao.ProductDAO;
import com.poly.dao.ProductSizeDAO;
import com.poly.dao.SizeDAO;
import com.poly.model.Product;
import com.poly.model.ProductSize;
import com.poly.model.Size;

import jakarta.servlet.http.HttpSession;

@Controller
public class DetailController {

    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private SizeDAO sizeDAO;
    @Autowired
    private ProductSizeDAO productSizeDAO;
    @GetMapping("/product-detail/{id}")
    public String productDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        Product product = productDAO.findById(id).orElse(null);
        if (product == null) {
            return "error/productNotFound";
        }

        // Lấy danh sách tất cả size có trong hệ thống
        List<Size> allSizes = sizeDAO.findAll();

        // Lấy danh sách size của sản phẩm từ bảng ProductSizes
        List<ProductSize> productSizes = productSizeDAO.findByProduct(product);

        // Tạo Map để lưu stock_quantity của từng size
        Map<Integer, Integer> sizeStockMap = new HashMap<>();
        for (Size size : allSizes) {
            sizeStockMap.put(size.getId(), 0);  // Mặc định size không có trong ProductSizes sẽ có stock = 0
        }
        for (ProductSize ps : productSizes) {
            sizeStockMap.put(ps.getSize().getId(), ps.getStockQuantity());
        }

        // Đưa dữ liệu xuống View
        model.addAttribute("product", product);
        model.addAttribute("sizes", allSizes);
        model.addAttribute("sizeStockMap", sizeStockMap);  // Map chứa stock của từng size

        // Lấy danh sách sản phẩm cùng danh mục
        List<Product> relatedProducts = productDAO.findByCategory(product.getCategory());
        model.addAttribute("relatedProducts", relatedProducts);

        // Lưu lại URL sản phẩm chi tiết
        session.setAttribute("lastProductDetailUrl", "/product-detail/" + id);
        
        return "product-detail";
    }


}