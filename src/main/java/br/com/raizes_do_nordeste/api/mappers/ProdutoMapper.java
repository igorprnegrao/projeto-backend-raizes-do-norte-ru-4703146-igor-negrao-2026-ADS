package br.com.raizes_do_nordeste.api.mappers;


import br.com.raizes_do_nordeste.api.DTOs.request.ProdutoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ProdutoResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "id", ignore = true)
    Produto toEntity(ProdutoRequestDTO dto);

    @Mapping(target = "estoque", ignore = true)
    ProdutoResponseDTO toDto(Produto produto);
}
