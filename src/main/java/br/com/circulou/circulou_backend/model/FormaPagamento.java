package br.com.circulou.circulou_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private String apelido;

    private String ultimos4Digitos;

    private String tokenPagamento;

    private Boolean principal;

    private Boolean ativa;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}