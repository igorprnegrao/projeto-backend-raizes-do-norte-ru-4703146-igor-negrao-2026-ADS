package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.EquipeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EquipeResponseDTO;
import br.com.raizes_do_nordeste.api.mappers.EquipeMapper;
import br.com.raizes_do_nordeste.domain.entities.Equipe;
import br.com.raizes_do_nordeste.domain.entities.PerfilAcesso;
import br.com.raizes_do_nordeste.domain.entities.Unidade;
import br.com.raizes_do_nordeste.infra.exceptions.ContatoAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.EmailAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeRepository repository;
    private final UnidadeRepository unidadeRepository;
    private final EquipeMapper mapper;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public EquipeResponseDTO cadastrar(EquipeRequestDTO equipeRequestDTO) {

        DadosSanitizados dados = sanitizarValidar(equipeRequestDTO);

        Equipe equipe = mapper.toEntity(equipeRequestDTO);

        equipe.setFonePrincipal(dados.contatoLimpo1());
        equipe.setEmail(dados.emailLimpo());
        equipe.setPasswordHash(passwordEncoder.encode(equipeRequestDTO.senha()));

        //Definir Perfil de acesso para equipe

        PerfilAcesso perfilAcesso = PerfilAcesso.builder()
                .tipoPerfil(equipeRequestDTO.tipoPerfil())
                .build();

        equipe.setPerfilAcesso(perfilAcesso);

        Unidade unidade = null;
        if (equipeRequestDTO.idUnidade() != null) {
            unidade = unidadeRepository.findById(equipeRequestDTO.idUnidade())
                    .orElseThrow(() -> new UnidadeNotFoundException(equipeRequestDTO.idUnidade().toString()));
        }
        equipe.setUnidade(unidade);

        Equipe equipeSalva = repository.save(equipe);

        return mapper.toDto(equipeSalva);

    }


    private DadosSanitizados sanitizarValidar(EquipeRequestDTO dto) {

        String contatoLimpo1 = (dto.fonePrincipal() != null)
                ? dto.fonePrincipal().replaceAll("\\D", "") : "";


        String emailLimpo = dto.email().trim().toLowerCase();

        if (repository.existsByFonePrincipal(contatoLimpo1)) {
            throw new ContatoAlreadyExistsException(dto.fonePrincipal());
        }

        if (repository.existsByEmail(emailLimpo)) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        return new DadosSanitizados(contatoLimpo1, emailLimpo);
    }

    /**
     * Record para armazenar dados sanitizados
     */
    private record DadosSanitizados(String contatoLimpo1, String emailLimpo) {
    }
}
