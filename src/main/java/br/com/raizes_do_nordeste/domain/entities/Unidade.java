package br.com.raizes_do_nordeste.domain.entities;

import br.com.raizes_do_nordeste.domain.enums.TipoCozinha;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_unidade")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_unidade")
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nomeLoja;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "fone_principal_fixo",  nullable = false)
    private String fonePrincipalFixo;

    @Column(name = "fone_secundario_celular", unique = true,  nullable = false)
    private String foneSecundarioCelular;

    @Column(name = "meta_desempenho_venda")
    private Integer metaDesempenhoVenda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCozinha tipoCozinha;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "endereco_id", unique = true)
    private Endereco endereco;

    @Builder.Default
    @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
    private List<Estoque> estoques = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Unidade unidade)) return false;

        return getId().equals(unidade.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
