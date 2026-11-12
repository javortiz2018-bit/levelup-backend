package com.levelup.backend.controller;

import com.levelup.backend.entity.Usuario;
import com.levelup.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (!usuario.getActivo()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("mensaje", "Cuenta desactivada"));
            }
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return ResponseEntity.ok(usuario);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Correo o contraseña incorrectos"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario nuevoUsuario) {
        if (usuarioRepository.existsByEmail(nuevoUsuario.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "El correo ya está registrado"));
        }
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        nuevoUsuario.setRol(2); // Siempre se registra como Comprador
        nuevoUsuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}