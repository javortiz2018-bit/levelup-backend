package com.levelup.backend.repository;

import com.levelup.backend.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BoletaRepository extends JpaRepository<Boleta, Long> {
    List<Boleta> findByUsuarioIdUsuarioOrderByFechaDesc(Long idUsuario);
}