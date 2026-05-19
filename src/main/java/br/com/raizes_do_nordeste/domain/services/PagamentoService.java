package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.PagamentoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PagamentoResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Pagamento;
import br.com.raizes_do_nordeste.domain.enums.MetodoPagamento;
import br.com.raizes_do_nordeste.domain.enums.StatusPagamento;
import br.com.raizes_do_nordeste.infra.exceptions.PagamentoAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.PagamentoNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.PedidoNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.PagamentoValorInvalidoException;
import br.com.raizes_do_nordeste.infra.repositories.PagamentoRepository;
import br.com.raizes_do_nordeste.infra.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public PagamentoResponseDTO simularPagamentoMock(PagamentoRequestDTO dto) {
        var pedido = pedidoRepository.findById(dto.idPedido())
                .orElseThrow(() -> new PedidoNotFoundException(dto.idPedido()));

        if (pagamentoRepository.existsByPedidoId(dto.idPedido())) {
            throw new PagamentoAlreadyExistsException(dto.idPedido());
        }

        BigDecimal valorPago = dto.valorPago();
        BigDecimal valorTotalPedido = pedido.getValorTotal() == null ? BigDecimal.ZERO : pedido.getValorTotal();
        validarValorPago(valorPago, valorTotalPedido, dto.idPedido());

        StatusPagamento status = resolverStatusMock(dto.metodoPagamento(), dto.aprovar());

        Pagamento salvo = pagamentoRepository.save(Pagamento.builder()
                .pedido(pedido)
                .valorPago(valorPago)
                .metodoPagamento(dto.metodoPagamento())
                .statusPagamento(status)
                .build());

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDTO buscarPorPedidoId(Long idPedido) {
        Pagamento pagamento = pagamentoRepository.findByPedidoId(idPedido)
                .orElseThrow(() -> new PagamentoNotFoundException(idPedido));
        return toResponse(pagamento);
    }

    private void validarValorPago(BigDecimal valorPago, BigDecimal valorTotalPedido, Long idPedido) {
        if (valorPago.compareTo(valorTotalPedido) != 0) {
            throw new PagamentoValorInvalidoException(idPedido, valorPago, valorTotalPedido);
        }
    }

    private StatusPagamento resolverStatusMock(MetodoPagamento metodo, Boolean aprovar) {
        if (aprovar != null) {
            return aprovar ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;
        }

        int chanceAprovacao = switch (metodo) {
            case DINHEIRO -> 100;
            case PIX, CARTAO_DEBITO, CARTAO_CREDITO -> 85;
            case VALE_ALIMENTACAO -> 75;
        };

        int sorteio = ThreadLocalRandom.current().nextInt(100);
        return sorteio < chanceAprovacao ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;
    }

    private PagamentoResponseDTO toResponse(Pagamento pagamento) {
        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getPedido().getId(),
                pagamento.getValorPago(),
                pagamento.getDataPagamento(),
                pagamento.getMetodoPagamento(),
                pagamento.getStatusPagamento()
        );
    }
}

