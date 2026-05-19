package br.com.raizes_do_nordeste.domain.entities;


import br.com.raizes_do_nordeste.domain.enums.MetodoPagamento;
import br.com.raizes_do_nordeste.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_pagamento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pagamento {

    @Id
    @Column(name = "id_pagamento")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "valor_pago", nullable = false)
    private BigDecimal valorPago;

    @CreationTimestamp
    @Column(name = "data_pagamento", updatable = false, nullable = false)
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Pagamento pagamento)) return false;
        return getId().equals(pagamento.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
