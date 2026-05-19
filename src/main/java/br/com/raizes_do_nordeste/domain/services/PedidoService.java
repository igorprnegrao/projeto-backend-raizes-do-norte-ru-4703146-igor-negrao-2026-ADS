package br.com.raizes_do_nordeste.domain.services;


import br.com.raizes_do_nordeste.api.DTOs.request.PedidoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteProdutoAdquiridoResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.MetaMensalUnidadeResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PedidoItemResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PedidoResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.TopProdutoConsumidoUnidadeResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.ItensPedido;
import br.com.raizes_do_nordeste.domain.entities.Pagamento;
import br.com.raizes_do_nordeste.domain.entities.Pedido;
import br.com.raizes_do_nordeste.domain.entities.Totem;
import br.com.raizes_do_nordeste.domain.enums.CategoriaComida;
import br.com.raizes_do_nordeste.domain.enums.StatusPagamento;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import br.com.raizes_do_nordeste.infra.exceptions.ClienteNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.EstoqueInsuficienteException;
import br.com.raizes_do_nordeste.infra.exceptions.EstoqueNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.EquipeNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.PagamentoValorInvalidoException;
import br.com.raizes_do_nordeste.infra.exceptions.ProdutoNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.ClienteRepository;
import br.com.raizes_do_nordeste.infra.repositories.EstoqueRepository;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import br.com.raizes_do_nordeste.infra.repositories.PagamentoRepository;
import br.com.raizes_do_nordeste.infra.repositories.PedidoRepository;
import br.com.raizes_do_nordeste.infra.repositories.ProdutoRepository;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import br.com.raizes_do_nordeste.infra.repositories.ItensPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ClienteRepository clienteRepository;
    private final UnidadeRepository unidadeRepository;
    private final EquipeRepository equipeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final ItensPedidoRepository itensPedidoRepository;
    private final TotemService totemService;

    @Transactional
    public PedidoResponseDTO cadastrar(PedidoRequestDTO dto) {
        var cliente = resolveCliente(dto);

        var unidade = unidadeRepository.findById(dto.idUnidade())
                .orElseThrow(() -> new UnidadeNotFoundException(dto.idUnidade().toString()));

        var atendente = dto.idAtendente() == null ? null : equipeRepository.findById(dto.idAtendente())
                .orElseThrow(() -> new EquipeNotFoundException(dto.idAtendente().toString()));

        Totem totem = dto.idTotem() == null ? null : totemService.buscarAtivoDaUnidade(dto.idTotem(), unidade.getId());

        Pedido pedido = Pedido.builder()
                .canalPedido(dto.canalPedido())
                .statusPedido(dto.statusPedido() == null ? StatusPedido.EM_PREPARO : dto.statusPedido())
                .cliente(cliente)
                .unidade(unidade)
                .atendente(atendente)
                .totem(totem)
                .build();

        List<ItensPedido> itens = dto.itens().stream().map(itemDto -> {
            var produto = produtoRepository.findById(itemDto.idProduto())
                    .orElseThrow(() -> new ProdutoNotFoundException(itemDto.idProduto().toString()));

            var estoque = estoqueRepository.findByProdutoIdAndUnidadeId(itemDto.idProduto(), unidade.getId())
                    .orElseThrow(() -> new EstoqueNotFoundException(itemDto.idProduto(), unidade.getId()));

            Integer disponivel = estoque.getQuantidade() == null ? 0 : estoque.getQuantidade();
            if (disponivel < itemDto.quantidade()) {
                throw new EstoqueInsuficienteException(itemDto.idProduto(), unidade.getId(), itemDto.quantidade(), disponivel);
            }

            estoque.setQuantidade(disponivel - itemDto.quantidade());

            return ItensPedido.builder()
                    .quantidade(itemDto.quantidade())
                    .precoMomento(produto.getPrecoUnitario())
                    .produto(produto)
                    .pedido(pedido)
                    .build();
        }).toList();

        pedido.setItensPedido(itens);

        BigDecimal valorTotalCalculado = itens.stream()
                .map(i -> i.getPrecoMomento().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(valorTotalCalculado);

        int pontosGanhos = valorTotalCalculado.intValue();
        int saldoAtual = cliente.getSaldoPontos() == null ? 0 : cliente.getSaldoPontos();
        cliente.setSaldoPontos(saldoAtual + Math.max(pontosGanhos, 0));
        clienteRepository.save(cliente);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        BigDecimal valorPago = dto.pagamento().valorPago() == null
                ? valorTotalCalculado
                : dto.pagamento().valorPago();

        validarValorPagamento(valorPago, valorTotalCalculado, pedidoSalvo.getId());

        Pagamento pagamentoSalvo = pagamentoRepository.save(Pagamento.builder()
                .pedido(pedidoSalvo)
                .valorPago(valorPago)
                .metodoPagamento(dto.pagamento().metodoPagamento())
                .statusPagamento(dto.pagamento().statusPagamento() == null
                        ? StatusPagamento.APROVADO
                        : dto.pagamento().statusPagamento())
                .build());

        List<PedidoItemResponseDTO> itensResponse = pedidoSalvo.getItensPedido().stream()
                .map(i -> new PedidoItemResponseDTO(i.getProduto().getId(), i.getQuantidade(), i.getPrecoMomento()))
                .toList();

        return new PedidoResponseDTO(
                pedidoSalvo.getId(),
                pedidoSalvo.getRegistroData(),
                pedidoSalvo.getCanalPedido(),
                pedidoSalvo.getStatusPedido(),
                pedidoSalvo.getValorTotal(),
                pedidoSalvo.getCliente().getId(),
                pedidoSalvo.getUnidade().getId(),
                pedidoSalvo.getAtendente() == null ? null : pedidoSalvo.getAtendente().getId(),
                itensResponse,
                pagamentoSalvo.getId()
        );
    }

    @Transactional(readOnly = true)
    public long buscarTotalPedidosUltimas24h() {
        LocalDateTime inicioJanela = LocalDateTime.now().minusHours(24);
        return pedidoRepository.countByRegistroDataAfter(inicioJanela);
    }

    @Transactional(readOnly = true)
    public Page<PedidoResponseDTO> listarPedidosUltimas24h(Pageable pageable) {
        LocalDateTime inicioJanela = LocalDateTime.now().minusHours(24);

        // 1 query — EntityGraph eager-fetches cliente, unidade e atendente (many-to-one seguros com paginação)
        Page<Pedido> page = pedidoRepository.findByRegistroDataAfter(inicioJanela, pageable);

        if (page.isEmpty()) {
            return page.map(p -> toResponse(p, Map.of()));
        }

        // 1 query — carrega todos os pagamentos dos pedidos desta página de uma vez
        List<Long> pedidoIds = page.stream().map(Pedido::getId).toList();
        Map<Long, UUID> pagamentoPorPedido = pagamentoRepository
                .findAllByPedidoIdIn(pedidoIds)
                .stream()
                .collect(Collectors.toMap(
                        pag -> pag.getPedido().getId(),
                        Pagamento::getId
                ));

        // itensPedido são carregados lazily, mas em lote via @BatchSize(size=30) da coleção
        // produtos dos itens também são carregados em lote via @BatchSize(size=30) na entidade Produto
        return page.map(pedido -> toResponse(pedido, pagamentoPorPedido));
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarValorTotalLucradoHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.plusDays(1).atStartOfDay();
        return pedidoRepository.somarValorTotalPorPeriodo(inicio, fim);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarResumoVendasHoje() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.plusDays(1).atStartOfDay();
        long totalPedidos = pedidoRepository.countByRegistroDataBetween(inicio, fim);
        BigDecimal valorTotal = pedidoRepository.somarValorTotalPorPeriodo(inicio, fim);
        return Map.of(
                "data", hoje.toString(),
                "totalPedidos", totalPedidos,
                "valorTotalArrecadado", valorTotal
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarResumoVendasHojePorUnidade(UUID idUnidade) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.plusDays(1).atStartOfDay();
        long totalPedidos = pedidoRepository.countByRegistroDataBetweenAndUnidade(inicio, fim, idUnidade);
        BigDecimal valorTotal = pedidoRepository.somarValorTotalPorPeriodoEUnidade(inicio, fim, idUnidade);
        return Map.of(
                "data", hoje.toString(),
                "idUnidade", idUnidade.toString(),
                "totalPedidos", totalPedidos,
                "valorTotalArrecadado", valorTotal
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarValorTotalLucradoUltimas24h() {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusHours(24);
        return pedidoRepository.somarValorTotalPorPeriodo(inicio, fim);
    }

    @Transactional(readOnly = true)
    public MetaMensalUnidadeResponseDTO verificarMetaMensalUnidade(UUID idUnidade) {
        var unidade = unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new UnidadeNotFoundException(idUnidade.toString()));

        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        BigDecimal valorArrecadado = pedidoRepository.somarValorTotalPorPeriodoEUnidade(
                inicioMes.atStartOfDay(),
                fimMes.plusDays(1).atStartOfDay(),
                idUnidade
        );

        BigDecimal metaMensal = unidade.getMetaDesempenhoVenda() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(unidade.getMetaDesempenhoVenda());

        boolean metaConfigurada = metaMensal.compareTo(BigDecimal.ZERO) > 0;
        boolean bateuMeta = metaConfigurada && valorArrecadado.compareTo(metaMensal) >= 0;
        BigDecimal percentualAtingido = metaConfigurada
                ? valorArrecadado.multiply(BigDecimal.valueOf(100)).divide(metaMensal, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new MetaMensalUnidadeResponseDTO(
                idUnidade,
                inicioMes,
                fimMes,
                metaMensal,
                valorArrecadado,
                percentualAtingido,
                bateuMeta
        );
    }

    @Transactional(readOnly = true)
    public List<TopProdutoConsumidoUnidadeResponseDTO> buscarTop10ProdutosConsumidosPorUnidade(UUID idUnidade) {
        unidadeRepository.findById(idUnidade)
                .orElseThrow(() -> new UnidadeNotFoundException(idUnidade.toString()));

        return itensPedidoRepository
                .buscarTopProdutosConsumidosPorUnidade(idUnidade, StatusPedido.CANCELADO, PageRequest.of(0, 10))
                .stream()
                .map(row -> new TopProdutoConsumidoUnidadeResponseDTO(
                        (UUID) row[0],
                        (String) row[1],
                        (CategoriaComida) row[2],
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClienteProdutoAdquiridoResponseDTO> listarProdutosAdquiridosDoClienteAutenticado(Pageable pageable) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        var cliente = clienteRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ClienteNotFoundException("email: " + emailAutenticado));

        return itensPedidoRepository
                .listarProdutosAdquiridosPorCliente(cliente.getId(), StatusPedido.CANCELADO, pageable)
                .map(row -> new ClienteProdutoAdquiridoResponseDTO(
                        (UUID) row[0],
                        (String) row[1],
                        (CategoriaComida) row[2],
                        ((Number) row[3]).longValue()
                ));
    }

    private void validarValorPagamento(BigDecimal valorPago, BigDecimal valorTotalPedido, Long idPedido) {
        if (valorPago.compareTo(valorTotalPedido) != 0) {
            throw new PagamentoValorInvalidoException(idPedido, valorPago, valorTotalPedido);
        }
    }

    private PedidoResponseDTO toResponse(Pedido pedido, Map<Long, UUID> pagamentoPorPedido) {
        List<PedidoItemResponseDTO> itensResponse = pedido.getItensPedido().stream()
                .map(i -> new PedidoItemResponseDTO(i.getProduto().getId(), i.getQuantidade(), i.getPrecoMomento()))
                .toList();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getRegistroData(),
                pedido.getCanalPedido(),
                pedido.getStatusPedido(),
                pedido.getValorTotal(),
                pedido.getCliente().getId(),
                pedido.getUnidade().getId(),
                pedido.getAtendente() == null ? null : pedido.getAtendente().getId(),
                itensResponse,
                pagamentoPorPedido.get(pedido.getId())
        );
    }

    private br.com.raizes_do_nordeste.domain.entities.Cliente resolveCliente(PedidoRequestDTO dto) {
        if (dto.idCliente() != null) {
            return clienteRepository.findById(dto.idCliente())
                    .orElseThrow(() -> new ClienteNotFoundException(dto.idCliente().toString()));
        }

        String fone = dto.foneCliente() == null ? "" : dto.foneCliente().replaceAll("\\D", "");
        if (fone.isBlank()) {
            throw new ClienteNotFoundException("telefone não informado");
        }

        return clienteRepository.findByFonePrincipal(fone)
                .orElseThrow(() -> new ClienteNotFoundException("telefone: " + dto.foneCliente()));
    }
}
