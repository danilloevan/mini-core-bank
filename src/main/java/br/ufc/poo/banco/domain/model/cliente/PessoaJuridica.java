package br.ufc.poo.banco.domain.model.cliente;

import br.ufc.poo.banco.domain.exception.NegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Subclasse PessoaJuridica que herda de Cliente.
 * Demonstra herança, atributo private específico, método estático de validação e polimorfismo.
 */
@Entity
@DiscriminatorValue("PJ")
public class PessoaJuridica extends Cliente {

    public static final int TAMANHO_CNPJ = 14;

    @Column(name = "razao_social", length = 150)
    private String razaoSocial;

    protected PessoaJuridica() {
        super();
    }

    public PessoaJuridica(String nomeFantasia, String cnpj, String email, String razaoSocial, Endereco endereco) {
        super(nomeFantasia, cnpj, email, endereco);
        if (razaoSocial == null || razaoSocial.isBlank()) {
            throw new NegocioException("A razão social é obrigatória para Pessoa Jurídica.");
        }
        this.razaoSocial = razaoSocial;
        if (!validarDocumento()) {
            throw new NegocioException("CNPJ inválido. O CNPJ deve conter 14 dígitos numéricos.");
        }
    }

    /**
     * Método estático utilitário (Requisito 2.g do edital: método static).
     */
    public static boolean validarCnpj(String cnpj) {
        if (cnpj == null) {
            return false;
        }
        String apenasDigitos = cnpj.replaceAll("\\D", "");
        return apenasDigitos.length() == TAMANHO_CNPJ && !apenasDigitos.chars().allMatch(c -> c == apenasDigitos.charAt(0));
    }

    @Override
    public boolean validarDocumento() {
        return validarCnpj(this.documento);
    }

    @Override
    public boolean isIsentoTarifaPix() {
        return false; // Pessoa Jurídica possui tarifa percentual em transferências PIX comerciais
    }

    @Override
    public String obterTipoCliente() {
        return "PJ";
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
}
