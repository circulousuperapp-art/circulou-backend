package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPerfil statusPerfil;

    @Column(length = 20, nullable = false)
    private String cnpj;

    @Column(length = 255, nullable = false)
    private String razaoSocial;

    @Column(length = 100)
    private String segmento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumentacao statusDocumentacao;

    @Column(precision = 3, scale = 2)
    private BigDecimal ratingMedia = BigDecimal.ZERO;

    @JsonIgnore
    @OneToMany(mappedBy = "lojistaProfile", cascade = CascadeType.ALL)
    private List<Loja> lojas;
}
