package br.com.raizes_do_nordeste.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID id;

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "fone_principal", unique = true, nullable = false)
    private String fonePrincipal;

    @Column(name = "fone_secundario")
    private String foneSecundario;

    @Column(name = "saldo_pontos")
    private Integer saldoPontos;

    @CreationTimestamp
    @Column(name = "data_registro", updatable = false, nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "consentimento_lgpd", nullable = false)
    private Boolean consentimentoLGPD;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe atendente;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Cliente cliente)) return false;

        return getId().equals(cliente.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
