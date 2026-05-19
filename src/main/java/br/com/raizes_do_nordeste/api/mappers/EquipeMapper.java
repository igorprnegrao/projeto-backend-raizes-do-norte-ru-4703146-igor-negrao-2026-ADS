package br.com.raizes_do_nordeste.api.mappers;

import br.com.raizes_do_nordeste.api.DTOs.request.EquipeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EquipeResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Equipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "unidade", ignore = true)       // service define via unidadeRepository
    @Mapping(target = "perfilAcesso", ignore = true)  // service define via PerfilAcesso.builder()
    @Mapping(target = "passwordHash", ignore = true)  // service define via passwordEncoder
    @Mapping(target = "email", ignore = true)         // service define após sanitização
    @Mapping(target = "fonePrincipal", ignore = true) // service define após sanitização
    Equipe toEntity(EquipeRequestDTO dto);

    @Mapping(target = "unidadeId", source = "unidade.id")
    EquipeResponseDTO toDto(Equipe equipe);

}
