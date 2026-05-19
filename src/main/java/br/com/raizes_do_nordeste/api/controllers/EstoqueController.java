package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.EstoqueRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EstoqueResponseDTO;
import br.com.raizes_do_nordeste.domain.services.EstoqueService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Estoque", description = "Gerenciamento de estoque por unidade")
@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService service;

    @Operation(summary = "Cadastrar estoque",
            description = "Registra o estoque de um produto em uma unidade específica. Requer perfil GERENTE.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estoque cadastrado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado – necessário perfil GERENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Produto ou unidade não encontrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T11:00:00",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada inválidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "quantidade",
                                      "message": "deve ser maior que zero"
                                    }
                                  ]
                                }
                                """)))
    })
    @PostMapping("/cadastro")
    public ResponseEntity<EstoqueResponseDTO> cadastrar(@RequestBody @Valid EstoqueRequestDTO request) {
        EstoqueResponseDTO response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Consultar quantidade em estoque",
            description = "Retorna a quantidade atual de um produto em uma unidade específica. Requer autenticação.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quantidade retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Estoque não encontrado para este produto/unidade",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/quantidade")
    public ResponseEntity<EstoqueResponseDTO> buscarQuantidade(
            @Parameter(description = "UUID do produto", required = true)
            @RequestParam UUID idProduto,
            @Parameter(description = "UUID da unidade", required = true)
            @RequestParam UUID idUnidade) {
        return ResponseEntity.ok(service.buscarQuantidade(idProduto, idUnidade));
    }
}
