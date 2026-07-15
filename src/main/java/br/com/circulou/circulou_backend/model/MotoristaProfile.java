package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "motorista_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotoristaProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPerfil statusPerfil;

    @Column(length = 20, nullable = false)
    private String cnh;

    @Column(length = 10, nullable = false)
    private String categoriaCnh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumentacao statusDocumentacao;

    @Column(precision = 3, scale = 2)
    private BigDecimal ratingMedia = BigDecimal.ZERO;
}
