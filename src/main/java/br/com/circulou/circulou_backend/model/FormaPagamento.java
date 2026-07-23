package br.com.circulou.circulou_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "forma_pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(name = "tipo", length = 255)
    private String tipo;

    @Size(max = 255)
    @Column(name = "apelido", length = 255)
    private String apelido;

    @Size(max = 255)
    @Column(name = "ultimos4_digitos", length = 255)
    private String ultimos4Digitos;

    @Size(max = 255)
    @Column(name = "token_pagamento", length = 255)
    private String tokenPagamento;

    @Column(name = "principal")
    private Boolean principal;

    @Column(name = "ativa")
    private Boolean ativa;

    @JsonIgnore
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}