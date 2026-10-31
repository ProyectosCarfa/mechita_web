package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.dto.PagoDto;
import com.mechita.mechita_web.dto.PedidoDto;
import com.mechita.mechita_web.service.PagoService;
import com.mechita.mechita_web.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoRestController {

    private final PedidoService pedidoService;
    private final PagoService pagoService;

    public record EstadoRequest(@NotBlank(message = "El estado es obligatorio") String estado) {
    }

    public record PagoRequest(
            @NotNull(message = "El método de pago es obligatorio") Long idMetodoPago,
            @Size(max = 80, message = "La referencia no debe superar los 80 caracteres") String referencia) {
    }

    @GetMapping
    public List<PedidoDto> listar(@RequestParam(required = false) String estado) {
        var pedidos = (estado == null || estado.isBlank())
                ? pedidoService.listar()
                : pedidoService.listarPorEstado(estado);
        return pedidos.stream().map(PedidoDto::de).toList();
    }

    @GetMapping("/{id}")
    public PedidoDto obtener(@PathVariable Long id) {
        return PedidoDto.de(pedidoService.buscar(id));
    }

    @PatchMapping("/{id}/estado")
    public PedidoDto cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return PedidoDto.de(pedidoService.cambiarEstado(id, request.estado()));
    }

    @PostMapping("/{id}/pago")
    public ResponseEntity<PagoDto> pagar(@PathVariable Long id, @Valid @RequestBody PagoRequest request) {
        var pago = pagoService.registrar(id, request.idMetodoPago(), request.referencia());
        return ResponseEntity.status(HttpStatus.CREATED).body(PagoDto.de(pago));
    }
}
