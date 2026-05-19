package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.ClienteRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteExtratoPontosResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClientePontosResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.update.ClienteUpdateDTO;
import br.com.raizes_do_nordeste.domain.services.ClienteService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Clientes", description = "Cadastro e atualização de clientes do sistema")
@RestController
@RequestMapping("/usuarios/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @Operation(summary = "Cadastrar cliente",
            description = "Cria um novo cliente. Pode ser realizado pelo atendente ou pelo próprio cliente (auto-cadastro). Endpoint público.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:40:00",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada inválidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "email",
                                      "message": "O e-mail deve ser válido"
                                    }
                                  ]
                                }
                                """)))
    })
    @PostMapping("/cadastro")
    public ResponseEntity<ClienteResponseDTO> cadastro(@RequestBody @Valid ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO response = service.cadastrar(clienteRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar cliente",
            description = "Atualiza parcialmente os dados de um cliente existente.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cliente atualizado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/{idCliente}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @Parameter(description = "UUID do cliente") @PathVariable UUID idCliente,
            @RequestBody ClienteUpdateDTO clienteUpdateDTO
    ) {
        ClienteResponseDTO response = service.atualizar(idCliente, clienteUpdateDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar meus pontos",
            description = "Retorna o saldo de pontos do cliente autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissao",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/me/pontos")
    public ResponseEntity<ClientePontosResponseDTO> consultarMeusPontos() {
        return ResponseEntity.ok(service.consultarSaldoPontosClienteAutenticado());
    }

    @Operation(summary = "Extrato de pontos",
            description = "Retorna o saldo atual e o extrato de lançamentos de pontos do cliente autenticado.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/me/pontos/extrato")
    public ResponseEntity<ClienteExtratoPontosResponseDTO> consultarExtratoPontos(Pageable pageable) {
        return ResponseEntity.ok(service.consultarExtratoPontosClienteAutenticado(pageable));
    }
}
