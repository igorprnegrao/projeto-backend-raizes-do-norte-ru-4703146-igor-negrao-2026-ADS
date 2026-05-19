package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {

	boolean existsByPedidoId(Long pedidoId);

	Optional<Pagamento> findByPedidoId(Long pedidoId);

	/**
	 * Carrega pagamentos de múltiplos pedidos em uma única query (IN clause),
	 * eliminando o N+1 gerado por chamadas repetidas a findByPedidoId na listagem.
	 */
	@Query("SELECT p FROM Pagamento p WHERE p.pedido.id IN :pedidoIds")
	List<Pagamento> findAllByPedidoIdIn(@Param("pedidoIds") List<Long> pedidoIds);
}


