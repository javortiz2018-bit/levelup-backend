package com.levelup.backend.controller;

import com.levelup.backend.config.JwtUtil;
import com.levelup.backend.entity.Usuario;
import com.levelup.backend.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void asegurarCredencialesBase() {
        // 1. Administrador (rol 1)
        usuarioRepository.findByEmail("admin@levelup.cl").ifPresent(admin -> {
            boolean requiereActualizar = false;

            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("admin123"));
                requiereActualizar = true;
            }
            if (admin.getRol() == null || admin.getRol() != 1) {
                admin.setRol(1);
                requiereActualizar = true;
            }
            if (admin.getActivo() == null || !admin.getActivo()) {
                admin.setActivo(true);
                requiereActualizar = true;
            }
            if (requiereActualizar) {
                usuarioRepository.save(admin);
            }
        });

        // 2. Vendedor de prueba (rol 3) si aún no existe en la BD
        if (!usuarioRepository.existsByEmail("vendedor@levelup.cl")) {
            Usuario vendedor = new Usuario();
            vendedor.setNombre("Vendedor Level-Up");
            vendedor.setEmail("vendedor@levelup.cl");
            vendedor.setPassword(passwordEncoder.encode("vendedor123"));
            vendedor.setRol(3);
            vendedor.setActivo(true);
            usuarioRepository.save(vendedor);
        }
    }

    private Map<String, Object> construirRespuestaAuth(Usuario usuario) {
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idUsuario", usuario.getIdUsuario());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("email", usuario.getEmail());
        respuesta.put("rol", usuario.getRol());
        respuesta.put("activo", usuario.getActivo());
        respuesta.put("token", token);
        return respuesta;
    }

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
                return ResponseEntity.ok(construirRespuestaAuth(usuario));
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
        nuevoUsuario.setRol(2); // Cliente
        nuevoUsuario.setActivo(true);

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(construirRespuestaAuth(guardado));
    }
}