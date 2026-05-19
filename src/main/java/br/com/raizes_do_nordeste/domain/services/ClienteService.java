package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.ClienteRequestDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteExtratoPontosLancamentoResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteExtratoPontosResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClientePontosResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.response.ClienteResponseDTO;
import br.com.raizes_do_nordeste.api.DTOs.update.ClienteUpdateDTO;
import br.com.raizes_do_nordeste.api.mappers.ClienteMapper;
import br.com.raizes_do_nordeste.domain.entities.Cliente;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import br.com.raizes_do_nordeste.infra.exceptions.ClienteNotFoundException;
import br.com.raizes_do_nordeste.infra.exceptions.ContatoAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.EmailAlreadyExistsException;
import br.com.raizes_do_nordeste.infra.exceptions.EquipeNotFoundException;
import br.com.raizes_do_nordeste.infra.repositories.ClienteRepository;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import br.com.raizes_do_nordeste.infra.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final EquipeRepository equipeRepository;
    private final ClienteMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final PedidoRepository pedidoRepository;


    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO clienteRequestDTO) {

        DadosSanitizados dados = sanitizarValidar(clienteRequestDTO);

        Cliente cliente = mapper.toEntity(clienteRequestDTO);
        cliente.setDataRegistro(LocalDateTime.now());
        cliente.setSaldoPontos(0);
        cliente.setFonePrincipal(dados.contatoLimpo1());
        cliente.setFoneSecundario(dados.contatoLimpo2());
        cliente.setEmail(dados.emailLimpo());

        cliente.setPasswordHash(passwordEncoder.encode(clienteRequestDTO.senha()));

        if (clienteRequestDTO.idAtendente() != null) {
            var atendente = equipeRepository.findById(clienteRequestDTO.idAtendente())
                    .orElseThrow(() -> new EquipeNotFoundException(clienteRequestDTO.idAtendente().toString()));
            cliente.setAtendente(atendente);
        }

        Cliente clienteSalvo = repository.save(cliente);


        return mapper.toDto(clienteSalvo);
    }


    @Transactional
    public ClienteResponseDTO atualizar(UUID idCliente, ClienteUpdateDTO clienteUpdateDTO) {
        Cliente cliente = repository.findById(idCliente)
                .orElseThrow(() -> new ClienteNotFoundException("Usuario nao encontrado com o ID: " + idCliente));

        String emailAnterior = cliente.getEmail();
        String foneAnterior = cliente.getFonePrincipal();

        if (clienteUpdateDTO.nomeCompleto() != null) cliente.setNomeCompleto(clienteUpdateDTO.nomeCompleto());
        if (clienteUpdateDTO.email() != null) cliente.setEmail(clienteUpdateDTO.email());
        if (clienteUpdateDTO.fonePrincipal() != null) cliente.setFonePrincipal(clienteUpdateDTO.fonePrincipal());
        if (clienteUpdateDTO.senha() != null && !clienteUpdateDTO.senha().isBlank()) {
            cliente.setPasswordHash(passwordEncoder.encode(clienteUpdateDTO.senha()));
        }

        sanitizarValidarAtualizacao(cliente, emailAnterior, foneAnterior);

        Cliente clienteAtualizado = repository.save(cliente);
        return mapper.toDto(clienteAtualizado);
    }

    @Transactional(readOnly = true)
    public ClientePontosResponseDTO consultarSaldoPontosClienteAutenticado() {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        Cliente cliente = repository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente nao encontrado para o e-mail: " + emailAutenticado));

        int saldo = cliente.getSaldoPontos() == null ? 0 : cliente.getSaldoPontos();
        return new ClientePontosResponseDTO(cliente.getId(), saldo);
    }

    @Transactional(readOnly = true)
    public ClienteExtratoPontosResponseDTO consultarExtratoPontosClienteAutenticado(org.springframework.data.domain.Pageable pageable) {
        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();

        Cliente cliente = repository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente nao encontrado para o e-mail: " + emailAutenticado));

        int saldo = cliente.getSaldoPontos() == null ? 0 : cliente.getSaldoPontos();
        var paginaOrdenada = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "registroData"));

        var page = pedidoRepository.findByClienteIdAndStatusPedidoNot(cliente.getId(), StatusPedido.CANCELADO, paginaOrdenada);

        var lancamentos = page.stream()
                .map(pedido -> new ClienteExtratoPontosLancamentoResponseDTO(
                        pedido.getId(),
                        pedido.getRegistroData(),
                        pedido.getCanalPedido(),
                        pedido.getValorTotal(),
                        pedido.getValorTotal() == null ? 0 : pedido.getValorTotal().intValue()
                ))
                .toList();

        return new ClienteExtratoPontosResponseDTO(
                cliente.getId(),
                saldo,
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                lancamentos
        );
    }

    //Sanitiza e valida os dados de entrada do Cliente

    private DadosSanitizados sanitizarValidar(ClienteRequestDTO dto) {

        String contatoLimpo1 = (dto.fonePrincipal() != null)
                ? dto.fonePrincipal().replaceAll("\\D", "") : "";

        String contatoLimpo2 = (dto.foneSecundario() != null)
                ? dto.foneSecundario().replaceAll("\\D", "") : "";

        String emailLimpo = dto.email().trim().toLowerCase();

        if (repository.existsByFonePrincipal(contatoLimpo1)) {
            throw new ContatoAlreadyExistsException(dto.fonePrincipal());
        }

        if (repository.existsByEmail(emailLimpo)) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        return new DadosSanitizados(contatoLimpo1, contatoLimpo2, emailLimpo);
    }

    private void sanitizarValidarAtualizacao(Cliente cliente, String emailAnterior, String foneAnterior) {
        if (cliente.getNomeCompleto() != null) {
            cliente.setNomeCompleto(cliente.getNomeCompleto().trim());
        }

        if (cliente.getEmail() != null) {
            String emailLimpo = cliente.getEmail().trim().toLowerCase();
            if (!emailLimpo.equals(emailAnterior) && repository.existsByEmail(emailLimpo)) {
                throw new EmailAlreadyExistsException(cliente.getEmail());
            }
            cliente.setEmail(emailLimpo);
        }

        if (cliente.getFonePrincipal() != null) {
            String foneLimpo = cliente.getFonePrincipal().replaceAll("\\D", "");
            if (!foneLimpo.equals(foneAnterior) && repository.existsByFonePrincipal(foneLimpo)) {
                throw new ContatoAlreadyExistsException(cliente.getFonePrincipal());
            }
            cliente.setFonePrincipal(foneLimpo);
        }
    }

    /**
     * Record para armazenar dados sanitizados
     */
    private record DadosSanitizados(String contatoLimpo1, String contatoLimpo2, String emailLimpo) {
    }

}
