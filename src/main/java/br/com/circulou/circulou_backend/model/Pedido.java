package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.model.event.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PedidoStatus status = PedidoStatus.PENDENTE;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column
    private LocalDateTime dataLimiteCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

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
