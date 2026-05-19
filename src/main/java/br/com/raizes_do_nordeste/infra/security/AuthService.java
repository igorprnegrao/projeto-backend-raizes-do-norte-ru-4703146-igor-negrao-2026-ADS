package br.com.raizes_do_nordeste.infra.security;

import br.com.raizes_do_nordeste.domain.entities.Cliente;
import br.com.raizes_do_nordeste.domain.entities.Equipe;
import br.com.raizes_do_nordeste.domain.enums.TipoPerfil;
import br.com.raizes_do_nordeste.infra.repositories.ClienteRepository;
import br.com.raizes_do_nordeste.infra.repositories.EquipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {

    private final EquipeRepository equipeRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Tentando autenticar usuario: {}", username);

        var equipeOpt = equipeRepository.findByEmail(username);
        if (equipeOpt.isPresent()) {
            log.info("Usuario encontrado como membro da equipe");
            return toUserDetailsEquipe(equipeOpt.get());
        }

        var clienteOpt = clienteRepository.findByEmail(username);
        if (clienteOpt.isPresent()) {
            log.info("Usuario encontrado como cliente");
            return toUserDetailsCliente(clienteOpt.get());
        }

        log.error("Usuario nao encontrado: {}", username);
        throw new UsernameNotFoundException("Usuario nao encontrado com o e-mail: " + username);
    }

    private UserDetails toUserDetailsEquipe(Equipe equipe) {
        String role = "ROLE_EQUIPE";
        if (equipe.getPerfilAcesso() != null && equipe.getPerfilAcesso().getTipoPerfil() != null) {
            TipoPerfil tipo = equipe.getPerfilAcesso().getTipoPerfil();
            role = "ROLE_" + tipo.name();
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return User.withUsername(equipe.getEmail())
                .password(equipe.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    private UserDetails toUserDetailsCliente(Cliente cliente) {
        return User.withUsername(cliente.getEmail())
                .password(cliente.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")))
                .build();
    }
}

