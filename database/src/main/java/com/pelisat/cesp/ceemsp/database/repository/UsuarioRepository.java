package com.pelisat.cesp.ceemsp.database.repository;

import com.pelisat.cesp.ceemsp.database.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Usuario getUsuarioByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Usuario getUsuarioByCredential(@Param("email") String email, @Param("password") String password);
}
