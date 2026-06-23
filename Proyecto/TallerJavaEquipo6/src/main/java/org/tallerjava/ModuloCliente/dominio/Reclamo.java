package org.tallerjava.ModuloCliente.dominio;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reclamos")
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comentario;
    private LocalDateTime fecha;
    private String cedulaCliente;

    @Enumerated(EnumType.STRING)
    private EstadoReclamo etiqueta;

    public Reclamo() {}

    public Reclamo(String comentario, String cedulaCliente) {
        this.comentario = comentario;
        this.cedulaCliente = cedulaCliente;
        this.fecha = LocalDateTime.now();
        this.etiqueta = EstadoReclamo.PENDIENTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCedulaCliente() { return cedulaCliente; }
    public void setCedulaCliente(String cedulaCliente) { this.cedulaCliente = cedulaCliente; }

    public EstadoReclamo getEtiqueta() { return etiqueta; }
    public void setEtiqueta(EstadoReclamo etiqueta) { this.etiqueta = etiqueta; }
}