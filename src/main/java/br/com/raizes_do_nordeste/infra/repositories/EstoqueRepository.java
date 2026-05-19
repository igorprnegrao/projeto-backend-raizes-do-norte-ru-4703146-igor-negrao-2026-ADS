package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

	boolean existsByProdutoIdAndUnidadeId(UUID produtoId, UUID unidadeId);

	Optional<Estoque> findByProdutoIdAndUnidadeId(UUID produtoId, UUID unidadeId);

	@org.springframework.data.jpa.repository.Query(
		"SELECT e.quantidade FROM Estoque e WHERE e.produto.id = :idProduto AND e.unidade.id = :idUnidade"
	)
	Optional<Integer> findQuantidadeByProdutoIdAndUnidadeId(UUID idProduto, UUID idUnidade);
}
