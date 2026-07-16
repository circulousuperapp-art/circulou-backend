package br.com.circulou.circulou_backend.model;

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
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private String marca;
    private String unidadeMedida;
    private Double peso;
    private String codigoBarras;

    // TODO: Ponto de extensão para futura implementação de Categoria
    // A Categoria será um agregado próprio vinculado ao Produto em sprints futuras.
    private String imagemPrincipal;
    private Boolean ativo;

}