package br.ufc.poo.banco.domain.model.conta;

import br.ufc.poo.banco.domain.exception.NegocioException;
import br.ufc.poo.banco.domain.exception.SaldoInsuficienteException;
import br.ufc.poo.banco.domain.model.cliente.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * Classe Abstrata Conta (Hierarquia de Herança 2).
 * Possui atributo e método static, atributos protected herdados pelas subclasses,
 * associação simples com Cliente (titular), agregação com Agencia e métodos polimórficos.
 */
@Entity
@Table(name = "contas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_conta", discriminatorType = DiscriminatorType.STRING)
public abstract class Conta {

    // Atributo estático (Requisito 2.g do edital)
    public static final String CODIGO_BANCO = "123";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_conta", nullable = false, unique = true, length = 20)
    protected String numeroConta;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    protected BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    protected StatusConta status;

    // Relação UML: ASSOCIAÇÃO SIMPLES (Conta --> Cliente)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente titular;

    // Relação UML: AGREGAÇÃO (Agencia o-- Conta)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "agencia_id", nullable = false)
    private Agencia agencia;

    protected Conta() {
        // Construtor padrão para o JPA
    }

    protected Conta(String numeroConta, Cliente titular, Agencia agencia) {
        if (titular == null) {
            throw new NegocioException("A conta deve possuir um cliente titular.");
        }
        if (agencia == null) {
            throw new NegocioException("A conta deve estar vinculada a uma agência.");
        }
        if (!podeTerTitular(titular)) {
            throw new NegocioException(String.format(
                    "O tipo de conta %s não é compatível com cliente do tipo %s.",
                    obterTipoConta(), titular.obterTipoCliente()
            ));
        }
        this.numeroConta = numeroConta;
        this.saldo = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.status = StatusConta.ATIVA;
        this.titular = titular;
        this.agencia = agencia;
        agencia.adicionarConta(this);
    }

    /**
     * Método estático utilitário para geração padronizada de número de conta com dígito verificador (Requisito 2.g).
     */
    public static String gerarNumeroContaFormatado(long sequencial) {
        long digito = (sequencial * 7) % 10;
        return String.format("%05d-%d", sequencial, digito);
    }

    /**
     * Operação direta de crédito no saldo da conta.
     */
    public void creditar(BigDecimal valor) {
        validarContaAtiva();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("O valor para crédito deve ser maior que zero.");
        }
        this.saldo = this.saldo.add(valor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Operação direta de débito no saldo da conta.
     */
    public void debitar(BigDecimal valor) {
        validarContaAtiva();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegocioException("O valor para débito deve ser maior que zero.");
        }
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException(this.numeroConta, this.saldo, valor);
        }
        this.saldo = this.saldo.subtract(valor).setScale(2, RoundingMode.HALF_UP);
    }

    protected void validarContaAtiva() {
        if (this.status != StatusConta.ATIVA) {
            throw new NegocioException("A conta " + this.numeroConta + " não está ativa (Status: " + this.status + ").");
        }
    }

    // Métodos abstratos para Polimorfismo nas subclasses ContaCorrente e ContaEmpresarial
    public abstract BigDecimal obterLimiteSaqueDiario();

    public abstract BigDecimal obterTarifaManutencao();

    public abstract boolean podeTerTitular(Cliente cliente);

    public abstract String obterTipoConta();

    public Long getId() {
        return id;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public StatusConta getStatus() {
        return status;
    }

    public void setStatus(StatusConta status) {
        this.status = status;
    }

    public Cliente getTitular() {
        return titular;
    }

    public Agencia getAgencia() {
        return agencia;
    }
}
