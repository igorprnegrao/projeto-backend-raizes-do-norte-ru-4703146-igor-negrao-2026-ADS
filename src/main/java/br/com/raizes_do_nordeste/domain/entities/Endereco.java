package br.com.raizes_do_nordeste.domain.entities;


import br.com.raizes_do_nordeste.domain.enums.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_endereco")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Endereco {

    @Id
    @Column(name = "id_endereco")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "bairro", nullable = false)
    private String bairro;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Endereco endereco)) return false;

        return getId().equals(endereco.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
