package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.NotificacionArgos;
import com.pelisat.cesp.ceemsp.database.type.NotificacionArgosTipoEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionArgosRepository extends JpaRepository<NotificacionArgos, Integer> {
    List<NotificacionArgos> getAllByUsuarioAndEliminadoFalse(int usuario);
    List<NotificacionArgos> getAllByTipoAndEliminadoFalse(NotificacionArgosTipoEnum tipo);
    NotificacionArgos getByUuidAndEliminadoFalse(String uuid);
}
