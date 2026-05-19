package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.PedidoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PedidoResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.MetaMensalUnidadeResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.TopProdutoConsumidoUnidadeResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteProdutoAdquiridoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import br.com.raizes_do_nordeste.domain.services.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Pedidos", description = "Criação e consultas de pedidos (web, app, totem e atendente)")
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @Operation(summary = "Criar pedido",
            description = "Registra um novo pedido. O pagamento deve ser realizado no mesmo momento. Requer autenticação.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão para criar pedido",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Unidade ou produto inexistente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "409", description = "Sem estoque disponível para um ou mais itens",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T11:20:00",
                                  "status": 409,
                                  "error": "Conflict",
                                  "message": "Estoque insuficiente para o produto solicitado."
                                }
                                """))),
        @ApiResponse(responseCode = "422", description = "Request inválido (erro de validação)",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T11:21:00",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada inválidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "itens",
                                      "message": "A lista de itens e obrigatoria"
                                    }
                                  ]
                                }
                                """)))
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> cadastrar(@RequestBody @Valid PedidoRequestDTO request) {
        PedidoResponseDTO response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Resumo de vendas do dia",
            description = "Retorna a quantidade de pedidos e o valor total arrecadado no dia de hoje (00h–23h59). " +
                    "Se `idUnidade` for informado, filtra apenas os pedidos daquela unidade.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/resumo-hoje")
    public ResponseEntity<Map<String, Object>> buscarResumoVendasHoje(
            @Parameter(description = "UUID da unidade (opcional). Quando informado, filtra os pedidos da unidade.")
            @RequestParam(required = false) UUID idUnidade) {
        if (idUnidade != null) {
            return ResponseEntity.ok(service.buscarResumoVendasHojePorUnidade(idUnidade));
        }
        return ResponseEntity.ok(service.buscarResumoVendasHoje());
    }

    @Operation(summary = "Total de pedidos nas últimas 24h",
            description = "Retorna a quantidade total de pedidos realizados nas últimas 24 horas.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso")
    @GetMapping("/quantidade-24h")
    public ResponseEntity<Map<String, Long>> buscarQuantidadeUltimas24h() {
        long total = service.buscarTotalPedidosUltimas24h();
        return ResponseEntity.ok(Map.of("totalPedidos24h", total));
    }

    @Operation(summary = "Faturamento do dia (hoje)",
            description = "Retorna o valor total faturado nos pedidos de hoje.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Valor retornado com sucesso")
    @GetMapping("/faturamento-hoje")
    public ResponseEntity<Map<String, BigDecimal>> buscarFaturamentoHoje() {
        BigDecimal valorTotal = service.buscarValorTotalLucradoHoje();
        return ResponseEntity.ok(Map.of("valorTotalHoje", valorTotal));
    }

    @Operation(summary = "Faturamento nas últimas 24h",
            description = "Retorna o valor total faturado nas últimas 24 horas.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Valor retornado com sucesso")
    @GetMapping("/faturamento-24h")
    public ResponseEntity<Map<String, BigDecimal>> buscarFaturamentoUltimas24h() {
        BigDecimal valorTotal = service.buscarValorTotalLucradoUltimas24h();
        return ResponseEntity.ok(Map.of("valorTotal24h", valorTotal));
    }

    @Operation(summary = "Listar pedidos das últimas 24h (paginado)",
            description = "Retorna uma página de pedidos das últimas 24 horas. Use os parâmetros `page`, `size` e `sort`.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lista paginada retornada com sucesso")
    @GetMapping("/page-24h")
    public ResponseEntity<Page<PedidoResponseDTO>> listarPedidosUltimas24h(Pageable pageable) {
        return ResponseEntity.ok(service.listarPedidosUltimas24h(pageable));
    }

    @Operation(summary = "Meta mensal da unidade",
            description = "Verifica se a unidade bateu a meta mensal de valor arrecadado no mês atual.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resumo de meta mensal retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/meta-mes")
    public ResponseEntity<MetaMensalUnidadeResponseDTO> verificarMetaMensalUnidade(
            @Parameter(description = "UUID da unidade", required = true)
            @RequestParam UUID idUnidade) {
        return ResponseEntity.ok(service.verificarMetaMensalUnidade(idUnidade));
    }

    @Operation(summary = "Top 10 produtos mais consumidos da unidade",
            description = "Retorna os 10 produtos mais consumidos da unidade, ordenados por quantidade vendida (exclui pedidos cancelados).",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/top-produtos-consumidos")
    public ResponseEntity<List<TopProdutoConsumidoUnidadeResponseDTO>> buscarTopProdutosConsumidos(
            @Parameter(description = "UUID da unidade", required = true)
            @RequestParam UUID idUnidade) {
        return ResponseEntity.ok(service.buscarTop10ProdutosConsumidosPorUnidade(idUnidade));
    }

    @Operation(summary = "Produtos adquiridos pelo cliente",
            description = "Retorna a lista de produtos já adquiridos pelo cliente autenticado (consolidados por produto). " +
                    "Aceita `page`, `size` e `sort`.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/meus-produtos")
    public ResponseEntity<Page<ClienteProdutoAdquiridoResponseDTO>> listarMeusProdutosAdquiridos(Pageable pageable) {
        return ResponseEntity.ok(service.listarProdutosAdquiridosDoClienteAutenticado(pageable));
    }
}
