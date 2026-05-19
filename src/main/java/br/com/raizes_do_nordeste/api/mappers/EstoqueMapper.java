package br.com.raizes_do_nordeste.api.mappers;

import br.com.raizes_do_nordeste.api.DTOs.request.EstoqueRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EstoqueResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Estoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstoqueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "unidade", ignore = true)
    Estoque toEntity(EstoqueRequestDTO dto);

    @Mapping(target = "idProduto", source = "produto.id")
    @Mapping(target = "nomeProduto", source = "produto.nome")
    @Mapping(target = "categoriaComida", source = "produto.categoriaComida")
    @Mapping(target = "idUnidade", source = "unidade.id")
    EstoqueResponseDTO toDto(Estoque estoque);
}
