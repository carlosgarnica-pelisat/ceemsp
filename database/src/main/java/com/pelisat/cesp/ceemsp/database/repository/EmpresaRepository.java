package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Empresa;
import com.pelisat.cesp.ceemsp.database.type.EmpresaStatusEnum;
import com.pelisat.cesp.ceemsp.database.type.TipoTramiteEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    List<Empresa> getAllByEliminadoFalse();
    Empresa getByUuidAndEliminadoFalse(String uuid);
    Empresa findFirstByTipoTramiteOrderByFechaCreacionDesc(TipoTramiteEnum tipoTramiteEnum);
    Empresa getByRfcAndEliminadoFalse(String rfc);
    Empresa getByCurpAndEliminadoFalse(String curp);

    Integer countAllByStatusAndEliminadoFalse(EmpresaStatusEnum status);
    Integer countAllByTipoTramiteAndEliminadoFalse(TipoTramiteEnum tipoTramiteEnum);
    Integer countAllByEliminadoFalse();
}
