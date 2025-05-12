package com.poly.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.model.*;
@Repository
public interface OrderDAO extends JpaRepository<Order, Integer> {
	List<Order> findByUser(User user);
	
	
	List<Order> findByOrderDate(Date orderDate);
	
	
	@Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.orderID = :id")
    Order findByIdWithUser(@Param("id") Integer id);

	@Query("SELECT o FROM Order o JOIN FETCH o.user") // Câu truy vấn đã được sửa!
    List<Order> findAllWithUsers();
}
