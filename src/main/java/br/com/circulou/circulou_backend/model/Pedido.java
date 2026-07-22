package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.model.event.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PedidoStatus status = PedidoStatus.PENDENTE;

    @NotNull
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_limite_cancelamento")
    private LocalDateTime dataLimiteCancelamento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<Object> domainEvents = new ArrayList<>();

    public void calcularTotal() {
        this.valorTotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void transitarPara(PedidoStatus novoStatus, PedidoStatusPolicy policy) {
        if (!policy.isTransicaoValida(this.status, novoStatus)) {
            throw new BusinessException("Transição de status inválida: " + this.status + " -> " + novoStatus);
        }

        this.status = novoStatus;
        registrarEventoDeStatus(novoStatus);
    }

    public void cancelar(PedidoStatusPolicy policy) {
        if (!isCancelamentoPermitido()) {
            throw new BusinessException("O prazo de cancelamento para este pedido expirou ou o status não permite cancelamento");
        }
        transitarPara(PedidoStatus.CANCELADO, policy);
    }

    public boolean isCancelamentoPermitido() {
        if (this.dataLimiteCancelamento == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(this.dataLimiteCancelamento);
    }

    private void registrarEventoDeStatus(PedidoStatus novoStatus) {
        Object event = switch (novoStatus) {
            case AGUARDANDO_LIBERACAO -> new PedidoCriadoEvent(this.id);
            case EM_PREPARO -> new PedidoLiberadoEvent(this.id);
            case PRONTO_PARA_RETIRADA -> new PedidoProntoParaRetiradaEvent(this.id);
            case EM_ROTA -> new PedidoEmRotaEvent(this.id);
            case ENTREGUE -> new PedidoEntregueEvent(this.id);
            case CANCELADO -> new PedidoCanceladoEvent(this.id);
            default -> null;
        };

        if (event != null) {
            this.domainEvents.add(event);
            if (novoStatus == PedidoStatus.EM_PREPARO) {
                this.domainEvents.add(new PedidoEmPreparoEvent(this.id));
            }
        }
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = new ArrayList<>(this.domainEvents);
        this.domainEvents.clear();
        return Collections.unmodifiableList(events);
    }
}
