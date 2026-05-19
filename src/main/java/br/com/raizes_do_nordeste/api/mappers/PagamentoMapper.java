package br.com.raizes_do_nordeste.api.mappers;

import br.com.raizes_do_nordeste.api.DTOs.request.PagamentoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.PagamentoResponseDTO;
import br.com.raizes_do_nordeste.domain.entities.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(target = "id", ignore = true)
    Pagamento toEntity(PagamentoRequestDTO dto);

    PagamentoResponseDTO toDto(Pagamento pagamento);
}
