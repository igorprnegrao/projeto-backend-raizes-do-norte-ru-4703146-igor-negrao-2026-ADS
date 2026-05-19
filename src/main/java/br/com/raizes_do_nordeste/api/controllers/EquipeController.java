package br.com.raizes_do_nordeste.api.controllers;


import br.com.raizes_do_nordeste.api.DTOs.request.EquipeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EquipeResponseDTO;
import br.com.raizes_do_nordeste.domain.services.EquipeService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Equipe", description = "Cadastro de membros da equipe (Gerente, Atendente, Cozinheiro)")
@RestController
@RequestMapping("/usuarios/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService service;

    @Operation(summary = "Cadastrar membro da equipe",
            description = "Cria um novo membro da equipe vinculado a uma unidade. Requer perfil GERENTE.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Membro cadastrado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado – necessário perfil GERENTE",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "409", description = "E-mail ou telefone já cadastrado",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "422", description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:50:00",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada inválidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "tipoPerfil",
                                      "message": "Tipo de perfil inválido"
                                    }
                                  ]
                                }
                                """)))
    })
    @PostMapping("/cadastro")
    public ResponseEntity<EquipeResponseDTO>  cadastro(@RequestBody @Valid EquipeRequestDTO equipeRequestDTO) {
        EquipeResponseDTO response = service.cadastrar(equipeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
