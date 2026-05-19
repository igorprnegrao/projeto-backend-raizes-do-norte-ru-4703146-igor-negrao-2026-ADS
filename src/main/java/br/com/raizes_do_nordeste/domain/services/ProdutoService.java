package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.ProdutoRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EstoqueResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ProdutoResponseDTO;
import br.com.raizes_do_nordeste.api.mappers.EstoqueMapper;
import br.com.raizes_do_nordeste.api.mappers.ProdutoMapper;
import br.com.raizes_do_nordeste.domain.entities.Estoque;
import br.com.raizes_do_nordeste.domain.entities.Produto;
import br.com.raizes_do_nordeste.domain.entities.Unidade;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.EstoqueRepository;
import br.com.raizes_do_nordeste.infra.repositories.ProdutoRepository;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;
    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final EstoqueMapper estoqueMapper;

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {

        Unidade unidade = unidadeRepository.findById(dto.idUnidade())
                .orElseThrow(() -> new UnidadeNotFoundException(dto.idUnidade().toString()));

        Produto produtoSalvo = repository.save(mapper.toEntity(dto));

        int quantidade = dto.quantidadeInicial() != null ? dto.quantidadeInicial() : 0;

        Estoque estoqueSalvo = estoqueRepository.save(
                Estoque.builder()
                        .produto(produtoSalvo)
                        .unidade(unidade)
                        .quantidade(quantidade)
                        .build()
        );

        // Montagem do response: produto + estoque criado automaticamente
        EstoqueResponseDTO estoqueDto = estoqueMapper.toDto(estoqueSalvo);
        ProdutoResponseDTO produtoDto = mapper.toDto(produtoSalvo);

        return new ProdutoResponseDTO(
                produtoDto.id(),
                produtoDto.nome(),
                produtoDto.descricao(),
                produtoDto.precoUnitario(),
                produtoDto.categoriaComida(),
                produtoDto.periodoDia(),
                estoqueDto
        );
    }
}
