package br.com.raizes_do_nordeste.infra.repositories;

import br.com.raizes_do_nordeste.domain.entities.ItensPedido;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItensPedidoRepository extends JpaRepository<ItensPedido, UUID> {

    @Query("""
            SELECT ip.produto.id,
                   ip.produto.nome,
                   ip.produto.categoriaComida,
                   COALESCE(SUM(ip.quantidade), 0)
            FROM ItensPedido ip
            WHERE ip.pedido.unidade.id = :idUnidade
              AND ip.pedido.statusPedido <> :statusExcluido
            GROUP BY ip.produto.id, ip.produto.nome, ip.produto.categoriaComida
            ORDER BY SUM(ip.quantidade) DESC
            """)
    List<Object[]> buscarTopProdutosConsumidosPorUnidade(
            @Param("idUnidade") UUID idUnidade,
            @Param("statusExcluido") StatusPedido statusExcluido,
            Pageable pageable
    );

    @Query(value = """
            SELECT ip.produto.id,
                   ip.produto.nome,
                   ip.produto.categoriaComida,
                   COALESCE(SUM(ip.quantidade), 0)
            FROM ItensPedido ip
            WHERE ip.pedido.cliente.id = :idCliente
              AND ip.pedido.statusPedido <> :statusExcluido
            GROUP BY ip.produto.id, ip.produto.nome, ip.produto.categoriaComida
            """,
            countQuery = """
            SELECT COUNT(DISTINCT ip.produto.id)
            FROM ItensPedido ip
            WHERE ip.pedido.cliente.id = :idCliente
              AND ip.pedido.statusPedido <> :statusExcluido
            """)
    Page<Object[]> listarProdutosAdquiridosPorCliente(
            @Param("idCliente") UUID idCliente,
            @Param("statusExcluido") StatusPedido statusExcluido,
            Pageable pageable
    );
}
