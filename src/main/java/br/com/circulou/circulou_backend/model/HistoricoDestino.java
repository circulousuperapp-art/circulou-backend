package br.com.circulou.circulou_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoDestino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeLocal;

    private String endereco;

    private Double latitude;

    private Double longitude;

    private LocalDateTime ultimaUtilizacao;

    private Integer quantidadeUtilizacoes;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}