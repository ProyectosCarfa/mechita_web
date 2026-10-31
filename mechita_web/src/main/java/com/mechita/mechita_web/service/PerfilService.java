package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Direccion;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.DireccionRepository;
import com.mechita.mechita_web.repository.DistritoRepository;
import com.mechita.mechita_web.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    public Cliente obtenerPorUsuario(Usuario usuario) {
        return clienteRepository.findByUsuario(usuario).orElse(null);
    }

    public Direccion direccionPrincipal(Cliente cliente) {
        return direccionRepository.findByCliente(cliente).stream()
                .min(Comparator.comparing(Direccion::getIdDireccion))
                .orElse(null);
    }

    public void cargarDatosEnModelo(Usuario usuario, Model model) {
        Optional<Cliente> clienteOpt = clienteRepository.findByUsuario(usuario);
        model.addAttribute("distritos", distritoRepository.findAllByOrderByNombreAsc());

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Direccion> direcciones = direccionRepository.findByCliente(cliente);
            model.addAttribute("cliente", cliente);
            model.addAttribute("pedidos", pedidoRepository.findByClienteOrderByFechaPedidoDesc(cliente));
            model.addAttribute("direcciones", direcciones);
            model.addAttribute("direccionPrincipal", direccionPrincipal(cliente));
        } else {
            model.addAttribute("cliente", null);
            model.addAttribute("pedidos", Collections.emptyList());
            model.addAttribute("direcciones", Collections.emptyList());
            model.addAttribute("direccionPrincipal", null);
        }
    }
}
