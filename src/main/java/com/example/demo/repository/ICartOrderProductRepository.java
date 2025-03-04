package com.example.demo.repository;

import com.example.demo.model.entity.CartOrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartOrderProductRepository extends JpaRepository<CartOrderProductEntity, Long> {


}
