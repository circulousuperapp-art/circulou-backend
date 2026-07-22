package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_perfil", nullable = false, length = 30)
    private StatusPerfil statusPerfil;

    @NotBlank
    @Size(max = 20)
    @Column(length = 20, nullable = false, unique = true)
    private String cnh;

    @NotBlank
    @Size(max = 10)
    @Column(name = "categoria_cnh", length = 10, nullable = false)
    private String categoriaCnh;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_documentacao", nullable = false, length = 30)
    private StatusDocumentacao statusDocumentacao;

    @NotNull
    @Column(name = "rating_media", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingMedia = BigDecimal.ZERO;
}
