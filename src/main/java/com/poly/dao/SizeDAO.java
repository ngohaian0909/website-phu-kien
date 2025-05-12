package com.poly.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.model.Size;

@Repository
public interface SizeDAO extends JpaRepository<Size, Integer> {
	
}
