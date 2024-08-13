package com.example.apiestoque.meuapp.repository;

import com.example.apiestoque.meuapp.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EstoqueRepository extends JpaRepository<Produto, Long> {

    @Modifying
    @Query("DELETE FROM Produto e WHERE e.id = ?1")
    void deleteById(Long id);


    List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThanEqual(String nome, Double preco);

}
