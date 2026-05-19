package br.com.raizes_do_nordeste.domain.entities;


import br.com.raizes_do_nordeste.domain.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_perfil_acesso")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerfilAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_perfil_acesso")
    private UUID id;

    @CreationTimestamp
    @Column(name = "data_registro", updatable = false, nullable = false)
    private LocalDateTime dataRegistro;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPerfil tipoPerfil;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof PerfilAcesso that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
