package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "corrida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Corrida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private MotoristaProfile motorista;

    @Column(name = "origem_logradouro")
    private String origemLogradouro;

    @Column(name = "origem_numero")
    private String origemNumero;

    @Column(name = "origem_bairro")
    private String origemBairro;

    @Column(name = "origem_cidade")
    private String origemCidade;

    @Column(name = "origem_estado")
    private String origemEstado;

    @Column(name = "origem_latitude")
    private Double origemLatitude;

    @Column(name = "origem_longitude")
    private Double origemLongitude;

    @Column(name = "destino_logradouro")
    private String destinoLogradouro;

    @Column(name = "destino_numero")
    private String destinoNumero;

    @Column(name = "destino_bairro")
    private String destinoBairro;

    @Column(name = "destino_cidade")
    private String destinoCidade;

    @Column(name = "destino_estado")
    private String destinoEstado;

    @Column(name = "destino_latitude")
    private Double destinoLatitude;

    @Column(name = "destino_longitude")
    private Double destinoLongitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorridaStatus status = CorridaStatus.SOLICITADA;

    @NotNull
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "valor_estimado", precision = 10, scale = 2)
    private BigDecimal valorEstimado;

    @Column(name = "valor_final", precision = 10, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "distancia_estimada_metros")
    private Integer distanciaEstimadaMetros;

    @Column(name = "duracao_estimada_segundos")
    private Integer duracaoEstimadaSegundos;

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    public void transitarPara(CorridaStatus novoStatus, CorridaStatusPolicy policy) {
        if (!policy.isTransicaoValida(this.status, novoStatus)) {
            throw new BusinessException("Transição de status inválida: " + this.status + " -> " + novoStatus);
        }
        this.status = novoStatus;
        
        if (novoStatus == CorridaStatus.CLIENTE_EMBARCADO && this.dataInicio == null) {
            this.dataInicio = LocalDateTime.now();
        } else if (novoStatus == CorridaStatus.FINALIZADA) {
            this.dataFinalizacao = LocalDateTime.now();
        }
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return Collections.unmodifiableList(events);
    }
}
