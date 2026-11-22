package br.ufc.poo.banco.repository;

import br.ufc.poo.banco.domain.model.conta.ChavePix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChavePixRepository extends JpaRepository<ChavePix, Long> {

    Optional<ChavePix> findByValor(String valor);

    List<ChavePix> findByContaId(Long contaId);

    boolean existsByValor(String valor);
}
