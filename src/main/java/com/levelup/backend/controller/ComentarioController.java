package com.levelup.backend.controller;

import com.levelup.backend.entity.Comentario;
import com.levelup.backend.repository.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping
    public List<Comentario> listarComentarios() {
        return comentarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Comentario> guardarComentario(@RequestBody Comentario comentario) {
        Comentario guardado = comentarioRepository.save(comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarComentario(@PathVariable Long id) {
        if (!comentarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comentarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}