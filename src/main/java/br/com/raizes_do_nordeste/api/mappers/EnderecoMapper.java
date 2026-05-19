package br.com.raizes_do_nordeste.api.mappers;


import br.com.raizes_do_nordeste.api.DTOs.request.EnderecoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EnderecoResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "id", ignore = true)
    Endereco toEntity(EnderecoRequestDTO dto);

    EnderecoResponseDTO toDto(Endereco endereco);
}
