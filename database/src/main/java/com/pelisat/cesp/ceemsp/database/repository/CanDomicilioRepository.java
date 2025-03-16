package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.CanDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanDomicilioRepository extends JpaRepository<CanDomicilio, Integer> {
    List<CanDomicilio> getAllByCan(int can);
}
