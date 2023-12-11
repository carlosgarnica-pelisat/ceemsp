package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.ArmaDomicilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArmaDomicilioRepository extends JpaRepository<ArmaDomicilio, Integer> {
    List<ArmaDomicilio> getAllByArma(int arma);
}
