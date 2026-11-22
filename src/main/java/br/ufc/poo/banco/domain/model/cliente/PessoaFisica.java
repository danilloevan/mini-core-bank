package br.ufc.poo.banco.domain.model.cliente;

import br.ufc.poo.banco.domain.exception.NegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

/**
 * Subclasse PessoaFisica que herda de Cliente.
 * Demonstra herança, atributo private específico, método estático de validação e polimorfismo.
 */
@Entity
@DiscriminatorValue("PF")
public class PessoaFisica extends Cliente {

    public static final int TAMANHO_CPF = 11;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    protected PessoaFisica() {
        super();
    }

    public PessoaFisica(String nome, String cpf, String email, LocalDate dataNascimento, Endereco endereco) {
        super(nome, cpf, email, endereco);
        this.dataNascimento = dataNascimento;
        if (!validarDocumento()) {
            throw new NegocioException("CPF inválido. O CPF deve conter 11 dígitos numéricos.");
        }
    }

    /**
     * Método estático utilitário (Requisito 2.g do edital: método static).
     */
    public static boolean validarCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        String apenasDigitos = cpf.replaceAll("\\D", "");
        return apenasDigitos.length() == TAMANHO_CPF && !apenasDigitos.chars().allMatch(c -> c == apenasDigitos.charAt(0));
    }

    @Override
    public boolean validarDocumento() {
        return validarCpf(this.documento);
    }

    @Override
    public boolean isIsentoTarifaPix() {
        return true; // Banco Central: Pessoa Física é isenta de tarifa no PIX comum
    }

    @Override
    public String obterTipoCliente() {
        return "PF";
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
}
