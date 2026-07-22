package br.com.circulou.circulou_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column(length = 255)
    private String cep;

    @Size(max = 255)
    @Column(length = 255)
    private String logradouro;

    @Size(max = 255)
    @Column(length = 255)
    private String numero;

    @Size(max = 255)
    @Column(length = 255)
    private String complemento;

    @Size(max = 255)
    @Column(length = 255)
    private String bairro;

    @Size(max = 255)
    @Column(length = 255)
    private String cidade;

    @Size(max = 255)
    @Column(length = 255)
    private String estado;
}