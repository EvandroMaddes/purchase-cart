package com.example.demo.repository;

import com.example.demo.model.entity.CartOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartOrderRepository extends JpaRepository<CartOrderEntity, Long> {


}
