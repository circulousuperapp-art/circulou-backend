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

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(length = 100)
    private String marca;

    @Column(length = 20)
    private String unidadeMedida;

    private Double peso;

    @Column(unique = true, length = 50)
    private String codigoBarras;

    // TODO: Ponto de extensão para futura implementação de Categoria
    // A Categoria será um agregado próprio vinculado ao Produto em sprints futuras.
    private String imagemPrincipal;
    private Boolean ativo;

}