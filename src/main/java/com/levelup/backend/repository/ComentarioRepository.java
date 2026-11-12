package com.levelup.backend.repository;

import com.levelup.backend.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByNoticiaIdNoticiaOrderByFechaComentarioDesc(Long idNoticia);
}