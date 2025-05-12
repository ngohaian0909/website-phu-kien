package com.poly.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.model.Product;
import com.poly.model.ProductSize;

import jakarta.transaction.Transactional;

@Repository
public interface ProductSizeDAO extends JpaRepository<ProductSize, Integer> {
    
    // Tìm danh sách ProductSize theo Product
    List<ProductSize> findByProduct(Product product);

    // Tìm danh sách ProductSize theo productId
    @Query("SELECT ps FROM ProductSize ps WHERE ps.product.id = :productId")
    List<ProductSize> findByProductId(Integer productId);

    // Xóa ProductSize theo Product
    @Transactional
    @Modifying
    @Query("DELETE FROM ProductSize ps WHERE ps.product = :product")
    void deleteByProduct(Product product);

    Optional<ProductSize> findByProduct_ProductIdAndSize_Id(Integer productId, Integer sizeId);
    

}
