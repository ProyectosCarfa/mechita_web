package com.mechita.mechita_web.repository;

import com.mechita.mechita_web.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findAllByOrderByFechaPagoDesc();

    @Query("select coalesce(sum(p.monto), 0) from Pago p")
    BigDecimal totalRecaudado();
}
