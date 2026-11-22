package br.ufc.poo.banco.domain.model.conta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa uma Agência Bancária.
 * Relação UML: AGREGAÇÃO com Conta (uma Agência agrega várias Contas, mas as Contas
 * possuem ciclo de vida próprio e podem ser transferidas de agência).
 */
@Entity
@Table(name = "agencias")
public class Agencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    // Relação UML: AGREGAÇÃO (o--)
    @OneToMany(mappedBy = "agencia")
    private List<Conta> contas = new ArrayList<>();

    protected Agencia() {
        // Construtor padrão para o JPA
    }

    public Agencia(String codigo, String nome, String cidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.cidade = cidade;
    }

    public void adicionarConta(Conta conta) {
        if (conta != null && !this.contas.contains(conta)) {
            this.contas.add(conta);
        }
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public List<Conta> getContas() {
        return Collections.unmodifiableList(contas);
    }
}
