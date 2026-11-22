package br.ufc.poo.banco.domain.model.conta;

import br.ufc.poo.banco.domain.exception.NegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Classe que representa uma Chave PIX vinculada a uma Conta.
 * Relação UML: ASSOCIAÇÃO SIMPLES (ChavePix --> Conta).
 */
@Entity
@Table(name = "chaves_pix")
public class ChavePix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoChavePix tipo;

    @Column(name = "valor", nullable = false, unique = true, length = 120)
    private String valor;

    // Relação UML: ASSOCIAÇÃO SIMPLES com Conta
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    protected ChavePix() {
        // Construtor padrão para o JPA
    }

    public ChavePix(TipoChavePix tipo, String valor, Conta conta) {
        if (tipo == null) {
            throw new NegocioException("O tipo da chave PIX é obrigatório.");
        }
        if (valor == null || valor.isBlank()) {
            throw new NegocioException("O valor da chave PIX é obrigatório.");
        }
        if (conta == null) {
            throw new NegocioException("A chave PIX deve estar associada a uma conta.");
        }
        this.tipo = tipo;
        this.valor = valor.trim();
        this.conta = conta;
    }

    public Long getId() {
        return id;
    }

    public TipoChavePix getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public Conta getConta() {
        return conta;
    }
}
