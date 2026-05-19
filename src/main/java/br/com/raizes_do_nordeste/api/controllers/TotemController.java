package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.TotemRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.TotemResponseDTO;
import br.com.raizes_do_nordeste.domain.services.TotemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import java.util.UUID;

@Tag(name = "Totens", description = "Cadastro e validação de totens de autoatendimento por unidade")
@RestController
@RequestMapping("/totens")
@RequiredArgsConstructor
public class TotemController {

    private final TotemService service;

    @Operation(summary = "Cadastrar totem",
            description = "Registra um novo totem vinculado a uma unidade. Requer perfil GERENTE.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Totem cadastrado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado – necessário perfil GERENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PostMapping("/cadastro")
    public ResponseEntity<TotemResponseDTO> cadastrar(@RequestBody @Valid TotemRequestDTO request) {
        TotemResponseDTO response = service.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Validar totem de uma unidade",
            description = "Verifica se o totem informado está ativo e pertence à unidade informada.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Totem válido e ativo"),
        @ApiResponse(responseCode = "404", description = "Totem não encontrado ou não pertence à unidade",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{idTotem}/validar-unidade/{idUnidade}")
    public ResponseEntity<Map<String, Object>> validarTotemDaUnidade(
            @Parameter(description = "UUID do totem") @PathVariable UUID idTotem,
            @Parameter(description = "UUID da unidade") @PathVariable UUID idUnidade
    ) {
        var totem = service.buscarAtivoDaUnidade(idTotem, idUnidade);
        return ResponseEntity.ok(Map.of(
                "idTotem", totem.getId(),
                "idUnidade", idUnidade,
                "valido", true
        ));
    }
}
