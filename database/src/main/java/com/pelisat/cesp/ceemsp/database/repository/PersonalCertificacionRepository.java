package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.PersonalCertificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonalCertificacionRepository extends JpaRepository<PersonalCertificacion, Integer> {
    List<PersonalCertificacion> getAllByPersonalAndEliminadoFalse(int personal);
    PersonalCertificacion findByUuidAndEliminadoFalse(String uuid);
}
