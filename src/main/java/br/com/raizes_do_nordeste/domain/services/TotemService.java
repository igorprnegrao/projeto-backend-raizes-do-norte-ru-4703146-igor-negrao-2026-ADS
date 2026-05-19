package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.TotemRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.TotemResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Totem;
import br.com.raizes_do_nordeste.domain.enums.StatusMaquina;
import br.com.raizes_do_nordeste.infra.exceptions.TotemAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.TotemInativoException;
import br.com.raizes_do_nordeste.infra.exceptions.TotemNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.TotemUnidadeInvalidaException;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.TotemRepository;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TotemService {

    private final TotemRepository totemRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public TotemResponseDTO cadastrar(TotemRequestDTO dto) {
        if (totemRepository.existsByCodigoIdentificador(dto.codigoIdentificador())) {
            throw new TotemAlreadyExistsException(dto.codigoIdentificador());
        }

        var unidade = unidadeRepository.findById(dto.idUnidade())
                .orElseThrow(() -> new UnidadeNotFoundException(dto.idUnidade().toString()));

        Totem totem = Totem.builder()
                .codigoIdentificador(dto.codigoIdentificador())
                .tokenAutenticacao(dto.tokenAutenticacao())
                .statusMaquina(dto.statusMaquina())
                .unidade(unidade)
                .build();

        Totem salvo = totemRepository.save(totem);
        return new TotemResponseDTO(salvo.getId(), salvo.getCodigoIdentificador(), salvo.getStatusMaquina(), unidade.getId());
    }

    public Totem buscarAtivoPorId(UUID idTotem) {
        Totem totem = totemRepository.findById(idTotem)
                .orElseThrow(() -> new TotemNotFoundException(idTotem.toString()));

        if (totem.getStatusMaquina() != StatusMaquina.ATIVO) {
            throw new TotemInativoException(idTotem.toString());
        }

        return totem;
    }

    public Totem buscarAtivoDaUnidade(UUID idTotem, UUID idUnidade) {
        Totem totem = buscarAtivoPorId(idTotem);

        if (totem.getUnidade() == null || !totem.getUnidade().getId().equals(idUnidade)) {
            throw new TotemUnidadeInvalidaException(idTotem, idUnidade);
        }

        return totem;
    }
}

