package br.com.circulou.circulou_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;

    private String telefone;

    private String logo;

    private Integer tempoMedioPreparo;

    private Boolean ativa;

    private Boolean seloConfianca;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @JsonIgnore
    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL)
    private List<Produto> produtos;

    @JsonIgnore
    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;
}