package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.UnidadeRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.UnidadeResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.update.EnderecoUpdateDTO;
import br.com.raizes_do_nordeste.api.DTOs.update.UnidadeUpdateDTO;
import br.com.raizes_do_nordeste.api.mappers.UnidadeMapper;
import br.com.raizes_do_nordeste.domain.entities.Endereco;
import br.com.raizes_do_nordeste.domain.entities.Unidade;
import br.com.raizes_do_nordeste.infra.exceptions.ContatoAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.EmailAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.UnidadeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository repository;
    private final UnidadeMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UnidadeResponseDTO cadastrar(UnidadeRequestDTO unidadeRequestDTO) {

        DadosSanitizados dados = sanitizarValidar(unidadeRequestDTO);

        Unidade unidade = mapper.toEntity(unidadeRequestDTO);

        unidade.setFonePrincipalFixo(dados.contatoLimpoFixo());
        unidade.setFoneSecundarioCelular(dados.contatoLimpoCelular());
        unidade.setEmail(dados.emailLimpo());
        unidade.setPasswordHash(passwordEncoder.encode(unidadeRequestDTO.senha()));

        Unidade unidadeSalvo = repository.save(unidade);

        return mapper.toDto(unidadeSalvo);

    }

    @Transactional
    public UnidadeResponseDTO atualizar(UUID idUnidade, UnidadeRequestDTO unidadeRequestDTO) {
        Unidade unidade = repository.findById(idUnidade)
                .orElseThrow(() -> new UnidadeNotFoundException(idUnidade.toString()));

        DadosSanitizados dados = sanitizarValidarAtualizacao(idUnidade, unidadeRequestDTO);

        unidade.setNomeLoja(unidadeRequestDTO.nomeLoja().trim());
        unidade.setEmail(dados.emailLimpo());
        unidade.setPasswordHash(passwordEncoder.encode(unidadeRequestDTO.senha()));
        unidade.setFonePrincipalFixo(dados.contatoLimpoFixo());
        unidade.setFoneSecundarioCelular(dados.contatoLimpoCelular());
        unidade.setMetaDesempenhoVenda(unidadeRequestDTO.metaDesempenhoVenda());
        unidade.setTipoCozinha(unidadeRequestDTO.tipoCozinha());

        if (unidade.getEndereco() == null) {
            unidade.setEndereco(new Endereco());
        }
        unidade.getEndereco().setLogradouro(unidadeRequestDTO.endereco().logradouro());
        unidade.getEndereco().setNumero(unidadeRequestDTO.endereco().numero());
        unidade.getEndereco().setBairro(unidadeRequestDTO.endereco().bairro());
        unidade.getEndereco().setCidade(unidadeRequestDTO.endereco().cidade());
        unidade.getEndereco().setEstado(unidadeRequestDTO.endereco().estado());

        Unidade unidadeAtualizada = repository.save(unidade);
        return mapper.toDto(unidadeAtualizada);
    }

    @Transactional
    public UnidadeResponseDTO atualizarParcial(UUID idUnidade, UnidadeUpdateDTO dto) {
        Unidade unidade = repository.findById(idUnidade)
                .orElseThrow(() -> new UnidadeNotFoundException(idUnidade.toString()));

        if (dto.nomeLoja() != null) {
            unidade.setNomeLoja(dto.nomeLoja().trim());
        }

        if (dto.email() != null) {
            String emailLimpo = dto.email().trim().toLowerCase();
            if (repository.existsByEmailAndIdNot(emailLimpo, idUnidade)) {
                throw new EmailAlreadyExistsException(dto.email());
            }
            unidade.setEmail(emailLimpo);
        }

        if (dto.senha() != null && !dto.senha().isBlank()) {
            unidade.setPasswordHash(passwordEncoder.encode(dto.senha()));
        }

        if (dto.fonePrincipalFixo() != null) {
            unidade.setFonePrincipalFixo(dto.fonePrincipalFixo().replaceAll("\\D", ""));
        }

        if (dto.foneSecundarioCelular() != null) {
            String contatoLimpoCelular = dto.foneSecundarioCelular().replaceAll("\\D", "");
            if (repository.existsByFoneSecundarioCelularAndIdNot(contatoLimpoCelular, idUnidade)) {
                throw new ContatoAlreadyExistsException(dto.foneSecundarioCelular());
            }
            unidade.setFoneSecundarioCelular(contatoLimpoCelular);
        }

        if (dto.metaDesempenhoVenda() != null) {
            unidade.setMetaDesempenhoVenda(dto.metaDesempenhoVenda());
        }

        if (dto.tipoCozinha() != null) {
            unidade.setTipoCozinha(dto.tipoCozinha());
        }

        if (dto.endereco() != null) {
            aplicarEnderecoParcial(unidade, dto.endereco());
        }

        Unidade unidadeAtualizada = repository.save(unidade);
        return mapper.toDto(unidadeAtualizada);
    }

    private void aplicarEnderecoParcial(Unidade unidade, EnderecoUpdateDTO dto) {
        if (unidade.getEndereco() == null) {
            unidade.setEndereco(new Endereco());
        }

        if (dto.logradouro() != null) {
            unidade.getEndereco().setLogradouro(dto.logradouro().trim());
        }
        if (dto.numero() != null) {
            unidade.getEndereco().setNumero(dto.numero().trim());
        }
        if (dto.bairro() != null) {
            unidade.getEndereco().setBairro(dto.bairro().trim());
        }
        if (dto.cidade() != null) {
            unidade.getEndereco().setCidade(dto.cidade().trim());
        }
        if (dto.estado() != null) {
            unidade.getEndereco().setEstado(dto.estado());
        }
    }


    private DadosSanitizados sanitizarValidar(UnidadeRequestDTO dto){

        String contatoLimpoFixo = (dto.fonePrincipalFixo() != null)
                ? dto.fonePrincipalFixo().replaceAll("\\D", "") : "";


        String contatoLimpoCelular = (dto.foneSecundarioCelular() != null)
                ? dto.foneSecundarioCelular().replaceAll("\\D", "") : "";


        String emailLimpo = dto.email().trim().toLowerCase();


        if (repository.existsByFoneSecundarioCelular(contatoLimpoCelular)) {
            throw new ContatoAlreadyExistsException(dto.foneSecundarioCelular());
        }

        if (repository.existsByEmail(emailLimpo)) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        return new DadosSanitizados(contatoLimpoFixo, contatoLimpoCelular, emailLimpo);

    }

    private DadosSanitizados sanitizarValidarAtualizacao(UUID idUnidade, UnidadeRequestDTO dto) {
        String contatoLimpoFixo = (dto.fonePrincipalFixo() != null)
                ? dto.fonePrincipalFixo().replaceAll("\\D", "") : "";

        String contatoLimpoCelular = (dto.foneSecundarioCelular() != null)
                ? dto.foneSecundarioCelular().replaceAll("\\D", "") : "";

        String emailLimpo = dto.email().trim().toLowerCase();

        if (repository.existsByFoneSecundarioCelularAndIdNot(contatoLimpoCelular, idUnidade)) {
            throw new ContatoAlreadyExistsException(dto.foneSecundarioCelular());
        }

        if (repository.existsByEmailAndIdNot(emailLimpo, idUnidade)) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        return new DadosSanitizados(contatoLimpoFixo, contatoLimpoCelular, emailLimpo);
    }

    /**
     * Record para armazenar dados sanitizados
     */
    private record DadosSanitizados(
            String contatoLimpoFixo,
            String contatoLimpoCelular,
            String emailLimpo) {
    }
}
