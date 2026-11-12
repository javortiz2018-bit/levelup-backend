package com.levelup.backend.controller;

import com.levelup.backend.entity.Boleta;
import com.levelup.backend.entity.DetalleBoleta;
import com.levelup.backend.entity.Producto;
import com.levelup.backend.repository.BoletaRepository;
import com.levelup.backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boletas")
public class BoletaController {

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Boleta> listarPorUsuario(@PathVariable Long idUsuario) {
        return boletaRepository.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarCompra(@RequestBody Boleta boleta) {
        if (boleta.getDetalles() == null || boleta.getDetalles().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "La boleta debe contener al menos un producto"));
        }

        // Validar stock disponible antes de guardar (el trigger de MySQL descontará el stock automáticamente)
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            Producto productoDB = productoRepository.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (productoDB.getStock() < detalle.getCantidad()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "Stock insuficiente para: " + productoDB.getNombre()));
            }
            detalle.setBoleta(boleta);
            detalle.setPrecioUnitario(productoDB.getPrecio());
            detalle.setSubtotal(productoDB.getPrecio() * detalle.getCantidad());
        }

        Boleta guardada = boletaRepository.save(boleta);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }
}