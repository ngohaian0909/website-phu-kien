package com.poly.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.model.*;
@Repository
public interface OrderDetailDAO extends JpaRepository<OrderDetail, Integer> {
	List<OrderDetail> findByOrder(Order order);

	List<OrderDetail> findByOrder_User(User user);
	
	List<OrderDetail> findByOrderIn(List<Order> orders);

}
