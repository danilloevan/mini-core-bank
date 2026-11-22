package br.ufc.poo.banco.domain.model.transacao;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.model.conta.Conta;
import br.ufc.poo.banco.domain.strategy.CalculadoraTarifa;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Classe Abstrata Transacao (Hierarquia de Herança 3).
 * Implementa o padrão Template Method no método executar(CalculadoraTarifa),
 * possui composição com ComprovanteTransacao, associação simples com Conta (origem/destino)
 * e dependência com a interface CalculadoraTarifa.
 */
@Entity
@Table(name = "transacoes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_transacao", discriminatorType = DiscriminatorType.STRING)
public abstract class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    protected BigDecimal valor;

    @Column(name = "tarifa", nullable = false, precision = 15, scale = 2)
    protected BigDecimal tarifa;

    @Column(name = "data_hora", nullable = false)
    protected LocalDateTime dataHora;

    @Column(name = "descricao", length = 255)
    protected String descricao;

    // Relação UML: ASSOCIAÇÃO SIMPLES (Transacao --> Conta origem)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_origem_id")
    protected Conta contaOrigem;

    // Relação UML: ASSOCIAÇÃO SIMPLES (Transacao --> Conta destino)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_destino_id")
    protected Conta contaDestino;

    // Relação UML: COMPOSIÇÃO (Transacao *-- ComprovanteTransacao)
    @Embedded
    private ComprovanteTransacao comprovante;

    protected Transacao() {
        // Construtor padrão para o JPA
    }

    protected Transacao(BigDecimal valor, String descricao, Conta contaOrigem, Conta contaDestino) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("O valor da transação deve ser maior que zero.");
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
        this.tarifa = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.dataHora = LocalDateTime.now();
        this.descricao = descricao;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }

    /**
     * Template Method que orquestra a execução polimórfica da transação.
     * Relação UML: DEPENDÊNCIA com CalculadoraTarifa (recebida por parâmetro).
     */
    public void executar(CalculadoraTarifa calculadoraTarifa) {
        if (calculadoraTarifa == null) {
            throw new NegocioException("A calculadora de tarifa é obrigatória para executar a transação.");
        }
        // 1. Hook polimórfico: cada subclasse valida suas regras específicas
        validarRegrasEspecificas();

        // 2. Polimorfismo via interface Strategy: calcula a tarifa aplicável
        this.tarifa = calculadoraTarifa.calcularTarifa(this.valor);

        // 3. Hook polimórfico: cada subclasse efetiva os créditos e débitos nas contas
        efetivarMovimentacao();

        // 4. Composição: gera o comprovante parte integrante da transação
        this.comprovante = new ComprovanteTransacao(obterTipoTransacao(), this.valor, this.tarifa);
    }

    // Métodos protected abstratos (Requisito 2.f: modificador protected + Polimorfismo)
    protected abstract void validarRegrasEspecificas();

    protected abstract void efetivarMovimentacao();

    public abstract String obterTipoTransacao();

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public ComprovanteTransacao getComprovante() {
        return comprovante;
    }
}
