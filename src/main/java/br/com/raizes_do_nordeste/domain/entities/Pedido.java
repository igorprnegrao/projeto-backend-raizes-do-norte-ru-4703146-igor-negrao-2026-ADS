package br.com.raizes_do_nordeste.domain.entities;


import br.com.raizes_do_nordeste.domain.enums.CanalPedido;
import br.com.raizes_do_nordeste.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "tb_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;


    @CreationTimestamp
    @Column(name = "registro_data", updatable = false, nullable = false)
    private LocalDateTime registroData;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido statusPedido;

    @Column(name = "valor_total")
    private BigDecimal valorTotal = BigDecimal.valueOf(0.0);

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensPedido> itensPedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe atendente;

    @ManyToOne
    @JoinColumn(name = "totem_id")
    private Totem totem;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Pedido pedido)) return false;
        return getId().equals(pedido.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
