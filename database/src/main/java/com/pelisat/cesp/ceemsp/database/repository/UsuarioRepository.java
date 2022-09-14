package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Usuario;
import com.pelisat.cesp.ceemsp.database.type.RolTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario getUsuarioByEmail(String email);
    Usuario getUsuarioByEmailAndEliminadoFalse(String email);
    Usuario getUsuarioByUuidAndEliminadoFalse(String uuid);
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Usuario getUsuarioByCredential(@Param("email") String email, @Param("password") String password);
    List<Usuario> findAllByEliminadoFalse();
    List<Usuario> findAllByRolInAndEliminadoFalse(List<RolTypeEnum> roles);
    Usuario getUsuarioByEmpresaAndEliminadoFalse(int empresa);
}
