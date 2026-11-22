package br.ufc.poo.banco.domain.exception;

/**
 * Exceção base de regras de negócio do sistema bancário.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
