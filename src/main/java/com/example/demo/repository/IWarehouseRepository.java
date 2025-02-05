package com.example.demo.repository;

import com.example.demo.model.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface IWarehouseRepository extends JpaRepository<WarehouseEntity, Long> {

    Optional<WarehouseEntity> findByProduct_Id(Serializable product_id);
}
