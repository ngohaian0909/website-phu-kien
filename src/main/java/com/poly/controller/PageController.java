package com.poly.controller;

import com.poly.dao.ProductDAO;
import com.poly.dao.SizeDAO;
import com.poly.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @Autowired
    private ProductDAO productDAO;
    
    @GetMapping("/index")
    public String showProducts(Model model,
                               @RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "keyword", required = false) String keyword) { // Thêm tham số keyword

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Product> productPage;

        if (keyword != null && !keyword.isEmpty()) {
            // Tìm kiếm theo keyword
            productPage = productDAO.findByProductNameContainingIgnoreCase(keyword, pageable); // Hoặc một phương thức tìm kiếm khác phù hợp
            model.addAttribute("keyword", keyword); // Lưu keyword để hiển thị lại trong form tìm kiếm
        
        	
        }
        else {
            // Hiển thị tất cả sản phẩm
            productPage = productDAO.findAll(pageable);
            
        }
     
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);

        return "index";
    }
    @GetMapping("/info")
	public String showInfoForm() {
		return "info";
	}
}	