package br.com.raizes_do_nordeste.domain.entities;

import br.com.raizes_do_nordeste.domain.enums.StatusMaquina;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tb_totem")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Totem {

    @Id
    @Column(name = "id_totem")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo_indentificador", nullable = false)
    private String codigoIdentificador;

    @Column(name = "token_autenticacao", nullable = false)
    private String tokenAutenticacao;


    @Enumerated(EnumType.STRING)
    @Column(name = "status_maquina", nullable = false)
    private StatusMaquina statusMaquina;

    @ManyToOne
    @JoinColumn(name = "unidade_id")
    private Unidade unidade;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Totem totem)) return false;

        return getId().equals(totem.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
