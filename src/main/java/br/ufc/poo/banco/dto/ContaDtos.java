package br.ufc.poo.banco.dto;

import br.ufc.poo.banco.domain.model.conta.ChavePix;
import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.model.conta.TipoChavePix;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ContaDtos {

    public record AbrirContaRequest(
            @NotNull(message = "O ID do cliente titular é obrigatório") Long clienteId,
            @NotNull(message = "O ID da agência é obrigatório") Long agenciaId,
            @NotBlank(message = "O tipo da conta (CORRENTE ou EMPRESARIAL) é obrigatório") String tipoConta
    ) {}

    public record CadastrarChavePixRequest(
            @NotNull(message = "O tipo da chave PIX é obrigatório") TipoChavePix tipo,
            @NotBlank(message = "O valor da chave PIX é obrigatório") String valor
    ) {}

    public record ContaResponse(
            Long id,
            String codigoBanco,
            String codigoAgencia,
            String nomeAgencia,
            String numeroConta,
            String tipoConta,
            String status,
            BigDecimal saldo,
            BigDecimal limiteSaqueDiario,
            BigDecimal tarifaManutencao,
            Long titularId,
            String titularNome,
            String titularDocumento,
            String titularTipo
    ) {
        public static ContaResponse deEntidade(Conta conta) {
            return new ContaResponse(
                    conta.getId(),
                    Conta.CODIGO_BANCO,
                    conta.getAgencia().getCodigo(),
                    conta.getAgencia().getNome(),
                    conta.getNumeroConta(),
                    conta.obterTipoConta(),
                    conta.getStatus().name(),
                    conta.getSaldo(),
                    conta.obterLimiteSaqueDiario(),
                    conta.obterTarifaManutencao(),
                    conta.getTitular().getId(),
                    conta.getTitular().getNome(),
                    conta.getTitular().getDocumento(),
                    conta.getTitular().obterTipoCliente()
            );
        }
    }

    public record ChavePixResponse(
            Long id,
            String tipo,
            String valor,
            Long contaId,
            String numeroConta,
            String titularNome
    ) {
        public static ChavePixResponse deEntidade(ChavePix chave) {
            return new ChavePixResponse(
                    chave.getId(),
                    chave.getTipo().name(),
                    chave.getValor(),
                    chave.getConta().getId(),
                    chave.getConta().getNumeroConta(),
                    chave.getConta().getTitular().getNome()
            );
        }
    }
}
