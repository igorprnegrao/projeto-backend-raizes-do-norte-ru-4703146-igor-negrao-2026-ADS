package br.com.raizes_do_nordeste.api.controllers;

import br.com.raizes_do_nordeste.api.DTOs.request.LoginRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.request.PasswordChangeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.LoginResponseDTO;
import br.com.raizes_do_nordeste.domain.services.PasswordService;
import br.com.raizes_do_nordeste.infra.security.TokenConfig;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Autenticação", description = "Login JWT e troca de senha")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordService passwordService;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @Operation(summary = "Realizar login", description = "Autentica um usuário (equipe ou cliente) e retorna um token JWT Bearer.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso – retorna token JWT"),
        @ApiResponse(
                responseCode = "400",
                description = "Requisição malformada (JSON inválido ou corpo inválido)",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:20:00",
                                  "status": 400,
                                  "error": "Bad Request",
                                  "message": "Requisicao malformada."
                                }
                                """))
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Credenciais inválidas",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:21:00",
                                  "status": 401,
                                  "error": "Unauthorized",
                                  "message": "Credenciais invalidas."
                                }
                                """))
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Dados de entrada inválidos",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                        examples = @ExampleObject(value = """
                                {
                                  "timestamp": "2026-05-15T10:22:00",
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
                                """))
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        String jwt = tokenConfig.gerarToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(jwt, "Bearer"));
    }

    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha atual incorreta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid PasswordChangeRequestDTO request) {
        passwordService.changePassword(request);
        return ResponseEntity.noContent().build();
    }
}
