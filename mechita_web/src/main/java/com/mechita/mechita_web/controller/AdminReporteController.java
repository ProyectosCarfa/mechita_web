package com.mechita.mechita_web.controller;

import com.mechita.mechita_web.model.Pago;
import com.mechita.mechita_web.model.Pedido;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.PedidoRepository;
import com.mechita.mechita_web.repository.ProductoRepository;
import com.mechita.mechita_web.service.PagoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminReporteController {

    private final PagoService pagoService;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @GetMapping("/reportes")
    public String reportes(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || !usuario.esAdmin()) {
            return "redirect:/login";
        }

        // --- 1. CÁLCULO DE KPIs ---
        BigDecimal totalRecaudado = pagoService.totalRecaudado();
        if (totalRecaudado == null) totalRecaudado = BigDecimal.ZERO;

        model.addAttribute("totalRecaudado", totalRecaudado);
        model.addAttribute("totalPedidos", pedidoRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("stockBajo", productoRepository.findByStockLessThan(10).size());

        // --- 2. DATOS PARA GRÁFICOS ---
        List<Pago> pagos = pagoService.listar();
        List<Pedido> pedidos = pedidoRepository.findAll();

        // Gráfico de Ventas Mensuales
        Map<String, Double> ventasMensuales = new LinkedHashMap<>();
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        for (String m : meses) {
            ventasMensuales.put(m, 0.0); // Usamos Double
        }

        if (pagos != null) {
            for (Pago p : pagos) {
                if (p.getFechaPago() != null && p.getMonto() != null) {
                    int mesIndex = p.getFechaPago().getMonthValue() - 1;
                    String mesNombre = meses[mesIndex];
                    ventasMensuales.merge(mesNombre, p.getMonto().doubleValue(), Double::sum);
                }
            }
        }

        // Gráfico de Métodos de Pago
        Map<String, Long> metodosPagoStats = new HashMap<>();
        if (pagos != null) {
            metodosPagoStats = pagos.stream()
                    .filter(p -> p.getMetodoPago() != null)
                    .collect(Collectors.groupingBy(p -> p.getMetodoPago().getNombre(), Collectors.counting()));
        }

        // Top 5 Productos más vendidos
        Map<String, Integer> topProductosMap = new HashMap<>();
        if (pedidos != null) {
            for (Pedido pedido : pedidos) {
                if (pedido.getDetalles() != null) {
                    pedido.getDetalles().forEach(d -> {
                        if(d.getProducto() != null) {
                            topProductosMap.merge(d.getProducto().getNombre(), d.getCantidad(), Integer::sum);
                        }
                    });
                }
            }
        }
        List<Map.Entry<String, Integer>> topProductos = topProductosMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Enviamos los datos como Listas limpias
        model.addAttribute("ventasLabels", new ArrayList<>(ventasMensuales.keySet()));
        model.addAttribute("ventasData", new ArrayList<>(ventasMensuales.values()));
        model.addAttribute("metodosLabels", new ArrayList<>(metodosPagoStats.keySet()));
        model.addAttribute("metodosData", new ArrayList<>(metodosPagoStats.values()));
        model.addAttribute("topProductos", topProductos);

        return "roles/reportes";
    }
}