package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivo(Boolean activo);

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    long countByActivo(Boolean activo);
}