package com.example.apiestoque.meuapp.services;

import com.example.apiestoque.meuapp.models.Produto;
import com.example.apiestoque.meuapp.repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Service
//Todos os modleos que fazem interação com o banco de dados
public class ProdutoService {
    private final EstoqueRepository produtoRepository;
    public ProdutoService(EstoqueRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }
    public List<Produto> buscarTodosOsProdutos(){
        return produtoRepository.findAll();
    }
    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }
    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(()->
                new RuntimeException("Produto não enconytado"));
    }
    public Produto excluirProduto(Long id){
        Optional<Produto> prod = produtoRepository.findById(id);
        if (prod.isPresent()){
            produtoRepository.deleteById(id);
            return prod.get();
        }
        return null;
    }

    public List<Produto> buscar (String nome, Double preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThanEqual(nome, preco);
    }
}