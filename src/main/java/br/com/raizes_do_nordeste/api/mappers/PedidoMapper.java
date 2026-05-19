package br.com.raizes_do_nordeste.api.mappers;

import br.com.raizes_do_nordeste.api.DTOs.request.PedidoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PedidoResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    Pedido toEntity(PedidoRequestDTO dto);

    PedidoResponseDTO toDto(Pedido pedido);
}
