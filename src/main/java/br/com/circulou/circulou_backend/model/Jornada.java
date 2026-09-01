package br.com.circulou.circulou_backend.model;

import br.com.circulou.circulou_backend.exception.BusinessException;
import br.com.circulou.circulou_backend.exception.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "jornada")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class Jornada {

    public static final int MAX_PARADAS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "corrida_id")
    private Long corridaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JornadaTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JornadaStatus status = JornadaStatus.CRIADA;

    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<JornadaParada> paradas = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Jornada(Long clienteId, JornadaTipo tipo) {
        this.clienteId = Objects.requireNonNull(clienteId, "Cliente ID é obrigatório");
        this.tipo = Objects.requireNonNull(tipo, "Tipo de jornada é obrigatório");
    }

    public void adicionarParada(Long lojaId, Long pedidoId, Integer ordem) {
        if (status != JornadaStatus.CRIADA) {
            throw new BusinessException("Só é possível adicionar paradas em uma jornada com status CRIADA");
        }
        if (paradas.size() >= MAX_PARADAS) {
            throw new BusinessException("Limite máximo de " + MAX_PARADAS + " paradas atingido");
        }

        validarOrdem(ordem);

        JornadaParada parada = new JornadaParada(this, lojaId, pedidoId, ordem);
        this.paradas.add(parada);
    }

    public void iniciar() {
        if (paradas.isEmpty()) {
            throw new BusinessException("Não é possível iniciar uma jornada sem paradas");
        }
        validarTransicao(JornadaStatus.EM_EXECUCAO);
        this.status = JornadaStatus.EM_EXECUCAO;
    }

    public void concluir() {
        validarTransicao(JornadaStatus.CONCLUIDA);

        boolean todasTerminais = paradas.stream().allMatch(JornadaParada::isTerminal);
        if (!todasTerminais) {
            throw new BusinessException("Não é possível concluir a jornada: existem paradas pendentes");
        }

        this.status = JornadaStatus.CONCLUIDA;
    }

    public void cancelar() {
        if (status == JornadaStatus.CONCLUIDA) {
            throw new BusinessException("Não é possível cancelar uma jornada já concluída");
        }
        this.status = JornadaStatus.CANCELADA;
        // Opcionalmente cancela paradas não terminais, mas a regra diz que cancelar parada não cancela jornada.
        // Aqui, cancelar JORNADA é um ato global.
        paradas.stream()
                .filter(p -> !p.isTerminal())
                .forEach(JornadaParada::cancelar);
    }

    public void iniciarDeslocamentoProximaParada() {
        if (status != JornadaStatus.EM_EXECUCAO) {
            throw new BusinessException("A jornada deve estar em execução para iniciar o deslocamento de uma parada");
        }

        JornadaParada proxima = paradas.stream()
                .filter(p -> p.getStatus() == JornadaParadaStatus.AGUARDANDO)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Não existem mais paradas aguardando"));

        // Valida se não há outra parada em execução (não terminal)
        boolean existeParadaEmExecucao = paradas.stream()
                .anyMatch(p -> p.getStatus() == JornadaParadaStatus.EM_TRANSITO || p.getStatus() == JornadaParadaStatus.NA_LOJA);

        if (existeParadaEmExecucao) {
            throw new BusinessException("Já existe uma parada em execução. Conclua ou cancele a atual antes de iniciar a próxima.");
        }

        // Valida se todas as anteriores são terminais (redundante devido ao findFirst e ordenação, mas seguro)
        int index = paradas.indexOf(proxima);
        for (int i = 0; i < index; i++) {
            if (!paradas.get(i).isTerminal()) {
                throw new BusinessException("A parada anterior deve estar concluída ou cancelada");
            }
        }

        proxima.iniciarDeslocamento();
    }

    public void registrarChegadaNaLoja() {
        JornadaParada atual = getParadaEmExecucao();
        atual.registrarChegadaNaLoja();
    }

    public void confirmarRetirada() {
        JornadaParada atual = getParadaEmExecucao();
        atual.confirmarRetirada();
    }

    public void cancelarParada(Integer ordem) {
        JornadaParada parada = paradas.stream()
                .filter(p -> p.getOrdem().equals(ordem))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Parada não encontrada com a ordem " + ordem));

        parada.cancelar();
    }

    private JornadaParada getParadaEmExecucao() {
        return paradas.stream()
                .filter(p -> p.getStatus() == JornadaParadaStatus.EM_TRANSITO || p.getStatus() == JornadaParadaStatus.NA_LOJA)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Não existe parada em execução no momento"));
    }

    public List<JornadaParada> getParadas() {
        return Collections.unmodifiableList(paradas);
    }

    public void setCorridaId(Long corridaId) {
        if (tipo == JornadaTipo.PROPRIA) {
            throw new BusinessException("Jornada do tipo PROPRIA não pode ter uma corrida associada");
        }
        this.corridaId = corridaId;
    }

    private void validarOrdem(Integer ordem) {
        int proximaOrdemEsperada = paradas.size() + 1;
        if (!ordem.equals(proximaOrdemEsperada)) {
            throw new BusinessException("Ordem inválida. A próxima parada deve possuir a ordem " + proximaOrdemEsperada);
        }
    }

    private void validarTransicao(JornadaStatus novoStatus) {
        if (status == JornadaStatus.CONCLUIDA || status == JornadaStatus.CANCELADA) {
            throw new BusinessException("Transição inválida: Jornada já está em estado terminal: " + status);
        }

        if (novoStatus == JornadaStatus.EM_EXECUCAO && status != JornadaStatus.CRIADA) {
            throw new BusinessException("Jornada só pode ser iniciada a partir do status CRIADA");
        }
    }
}
