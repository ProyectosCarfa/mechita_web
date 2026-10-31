package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistritoRepository extends JpaRepository<Distrito, Long> {
    List<Distrito> findAllByOrderByNombreAsc();
}
