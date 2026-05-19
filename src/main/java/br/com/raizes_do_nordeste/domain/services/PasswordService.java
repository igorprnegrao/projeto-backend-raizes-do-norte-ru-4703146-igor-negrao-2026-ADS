package br.com.raizes_do_nordeste.domain.services;

import br.com.raizes_do_nordeste.api.DTOs.request.PasswordChangeRequestDTO;
import br.com.raizes_do_nordeste.infra.exceptions.InvalidPasswordException;
import br.com.raizes_do_nordeste.infra.repositories.ClienteRepository;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final EquipeRepository equipeRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public void changePassword(PasswordChangeRequestDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails userDetails)) {
            throw new InvalidPasswordException("Principal no contexto de seguranca nao e uma instancia de UserDetails.");
        }

        if (!passwordEncoder.matches(dto.senhaAtual(), userDetails.getPassword())) {
            throw new InvalidPasswordException("A senha atual esta incorreta.");
        }

        String newEncodedPassword = passwordEncoder.encode(dto.novaSenha());
        String email = userDetails.getUsername();

        var equipe = equipeRepository.findByEmail(email);
        if (equipe.isPresent()) {
            equipe.get().setPasswordHash(newEncodedPassword);
            equipeRepository.save(equipe.get());
            return;
        }

        var cliente = clienteRepository.findByEmail(email);
        if (cliente.isPresent()) {
            cliente.get().setPasswordHash(newEncodedPassword);
            clienteRepository.save(cliente.get());
            return;
        }

        throw new UsernameNotFoundException("Usuario autenticado nao encontrado para o e-mail: " + email);
    }
}

