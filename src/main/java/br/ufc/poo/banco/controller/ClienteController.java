package br.ufc.poo.banco.controller;

import br.ufc.poo.banco.dto.ClienteDtos.ClienteResponse;
import br.ufc.poo.banco.dto.ClienteDtos.CriarPessoaFisicaRequest;
import br.ufc.poo.banco.dto.ClienteDtos.CriarPessoaJuridicaRequest;
import br.ufc.poo.banco.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "1. Clientes", description = "Gerenciamento de Clientes Pessoa Física (PF) e Pessoa Jurídica (PJ)")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes (PF e PJ)")
    public List<ClienteResponse> listarTodos() {
        return clienteService.listarTodos().stream()
                .map(ClienteResponse::deEntidade)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return ClienteResponse.deEntidade(clienteService.buscarPorId(id));
    }

    @PostMapping("/pf")
    @Operation(summary = "Cadastrar novo cliente Pessoa Física (PF)")
    public ResponseEntity<ClienteResponse> cadastrarPessoaFisica(@Valid @RequestBody CriarPessoaFisicaRequest request) {
        ClienteResponse response = ClienteResponse.deEntidade(clienteService.cadastrarPessoaFisica(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pj")
    @Operation(summary = "Cadastrar novo cliente Pessoa Jurídica (PJ)")
    public ResponseEntity<ClienteResponse> cadastrarPessoaJuridica(@Valid @RequestBody CriarPessoaJuridicaRequest request) {
        ClienteResponse response = ClienteResponse.deEntidade(clienteService.cadastrarPessoaJuridica(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
