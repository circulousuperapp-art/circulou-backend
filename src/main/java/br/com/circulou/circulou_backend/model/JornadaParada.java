package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "jornada_parada")
@Getter
@NoArgsConstructor
public class JornadaParada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    private Jornada jornada;

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(nullable = false)
    private Integer ordem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JornadaParadaStatus status = JornadaParadaStatus.AGUARDANDO;

    @Version
    private Long version;

    public JornadaParada(Jornada jornada, Long lojaId, Long pedidoId, Integer ordem) {
        this.jornada = Objects.requireNonNull(jornada, "Jornada é obrigatória");
        this.lojaId = Objects.requireNonNull(lojaId, "Loja ID é obrigatória");
        this.pedidoId = Objects.requireNonNull(pedidoId, "Pedido ID é obrigatório");
        this.ordem = Objects.requireNonNull(ordem, "Ordem é obrigatória");

        if (ordem < 1 || ordem > 5) {
            throw new BusinessException("Ordem da parada deve estar entre 1 e 5");
        }
    }

    void iniciarDeslocamento() {
        validarTransicao(JornadaParadaStatus.EM_TRANSITO);
        this.status = JornadaParadaStatus.EM_TRANSITO;
    }

    void registrarChegadaNaLoja() {
        validarTransicao(JornadaParadaStatus.NA_LOJA);
        this.status = JornadaParadaStatus.NA_LOJA;
    }

    void confirmarRetirada() {
        validarTransicao(JornadaParadaStatus.RETIRADA_REALIZADA);
        this.status = JornadaParadaStatus.RETIRADA_REALIZADA;
    }

    void cancelar() {
        if (isTerminal()) {
            throw new BusinessException("Não é possível cancelar uma parada que já está em estado terminal");
        }
        this.status = JornadaParadaStatus.CANCELADA;
    }

    public boolean isTerminal() {
        return status == JornadaParadaStatus.RETIRADA_REALIZADA || status == JornadaParadaStatus.CANCELADA;
    }

    private void validarTransicao(JornadaParadaStatus novoStatus) {
        if (isTerminal()) {
            throw new BusinessException("Transição inválida: Parada já está em estado terminal: " + this.status);
        }

        boolean valida = false;
        switch (novoStatus) {
            case EM_TRANSITO -> valida = (status == JornadaParadaStatus.AGUARDANDO);
            case NA_LOJA -> valida = (status == JornadaParadaStatus.EM_TRANSITO);
            case RETIRADA_REALIZADA -> valida = (status == JornadaParadaStatus.NA_LOJA);
        }

        if (!valida) {
            throw new BusinessException("Transição de status inválida: de " + status + " para " + novoStatus);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JornadaParada that)) return false;
        return Objects.equals(id, that.id) || (Objects.equals(pedidoId, that.pedidoId) && pedidoId != null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedidoId);
    }
}
