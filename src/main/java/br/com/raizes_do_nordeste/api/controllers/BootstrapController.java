package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.BootstrapGerenteRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.request.EquipeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EquipeResponseDTO;
import br.com.raizes_do_nordeste.domain.enums.TipoPerfil;
import br.com.raizes_do_nordeste.domain.services.EquipeService;
import br.com.raizes_do_nordeste.infra.exceptions.BootstrapJaInicializadoException;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "Bootstrap", description = "Inicialização do sistema – cadastro do primeiro gerente (endpoint público, use apenas uma vez)")
@RestController
@RequestMapping("/bootstrap")
@RequiredArgsConstructor
public class BootstrapController {

    private final EquipeService equipeService;
    private final EquipeRepository equipeRepository;

    @Operation(summary = "Cadastrar primeiro gerente",
            description = "Cria o gerente inicial do sistema. Lança 409 se já existir um gerente cadastrado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Gerente criado com sucesso"),
        @ApiResponse(
                responseCode = "409",
                description = "Bootstrap já concluído – gerente já existe",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:30:00",
                                  "status": 409,
                                  "error": "Conflict",
                                  "message": "Bootstrap ja concluido: o primeiro gerente ja foi criado."
                                }
                                """))
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Dados inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:31:00",
                                  "status": 422,
                                  "error": "Unprocessable Entity",
                                  "message": "Dados de entrada inválidos.",
                                  "fieldErrors": [
                                    {
                                      "field": "idUnidade",
                                      "message": "nao deve ser nulo"
                                    }
                                  ]
                                }
                                """))
        )
    })
    @PostMapping("/gerente")
    public ResponseEntity<EquipeResponseDTO> cadastrarPrimeiroGerente(
            @RequestBody @Valid BootstrapGerenteRequestDTO request
    ) {
        boolean gerenteJaExiste = equipeRepository.findAll().stream()
                .anyMatch(e -> e.getPerfilAcesso() != null
                        && e.getPerfilAcesso().getTipoPerfil() == TipoPerfil.GERENTE);

        if (gerenteJaExiste) {
            throw new BootstrapJaInicializadoException();
        }

        EquipeRequestDTO dto = new EquipeRequestDTO(
                request.nomeCompleto(),
                request.fonePrincipal(),
                request.email(),
                request.senha(),
                TipoPerfil.GERENTE,
                request.idUnidade()
        );

        EquipeResponseDTO response = equipeService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
