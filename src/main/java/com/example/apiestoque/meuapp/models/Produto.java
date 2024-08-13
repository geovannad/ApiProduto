package com.example.apiestoque.meuapp.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Representa um produto no sistema")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do produto", example = "1234")
    private long id;
    @NotNull(message = "O nome não pode ser nulo")
    @Size(min = 2, message = "O nome deve ter pelo menos 2 caracteres")
    @Schema(description = "Nome do produto", example = "Hamburger")
    private String nome;
    @Schema(description = "Descrição do produto", example = "Hamburger congelado de 500g")
    private String descricao;
    @NotNull(message = "O preço não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser pelo menos 0")
    @Schema(description = "Preço do produto", example = "24,99")
    private Double preco;
    @NotNull(message = "O quatidade não pode ser nulo")
    @Min(value = 0, message = "O quantidade deve ser pelo menos 0")
    @Column(name="quantidadeestoque")
    @Schema(description = "Quantidade em estoque do produto", example = "10")
    private Integer qtdEstoque;

    public Produto(int id, String nome, String descricao, Double preco, Integer qtdEtoque) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.qtdEstoque = qtdEtoque;
    }

    public Produto() {
    }

    public long getId() {
        return id;
    }


    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public Integer getQtdEstoque() {
        return qtdEstoque;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public void setQtdEstoque(Integer qtdEstoque) {
        this.qtdEstoque = qtdEstoque;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", quantidadeestoque=" + qtdEstoque +
                '}';
    }
}