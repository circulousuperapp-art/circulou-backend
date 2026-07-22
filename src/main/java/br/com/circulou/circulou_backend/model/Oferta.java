package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "oferta", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"loja_id", "produto_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull
    @Column(nullable = false)
    private Integer estoque;

    @NotNull
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo = true;

    @NotNull
    @Column(nullable = false)
    private Boolean disponivel = true;

    @NotNull
    @Column(name = "permite_retirada", nullable = false)
    private Boolean permiteRetirada = false;

    @NotNull
    @Column(name = "permite_entrega", nullable = false)
    private Boolean permiteEntrega = true;

    @NotNull
    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @NotNull
    @LastModifiedDate
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // Campos preparados para evolução futura (Promoções, etc)
    // private BigDecimal precoPromocional;
    // private Double descontoPercentual;
    // private LocalDateTime dataInicioPromocao;
    // private LocalDateTime dataFimPromocao;
    // private Integer quantidadeMaximaPorCompra;
}
