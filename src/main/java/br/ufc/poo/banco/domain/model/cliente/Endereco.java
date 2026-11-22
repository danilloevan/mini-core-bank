package br.ufc.poo.banco.domain.model.cliente;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Classe que representa o Endereço de um Cliente.
 * Relação UML: COMPOSIÇÃO com Cliente (o Endereço é parte integrante do Cliente).
 */
@Embeddable
public class Endereco {

    @Column(name = "logradouro", nullable = false, length = 150)
    private String logradouro;

    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Column(name = "cep", nullable = false, length = 15)
    private String cep;

    protected Endereco() {
        // Construtor padrão exigido pelo JPA
    }

    public Endereco(String logradouro, String cidade, String uf, String cep) {
        this.logradouro = logradouro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
    }

    public String formatarEnderecoCompleto() {
        return String.format("%s - %s/%s (CEP: %s)", logradouro, cidade, uf, cep);
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
