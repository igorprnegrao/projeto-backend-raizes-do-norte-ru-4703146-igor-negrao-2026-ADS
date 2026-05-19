package br.com.raizes_do_nordeste.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CanalPedido {


    APP("Aplicativo de celular"),
    WEB("Navegador de web"),
    TOTEM("Navegador de totem"),
    BALCAO("Atendente no balcão");

    private final String descricao;

}
