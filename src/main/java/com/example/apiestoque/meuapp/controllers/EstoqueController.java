package com.example.apiestoque.meuapp.controllers;

import com.example.apiestoque.meuapp.models.Produto;
import com.example.apiestoque.meuapp.repository.EstoqueRepository;
import com.example.apiestoque.meuapp.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.*;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //Rest: devolve um json e boas praticas, Controller: informa que recebe requisição
@RequestMapping("/api/produtos")
public class EstoqueController {
    private final ProdutoService produtoService;
    private final Validator validator;

    @Autowired //Declara que todos os parametros que estão no métodos injetam dependencias
    public EstoqueController( ProdutoService produtoService, Validator validator) {
        this.produtoService = produtoService;
        this.validator = validator;
    }

    //método para mostrar tudo que tem cadastrado no banco
    @GetMapping("/selecionar")
    @Operation(summary = "Listar produto ", description = "Lista todos os produtos do banco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listagem realizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class)))
    })
    public List<Produto> listarProdutos() {
        return produtoService.buscarTodosOsProdutos();
    }

    //método para inserir um novo documento
    @PostMapping("/inserir")
    @Operation(summary = "Insere um produto", description = "Insere um produto com as informações necessárias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content)
    })
    public ResponseEntity<?> inserirProduto(@Parameter(description = "Informações do produto para inserir")@Valid @RequestBody Produto produto, BindingResult result) {
        try {
            /*
            if(produto.getQtdEstoque() == null || produto.getQtdEstoque() < 0){
                return ResponseEntity.status(404).body("O valor inserido no quantidade estoque está invalido");
            }
            if(produto.getPreco() == null || produto.getPreco() < 0){
                return ResponseEntity.status(404).body("O valor inserido no preço está invalido");
            }
            if(produto.getDescricao() == null){
                return ResponseEntity.status(404).body("A informação inserida na descrição está invalido");
            }
            if(produto.getNome() == null){
                return ResponseEntity.status(404).body("A informação inserida no nome está invalido");
            }


            if(resultado.hasErrors()){
                String mensagemErro = "";
                List<ObjectError> todosOsErros = result.getAllErrors();
                for (ObjectError o : todosOsErros){
                    mensagemErro += " - " + o.getDefaultMessage();
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);


             */
            if(result.hasErrors()){
                Map erros = validarProduto(result);
                return ResponseEntity.badRequest().body(erros);
            }else {
                produtoService.salvarProduto(produto);
                //retornando um json
                return ResponseEntity.ok("Produto inserido com sucesso");
            }
        } catch (NullPointerException nullPointerException) {
            System.out.println("Valor vazio passado");
            return ResponseEntity.badRequest().build();
        }
    }
    /*
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("id não existe");
        } else {
            produtoRepository.deleteById(id);
            return ResponseEntity.ok("Produto excluído com sucesso");
        }
    }

     */

    //método de deletar por id, é um metodo que eu criei. Tem implementações nas classes ProdutoService e EstoqueRepository
    @DeleteMapping("/excluirId/{id}")
    @Operation(summary = "Exclui produto por id", description = "Remove um produto do sistema pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<String> excluirProdutoCerto(@Parameter(description = "ID do produto a ser excluído") @PathVariable Long id) {
        Produto produto = produtoService.excluirProduto(id);
        if (produto != null) {
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluido com sucesso");
        } else {
            return ResponseEntity.status(404).body("não existe esse id");
        }
    }

    @PatchMapping("/atualizarParcial/{id}")
    @Operation(summary = "Atualiza um produto parcialmente", description = "Atualiza um produto de acordo com um campo desejado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity <?> atualizarParcial(@Parameter(description = "ID do produto para atualizar") @PathVariable Long id,
                                                   @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Mapeamento de campos a serem atualizados com os novos valores", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"nome\":\"Novo nome\", \"descricao\":\"nova descrecao\", \"preco\": \"10.0\", \"quantidadeEstoque\": \"3\" "))) @RequestBody Map<String, Object> updates) {
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            // Criação de um objeto Produto temporário para validação
            Produto produtoTemp = new Produto();

            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco((Double) updates.get("preco"));

            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQtdEstoque((Integer) updates.get("quantidadeEstoque"));
            }

            // Validando manualmente o objeto Produto temporário
            DataBinder binder = new DataBinder(produto);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();
            if(result.hasErrors()){
                Map erros = validarProduto(result);
                return ResponseEntity.badRequest().body(erros);
            }
            // Se a validação passar, atualize o produto
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado parcialmente com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto com ID " + id + " não encontrado");
        }
    }

    //método para validar o documento do tipo produto
    public Map<String, String> validarProduto(BindingResult result ){
        Map<String, String > erros = new HashMap<>();
        for(FieldError error: result.getFieldErrors()){
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }
//
//    @GetMapping("/buscarPorNome")
//    public ResponseEntity<?> buscarPorNome(@RequestParam String nome){
//
// ai eu n to entendedo nada preciso ver as aulas dele
//    }





/*
    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<String> atualizarParcial(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> updates) {
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
//            if(produto.getQtdEstoque() == null || produto.getQtdEstoque() < 0){
//                return ResponseEntity.status(404).body("O valor inserido no quantidade estoque está invalido");
//            }
//            if(produto.getPreco() == null || produto.getPreco() < 0){
//                return ResponseEntity.status(404).body("O valor inserido no preço está invalido");
//            }
//            if(produto.getDescricao() == null){
//                return ResponseEntity.status(404).body("A informação inserida na descrição está invalido");
//            }
//            if(produto.getNome() == null){
//                return ResponseEntity.status(404).body("A informação inserida no nome está invalido");
//
//            }
            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco((Double) updates.get("preco"));
            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQtdEstoque((Integer) updates.get("quantidadeEstoque"));
            }
            produtoRepository.save(produto);
            return ResponseEntity.ok("Produto atualizado parcialmente com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body("Produto com ID " + id + " não encontrado");
        }
    }

 */

    @PutMapping("atualizar/{id}")
    @Operation(summary = "Atualiza um produto por ID", description = "Atualiza um produto com o ID necessário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<String> atualizarProduto(@Parameter(description = "ID do produto que será atualizado")@PathVariable Long id, @Parameter(description = "Informações do produto para atualizar, JSON no BODY") @Valid @RequestBody Produto produtoAtualizado, BindingResult resultado) {
        // Verifique os erros de validação primeiro
        if(resultado.hasErrors()){
            String mensagemErro = "";
            List<ObjectError> todosOsErros = resultado.getAllErrors();
            for (ObjectError o : todosOsErros){
                mensagemErro += " - " + o.getDefaultMessage();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemErro);

        }else {
            Produto produto = produtoService.buscarProdutoPorId(id);
            if (produto != null) {
                produto.setNome(produtoAtualizado.getNome());
                produto.setDescricao(produtoAtualizado.getDescricao());
                produto.setPreco(produtoAtualizado.getPreco());
                produto.setQtdEstoque(produtoAtualizado.getQtdEstoque());
                produtoService.salvarProduto(produto);
                return ResponseEntity.ok("Produto atualizado com sucesso");
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }


    @GetMapping("/selecionarPorNome")
    @Operation(summary = "Buscar por nome e preço", description = "Busca o produto pelo nome e preço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto achado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
    })
    public ResponseEntity<?> buscarPorNome(@Parameter(description = "Nome do produto")@RequestParam String nome,@Parameter(description = "Valor do preço do produto") @RequestParam String preco) {
        List<Produto> busca = produtoService.buscar(nome, Double.parseDouble(preco));
        if (!busca.isEmpty()) {
            return ResponseEntity.ok().body(busca);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi encontrado nenhum produto com esses atributos.");
        }
    }




}//fim da class EstoqueController

