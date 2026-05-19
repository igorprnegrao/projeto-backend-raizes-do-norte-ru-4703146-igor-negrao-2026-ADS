package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.EstoqueRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.EstoqueResponseDTO;
import br.com.raizes_do_nordeste.api.mappers.EstoqueMapper;
import br.com.raizes_do_nordeste.domain.entities.Estoque;
import br.com.raizes_do_nordeste.domain.entities.Produto;
import br.com.raizes_do_nordeste.infra.exceptions.EstoqueAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.EstoqueNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.ProdutoNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.EstoqueRepository;
import br.com.raizes_do_nordeste.infra.repositories.ProdutoRepository;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository repository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    private final EstoqueMapper mapper;


    @Transactional
    public EstoqueResponseDTO cadastrar(EstoqueRequestDTO estoqueRequestDTO) {

        if (repository.existsByProdutoIdAndUnidadeId(estoqueRequestDTO.idProduto(), estoqueRequestDTO.idUnidade())) {
            throw new EstoqueAlreadyExistsException(estoqueRequestDTO.idProduto(), estoqueRequestDTO.idUnidade());
        }

        Estoque estoque = mapper.toEntity(estoqueRequestDTO);

        Produto produto = produtoRepository.findById(estoqueRequestDTO.idProduto())
                .orElseThrow(() -> new ProdutoNotFoundException(estoqueRequestDTO.idProduto().toString()));
        estoque.setProduto(produto);

        var unidade = unidadeRepository.findById(estoqueRequestDTO.idUnidade())
                .orElseThrow(() -> new UnidadeNotFoundException(estoqueRequestDTO.idUnidade().toString()));
        estoque.setUnidade(unidade);

        Estoque estoqueSalvo = repository.save(estoque);

        return mapper.toDto(estoqueSalvo);

    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO buscarQuantidade(UUID idProduto, UUID idUnidade) {
        Estoque estoque = repository.findByProdutoIdAndUnidadeId(idProduto, idUnidade)
                .orElseThrow(() -> new EstoqueNotFoundException(idProduto, idUnidade));
        return mapper.toDto(estoque);
    }
}
