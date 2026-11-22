package br.ufc.poo.banco.domain.model.cliente;

import br.ufc.poo.banco.domain.exception.NegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Classe Abstrata Cliente (Hierarquia de Herança 1).
 * Demonstra o uso de classe abstrata, atributos protected para acesso nas subclasses,
 * composição com Endereco e métodos abstratos para polimorfismo.
 */
@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cliente", discriminatorType = DiscriminatorType.STRING)
public abstract class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    protected String nome;

    @Column(name = "documento", nullable = false, unique = true, length = 20)
    protected String documento;

    @Column(name = "email", nullable = false, length = 120)
    protected String email;

    // Relação UML: COMPOSIÇÃO (Endereco não existe sem o Cliente)
    @Embedded
    private Endereco endereco;

    protected Cliente() {
        // Construtor padrão para o JPA
    }

    protected Cliente(String nome, String documento, String email, Endereco endereco) {
        if (nome == null || nome.isBlank()) {
            throw new NegocioException("O nome do cliente é obrigatório.");
        }
        if (endereco == null) {
            throw new NegocioException("O endereço do cliente é obrigatório (composição).");
        }
        this.nome = nome;
        this.documento = documento != null ? documento.replaceAll("\\D", "") : "";
        this.email = email;
        this.endereco = endereco;
    }

    /**
     * Método abstrato sobrescrito polimorficamente por PessoaFisica e PessoaJuridica.
     */
    public abstract boolean validarDocumento();

    /**
     * Regra polimórfica: Pessoa Física é isenta de tarifa PIX; Pessoa Jurídica paga tarifa percentual.
     */
    public abstract boolean isIsentoTarifaPix();

    /**
     * Retorna o identificador textual do tipo de cliente ("PF" ou "PJ").
     */
    public abstract String obterTipoCliente();

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new NegocioException("O endereço não pode ser nulo.");
        }
        this.endereco = endereco;
    }
}
