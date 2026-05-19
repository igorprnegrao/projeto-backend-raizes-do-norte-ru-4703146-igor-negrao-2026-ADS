package br.com.raizes_do_nordeste.infra.repositories;


import br.com.raizes_do_nordeste.domain.entities.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {

	@Query("SELECT COUNT(u) > 0 FROM Unidade u WHERE u.foneSecundarioCelular = :foneSecundarioCelular")
	boolean existsByFoneSecundarioCelular(@Param("foneSecundarioCelular") String foneSecundarioCelular);

	@Query("SELECT COUNT(u) > 0 FROM Unidade u WHERE u.email = :email")
	boolean existsByEmail(@Param("email") String email);

	@Query("SELECT COUNT(u) > 0 FROM Unidade u WHERE u.foneSecundarioCelular = :foneSecundarioCelular AND u.id <> :idUnidade")
	boolean existsByFoneSecundarioCelularAndIdNot(@Param("foneSecundarioCelular") String foneSecundarioCelular,
														 @Param("idUnidade") UUID idUnidade);

	@Query("SELECT COUNT(u) > 0 FROM Unidade u WHERE u.email = :email AND u.id <> :idUnidade")
	boolean existsByEmailAndIdNot(@Param("email") String email, @Param("idUnidade") UUID idUnidade);
}
