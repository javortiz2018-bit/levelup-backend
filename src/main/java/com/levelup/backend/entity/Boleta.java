package com.levelup.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "boleta")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta")
    private Long idBoleta;

    @Column(insertable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Integer subtotal;

    @Column(nullable = false)
    private Integer descuento = 0;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false, length = 20)
    private String estado = "PAGADA";

    @Column(name = "direccion_envio", nullable = false, length = 255)
    private String direccionEnvio;

    @Column(name = "telefono_contacto", nullable = false, length = 20)
    private String telefonoContacto;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @JsonManagedReference
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    private List<DetalleBoleta> detalles;
}