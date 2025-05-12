package com.poly.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.model.Product;
import com.poly.model.Category;
import java.util.List;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {
    
    // Phân trang danh sách sản phẩm
    Page<Product> findAll(Pageable pageable);

    // Tìm kiếm sản phẩm theo tên (không phân biệt hoa thường)
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    // Tìm sản phẩm theo danh mục
    List<Product> findByCategory(Category category);
}
