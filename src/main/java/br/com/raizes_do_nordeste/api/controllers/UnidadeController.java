package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.UnidadeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.UnidadeResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.update.UnidadeUpdateDTO;
import br.com.raizes_do_nordeste.domain.services.UnidadeService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Tag(name = "Unidades", description = "Cadastro de unidades físicas do restaurante (endpoint público para inicialização)")
@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService service;

    @Operation(summary = "Cadastrar unidade",
            description = "Cria uma nova unidade física do restaurante. Endpoint público – necessário antes do bootstrap do gerente.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Unidade criada com sucesso"),
        @ApiResponse(
                responseCode = "409",
                description = "Unidade já cadastrada com este e-mail ou telefone",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T00:19:03.651754",
                                  "status": 409,
                                  "error": "Conflict",
                                  "message": "O contato '55(85)98888-7777' ja esta em uso."
                                }
                                """)
                )
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Dados inválidos",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T00:20:10.120001",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada invalidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "email",
                                      "message": "O e-mail deve ser valido"
                                    },
                                    {
                                      "field": "foneSecundarioCelular",
                                      "message": "O numero do telefone e obrigatorio"
                                    }
                                  ]
                                }
                                """)
                )
        )
    })
    @PostMapping("/cadastro")
    public ResponseEntity<UnidadeResponseDTO> cadastrar(@RequestBody @Valid UnidadeRequestDTO unidadeRequestDTO) {
        UnidadeResponseDTO response = service.cadastrar(unidadeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar unidade",
            description = "Atualiza integralmente os dados da unidade informada.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrados",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{idUnidade}")
    public ResponseEntity<UnidadeResponseDTO> atualizar(
            @Parameter(description = "UUID da unidade") @PathVariable UUID idUnidade,
            @RequestBody @Valid UnidadeRequestDTO unidadeRequestDTO) {
        UnidadeResponseDTO response = service.atualizar(idUnidade, unidadeRequestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar parcialmente unidade",
            description = "Atualiza parcialmente os dados da unidade informada. Somente os campos enviados serao alterados.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unidade atualizada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "403", description = "Sem permissão",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrados",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/{idUnidade}")
    public ResponseEntity<UnidadeResponseDTO> atualizarParcial(
            @Parameter(description = "UUID da unidade") @PathVariable UUID idUnidade,
            @RequestBody @Valid UnidadeUpdateDTO unidadeUpdateDTO) {
        UnidadeResponseDTO response = service.atualizarParcial(idUnidade, unidadeUpdateDTO);
        return ResponseEntity.ok(response);
    }
}
