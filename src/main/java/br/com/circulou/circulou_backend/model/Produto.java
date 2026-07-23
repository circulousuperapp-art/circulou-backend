package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String nome;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @Size(max = 100)
    @Column(length = 100)
    private String marca;

    @Size(max = 20)
    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @NotNull
    @Positive
    @Digits(integer = 7, fraction = 3)
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal peso;

    public void setPeso(BigDecimal peso) {
        if (peso != null) {
            this.peso = peso.setScale(3, RoundingMode.HALF_UP);
        } else {
            this.peso = null;
        }
    }

    @Size(max = 50)
    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    // TODO: Ponto de extensão para futura implementação de Categoria
    // A Categoria será um agregado próprio vinculado ao Produto em sprints futuras.
    @Size(max = 255)
    @Column(name = "imagem_principal", length = 255)
    private String imagemPrincipal;
    
    private Boolean ativo;

}