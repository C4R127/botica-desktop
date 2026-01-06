package com.botica.botica_desktop.service;

import com.botica.botica_desktop.entity.Usuario;
import com.botica.botica_desktop.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario guardar(Usuario usuario) {
        // Verificamos si ya existe el usuario
        if (usuario.getId() == null && usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe.");
        }
        return usuarioRepository.save(usuario);
    }

    // 🔹 Ya EXISTÍA (no se toca)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Nuevo método agregado para validar login

    public Optional<Usuario> login(String username, String password) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (usuario.getPassword().equals(password) && usuario.isActivo()) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }


    // 🔹 NUEVO METODO (ESTO SOLUCIONA EL ERROR)
    public boolean validarLogin(String username, String password) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            return usuario.getPassword().equals(password)
                    && usuario.isActivo();
        }

        return false;
    }
}
