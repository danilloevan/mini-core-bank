package br.ufc.poo.banco.controller;

import br.ufc.poo.banco.dto.TransacaoDtos.DepositoRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.PixRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.SaqueRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.TedRequest;
import br.ufc.poo.banco.dto.TransacaoDtos.TransacaoResponse;
import br.ufc.poo.banco.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "3. Transações Financeiras", description = "Operações de Depósito, Saque, PIX e TED com execução polimórfica")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @PostMapping("/deposito")
    @Operation(summary = "Realizar depósito em conta (isento de tarifa)")
    public ResponseEntity<TransacaoResponse> realizarDeposito(@Valid @RequestBody DepositoRequest request) {
        TransacaoResponse response = TransacaoResponse.deEntidade(transacaoService.realizarDeposito(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/saque")
    @Operation(summary = "Realizar saque em conta (respeita limite polimórfico da ContaCorrente ou ContaEmpresarial)")
    public ResponseEntity<TransacaoResponse> realizarSaque(@Valid @RequestBody SaqueRequest request) {
        TransacaoResponse response = TransacaoResponse.deEntidade(transacaoService.realizarSaque(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pix")
    @Operation(summary = "Realizar transferência PIX por chave (PF: isento | PJ: tarifa de 1% até R$ 50,00)")
    public ResponseEntity<TransacaoResponse> realizarPix(@Valid @RequestBody PixRequest request) {
        TransacaoResponse response = TransacaoResponse.deEntidade(transacaoService.realizarPix(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/ted")
    @Operation(summary = "Realizar transferência TED entre contas (Tarifa fixa de R$ 10,00)")
    public ResponseEntity<TransacaoResponse> realizarTed(@Valid @RequestBody TedRequest request) {
        TransacaoResponse response = TransacaoResponse.deEntidade(transacaoService.realizarTed(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
