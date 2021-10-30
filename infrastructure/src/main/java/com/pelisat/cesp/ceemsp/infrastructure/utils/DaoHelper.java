package com.pelisat.cesp.ceemsp.infrastructure.utils;

import com.pelisat.cesp.ceemsp.database.model.CommonModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DaoHelper<T extends CommonModel> {
    public void fulfillAuditorFields(boolean createdAlso, T entity, int userId) {
        entity.setActualizadoPor(userId);
        entity.setFechaActualizacion(LocalDateTime.now());
        if(createdAlso) {
            entity.setCreadoPor(userId);
            entity.setFechaCreacion(LocalDateTime.now());
        }
    }
}
