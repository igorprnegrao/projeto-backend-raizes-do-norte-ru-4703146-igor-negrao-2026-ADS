package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.PagamentoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PagamentoResponseDTO;
import br.com.raizes_do_nordeste.domain.services.PagamentoService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Pagamentos", description = "Simulação e consulta de pagamentos de pedidos")
@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService service;

    @Operation(summary = "Simular pagamento (mock)",
            description = "Simula o pagamento de um pedido. O valor informado deve corresponder ao valor total do pedido.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pagamento simulado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Valor do pagamento diverge do total do pedido",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T11:10:00",
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "O valor do pagamento deve ser igual ao valor total do pedido."
                                }
                                """))),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/simular")
    public ResponseEntity<PagamentoResponseDTO> simular(@RequestBody @Valid PagamentoRequestDTO request) {
        PagamentoResponseDTO response = service.simularPagamentoMock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar pagamento por pedido",
            description = "Retorna o pagamento associado a um pedido.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento/pedido não encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorPedido(
            @Parameter(description = "ID numérico do pedido") @PathVariable Long idPedido) {
        PagamentoResponseDTO response = service.buscarPorPedidoId(idPedido);
        return ResponseEntity.ok(response);
    }
}
