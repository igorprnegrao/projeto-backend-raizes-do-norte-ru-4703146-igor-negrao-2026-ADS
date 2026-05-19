package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.fonePrincipal = :fonePrincipal")
    boolean existsByFonePrincipal(@Param("fonePrincipal") String fonePrincipal);

    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.email = :email")
    boolean existsByEmail(@Param("email") String email);

    Optional<Cliente> findByFonePrincipal(String fonePrincipal);

    Optional<Cliente> findByEmail(String email);
}
