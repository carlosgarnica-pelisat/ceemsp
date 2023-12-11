package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.NotificacionEmpresa;
import com.pelisat.cesp.ceemsp.database.type.NotificacionEmpresaTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionEmpresaRepository extends JpaRepository<NotificacionEmpresa, Integer> {
    List<NotificacionEmpresa> getAllByEmpresaAndEliminadoFalse(int usuario);
    List<NotificacionEmpresa> getAllByTipoAndEliminadoFalse(NotificacionEmpresaTipoEnum tipo);
    NotificacionEmpresa getByUuidAndEliminadoFalse(String uuid);
}
