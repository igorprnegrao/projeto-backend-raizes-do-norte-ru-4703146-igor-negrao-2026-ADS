package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, UUID> {

	@Query("SELECT COUNT(e) > 0 FROM Equipe e WHERE e.fonePrincipal = :fonePrincipal")
	boolean existsByFonePrincipal(@Param("fonePrincipal") String fonePrincipal);

	@Query("SELECT COUNT(e) > 0 FROM Equipe e WHERE e.email = :email")
	boolean existsByEmail(@Param("email") String email);

	Optional<Equipe> findByEmail(String email);
}
