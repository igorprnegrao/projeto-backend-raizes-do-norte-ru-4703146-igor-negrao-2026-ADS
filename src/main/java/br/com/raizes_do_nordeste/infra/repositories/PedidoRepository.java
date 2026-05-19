package br.com.raizes_do_nordeste.infra.repositories;


import br.com.raizes_do_nordeste.domain.entities.Pedido;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	long countByRegistroDataAfter(LocalDateTime dataHora);

	/**
	 * Listagem paginada com eager-fetch das associações many-to-one (cliente,
	 * unidade, atendente). O fetch join de many-to-one é seguro com paginação
	 * e elimina o N+1 dessas associações sem trazer coleções para memória.
	 */
	@EntityGraph(attributePaths = {"cliente", "unidade", "atendente"})
	Page<Pedido> findByRegistroDataAfter(LocalDateTime dataHora, Pageable pageable);

	@Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.registroData >= :inicio AND p.registroData < :fim")
	BigDecimal somarValorTotalPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

	@Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.registroData >= :inicio AND p.registroData < :fim AND p.unidade.id = :idUnidade")
	BigDecimal somarValorTotalPorPeriodoEUnidade(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("idUnidade") java.util.UUID idUnidade);

	@Query("SELECT COUNT(p) FROM Pedido p WHERE p.registroData >= :inicio AND p.registroData < :fim")
	long countByRegistroDataBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

	@Query("SELECT COUNT(p) FROM Pedido p WHERE p.registroData >= :inicio AND p.registroData < :fim AND p.unidade.id = :idUnidade")
	long countByRegistroDataBetweenAndUnidade(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim, @Param("idUnidade") java.util.UUID idUnidade);

	@EntityGraph(attributePaths = {"cliente", "unidade", "atendente"})
	Page<Pedido> findByClienteIdAndStatusPedidoNot(UUID clienteId, StatusPedido statusPedido, Pageable pageable);
}
