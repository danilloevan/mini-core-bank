package br.ufc.poo.banco.repository;

import br.ufc.poo.banco.domain.model.conta.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgenciaRepository extends JpaRepository<Agencia, Long> {

    Optional<Agencia> findByCodigo(String codigo);
}
