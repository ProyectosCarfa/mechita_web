package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivo(Boolean activo);

    List<Producto> findByCategoria_IdCategoria(Long idCategoria);

    List<Producto> findByCategoria_IdCategoriaAndActivo(Long idCategoria, Boolean activo);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByStockLessThan(Integer stock);

    long countByActivo(Boolean activo);

    long countByCategoria_IdCategoriaAndActivo(Long idCategoria, Boolean activo);
}