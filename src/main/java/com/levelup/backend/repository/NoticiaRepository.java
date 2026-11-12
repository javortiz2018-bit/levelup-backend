package com.levelup.backend.repository;

import com.levelup.backend.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findAllByOrderByFechaPublicacionDesc();
}