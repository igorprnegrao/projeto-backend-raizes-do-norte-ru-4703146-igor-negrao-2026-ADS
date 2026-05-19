package br.com.raizes_do_nordeste.domain.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_equipe")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_equipe")
    private UUID id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "fone_principal", unique = true, nullable = false)
    private String fonePrincipal;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash",  nullable = false)
    private String passwordHash;


    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "perfil_acesso_id")
    private PerfilAcesso perfilAcesso;

    @ManyToOne
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Equipe equipe)) return false;

        return getId().equals(equipe.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
