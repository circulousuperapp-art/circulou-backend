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
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lojista_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LojistaProfile {

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
    private String cnpj;

    @NotBlank
    @Size(max = 255)
    @Column(name = "razao_social", length = 255, nullable = false)
    private String razaoSocial;

    @Size(max = 100)
    @Column(length = 100)
    private String segmento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_documentacao", nullable = false, length = 30)
    private StatusDocumentacao statusDocumentacao;

    @NotNull
    @Column(name = "rating_media", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingMedia = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "lojistaProfile", cascade = CascadeType.ALL)
    private List<Loja> lojas;
}
