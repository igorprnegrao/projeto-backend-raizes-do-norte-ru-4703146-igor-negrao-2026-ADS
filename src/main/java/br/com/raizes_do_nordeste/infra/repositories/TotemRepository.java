package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.Totem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TotemRepository extends JpaRepository<Totem, UUID> {
	boolean existsByCodigoIdentificador(String codigoIdentificador);
}

