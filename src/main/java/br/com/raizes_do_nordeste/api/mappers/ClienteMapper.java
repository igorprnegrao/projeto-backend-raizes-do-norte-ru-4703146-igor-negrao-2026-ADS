package br.com.raizes_do_nordeste.api.mappers;


import br.com.raizes_do_nordeste.api.DTOs.request.ClienteRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "atendente", ignore = true)
    @Mapping(target = "dataRegistro", ignore = true)
    @Mapping(target = "saldoPontos", ignore = true)
    Cliente toEntity(ClienteRequestDTO dto);

    @Mapping(target = "idAtendente", source = "atendente.id")
    ClienteResponseDTO toDto(Cliente cliente);
}
