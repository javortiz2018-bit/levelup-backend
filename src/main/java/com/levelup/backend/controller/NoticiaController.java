package com.levelup.backend.controller;

import com.levelup.backend.entity.Noticia;
import com.levelup.backend.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaRepository noticiaRepository;

    @GetMapping
    public List<Noticia> listarNoticias() {
        return noticiaRepository.findAllByOrderByFechaPublicacionDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Noticia> obtenerNoticia(@PathVariable Long id) {
        return noticiaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Noticia> crearNoticia(@RequestBody Noticia noticia) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticiaRepository.save(noticia));
    }
}