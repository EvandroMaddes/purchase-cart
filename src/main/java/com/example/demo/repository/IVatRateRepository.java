package com.example.demo.repository;

import com.example.demo.model.entity.VatRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVatRateRepository extends JpaRepository<VatRateEntity, Long> {
}
