package com.levelup.backend.controller;

import com.levelup.backend.entity.Producto;
import com.levelup.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<Producto> listarPorCategoria(@PathVariable Long idCategoria) {
        return productoRepository.findByCategoriaIdCategoriaAndActivoTrue(idCategoria);
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        producto.setActivo(true);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoRepository.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto datos) {
        return productoRepository.findById(id).map(prod -> {
            prod.setNombre(datos.getNombre());
            prod.setDescripcion(datos.getDescripcion());
            prod.setPrecio(datos.getPrecio());
            prod.setStock(datos.getStock());
            prod.setImagen(datos.getImagen());
            prod.setCategoria(datos.getCategoria());
            if (datos.getActivo() != null) {
                prod.setActivo(datos.getActivo());
            }
            return ResponseEntity.ok(productoRepository.save(prod));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Borrado lógico para no romper el historial de boletas
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Long id) {
        return productoRepository.findById(id).map(prod -> {
            prod.setActivo(false);
            productoRepository.save(prod);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}