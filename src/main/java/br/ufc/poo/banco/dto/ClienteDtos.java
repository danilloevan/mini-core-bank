package br.ufc.poo.banco.dto;

import br.ufc.poo.banco.domain.model.cliente.Cliente;
import br.ufc.poo.banco.domain.model.cliente.PessoaFisica;
import br.ufc.poo.banco.domain.model.cliente.PessoaJuridica;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ClienteDtos {

    public record CriarPessoaFisicaRequest(
            @NotBlank(message = "O nome é obrigatório") String nome,
            @NotBlank(message = "O CPF é obrigatório") String cpf,
            @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotNull(message = "A data de nascimento é obrigatória") LocalDate dataNascimento,
            @NotBlank(message = "O logradouro é obrigatório") String logradouro,
            @NotBlank(message = "A cidade é obrigatória") String cidade,
            @NotBlank(message = "A UF é obrigatória") String uf,
            @NotBlank(message = "O CEP é obrigatório") String cep
    ) {}

    public record CriarPessoaJuridicaRequest(
            @NotBlank(message = "O nome fantasia é obrigatório") String nomeFantasia,
            @NotBlank(message = "O CNPJ é obrigatório") String cnpj,
            @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "A razão social é obrigatória") String razaoSocial,
            @NotBlank(message = "O logradouro é obrigatório") String logradouro,
            @NotBlank(message = "A cidade é obrigatória") String cidade,
            @NotBlank(message = "A UF é obrigatória") String uf,
            @NotBlank(message = "O CEP é obrigatório") String cep
    ) {}

    public record ClienteResponse(
            Long id,
            String tipoCliente,
            String nome,
            String documento,
            String email,
            LocalDate dataNascimento,
            String razaoSocial,
            String enderecoCompleto,
            boolean isentoTarifaPix
    ) {
        public static ClienteResponse deEntidade(Cliente cliente) {
            LocalDate dataNasc = (cliente instanceof PessoaFisica pf) ? pf.getDataNascimento() : null;
            String razao = (cliente instanceof PessoaJuridica pj) ? pj.getRazaoSocial() : null;
            return new ClienteResponse(
                    cliente.getId(),
                    cliente.obterTipoCliente(),
                    cliente.getNome(),
                    cliente.getDocumento(),
                    cliente.getEmail(),
                    dataNasc,
                    razao,
                    cliente.getEndereco().formatarEnderecoCompleto(),
                    cliente.isIsentoTarifaPix()
            );
        }
    }
}
