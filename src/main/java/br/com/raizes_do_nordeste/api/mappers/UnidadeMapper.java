package br.com.raizes_do_nordeste.api.mappers;


import br.com.raizes_do_nordeste.api.DTOs.request.UnidadeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.UnidadeResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Unidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EnderecoMapper.class)
public interface UnidadeMapper {

    @Mapping(target = "id", ignore = true)
    Unidade toEntity(UnidadeRequestDTO dto);

    UnidadeResponseDTO toDto(Unidade unidade);
}


