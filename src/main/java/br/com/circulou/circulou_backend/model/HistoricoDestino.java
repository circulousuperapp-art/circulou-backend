package br.com.circulou.circulou_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_destino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoDestino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "nome_local", length = 255)
    private String nomeLocal;

    @Size(max = 255)
    @Column(length = 255)
    private String endereco;

    private Double latitude;

    private Double longitude;

    @Column(name = "ultima_utilizacao")
    private LocalDateTime ultimaUtilizacao;

    @Column(name = "quantidade_utilizacoes")
    private Integer quantidadeUtilizacoes;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

}