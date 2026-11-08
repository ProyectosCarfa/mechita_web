package com.mechita.mechita_web.service;

import com.mechita.mechita_web.model.Cliente;
import com.mechita.mechita_web.model.Direccion;
import com.mechita.mechita_web.model.Usuario;
import com.mechita.mechita_web.repository.ClienteRepository;
import com.mechita.mechita_web.repository.DireccionRepository;
import com.mechita.mechita_web.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;

    @Transactional
    public Cliente registrarCliente(Usuario usuario, Cliente cliente, Direccion direccion) {
        // La contraseña ya viene encriptada desde el AuthController
        Usuario guardado = usuarioRepository.save(usuario);
        cliente.setUsuario(guardado);
        Cliente clienteGuardado = clienteRepository.save(cliente);
        direccion.setCliente(clienteGuardado);
        direccionRepository.save(direccion);
        return clienteGuardado;
    }
}