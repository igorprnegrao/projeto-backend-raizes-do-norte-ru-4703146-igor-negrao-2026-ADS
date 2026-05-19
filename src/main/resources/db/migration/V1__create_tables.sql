-- =============================================================
-- V1__create_tables.sql
-- Criação das tabelas do sistema Raízes do Nordeste
-- =============================================================


-- =============================================================
-- TABELAS
-- =============================================================

CREATE TABLE tb_endereco (
    id_endereco  UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    logradouro   VARCHAR(150) NOT NULL,
    numero       VARCHAR(20)  NOT NULL,
    bairro       VARCHAR(100) NOT NULL,
    cidade       VARCHAR(100) NOT NULL,
    estado       VARCHAR(2)   NOT NULL
);

CREATE TABLE tb_unidade (
    id_unidade              UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    nome                    VARCHAR(100)    NOT NULL,
    meta_desempenho_venda   INTEGER,
    tipo_cozinha            VARCHAR(20)     NOT NULL,
    endereco_id             UUID UNIQUE
);

CREATE TABLE tb_perfil_acesso (
    id_perfil_acesso    UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    data_registro       TIMESTAMP   NOT NULL DEFAULT NOW(),
    tipo_perfil         VARCHAR(20) NOT NULL
);

CREATE TABLE tb_produto (
    id_produto          UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    nome                VARCHAR(100)    NOT NULL,
    descricao           VARCHAR(255),
    preco_unitario      NUMERIC(10, 2)  NOT NULL,
    categoria_comida    VARCHAR(20)     NOT NULL,
    periodo_dia         VARCHAR(10)     NOT NULL
);

CREATE TABLE tb_equipe (
    id_equipe           UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    nome_completo       VARCHAR(150) NOT NULL,
    email               VARCHAR(150) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL UNIQUE,
    perfil_acesso_id    UUID        NOT NULL UNIQUE,
    unidade_id          UUID
);

CREATE TABLE tb_cliente (
    id_cliente          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    nome_completo       VARCHAR(150) NOT NULL,
    fone_principal      VARCHAR(20)  NOT NULL UNIQUE,
    fone_secundario     VARCHAR(20),
    saldo_pontos        INTEGER     DEFAULT 0,
    data_registro       TIMESTAMP   NOT NULL DEFAULT NOW(),
    consentimento_lgpd  BOOLEAN     NOT NULL DEFAULT FALSE,
    atendente_id        UUID
);

CREATE TABLE tb_pedido (
    id_pedido       BIGSERIAL           PRIMARY KEY,
    registro_data   TIMESTAMP           NOT NULL DEFAULT NOW(),
    canal_pedido    VARCHAR(10)         NOT NULL,
    status_pedido   VARCHAR(15)         NOT NULL,
    valor_total     NUMERIC(10, 2)      NOT NULL DEFAULT 0.00,
    cliente_id      UUID                NOT NULL,
    unidade_id      UUID                NOT NULL,
    atendente_id    UUID
);

CREATE TABLE tb_itens_pedido (
    id_itens_pedido UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    quantidade      INTEGER         NOT NULL,
    preco_momento   NUMERIC(10, 2)  NOT NULL,
    produto_id      UUID            NOT NULL,
    pedido_id       BIGINT          NOT NULL
);

CREATE TABLE tb_estoque (
    id_estoque  UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    quantidade  INTEGER NOT NULL DEFAULT 0,
    produto_id  UUID    NOT NULL
);

CREATE TABLE tb_pagamento (
    id_pagamento        UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    pedido_id           BIGINT          NOT NULL UNIQUE,
    valor_pago          NUMERIC(10, 2)  NOT NULL,
    data_pagamento      TIMESTAMP       NOT NULL DEFAULT NOW(),
    metodo_pagamento    VARCHAR(20)     NOT NULL,
    status_pagamento    VARCHAR(10)     NOT NULL
);


-- =============================================================
-- CHECK CONSTRAINTS
-- =============================================================

ALTER TABLE tb_unidade
    ADD CONSTRAINT chk_tipo_cozinha
        CHECK (tipo_cozinha IN ('COMPLETA', 'REDUZIDA'));

ALTER TABLE tb_endereco
    ADD CONSTRAINT chk_endereco_estado
        CHECK (estado IN ('AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'));

ALTER TABLE tb_perfil_acesso
    ADD CONSTRAINT chk_tipo_perfil
        CHECK (tipo_perfil IN ('ATENDENTE', 'COZINHEIRO', 'GERENTE'));

ALTER TABLE tb_produto
    ADD CONSTRAINT chk_categoria_comida
        CHECK (categoria_comida IN ('SOBREMESA', 'SALGADOS', 'PIZZAS', 'MASSAS', 'BEBIDAS', 'SANDUICHE', 'TAPIOCAS')),
    ADD CONSTRAINT chk_periodo_dia
        CHECK (periodo_dia IN ('ALMOCO', 'JANTAR'));

ALTER TABLE tb_pedido
    ADD CONSTRAINT chk_canal_pedido
        CHECK (canal_pedido IN ('APP', 'WEB', 'TOTEM', 'BALCAO')),
    ADD CONSTRAINT chk_status_pedido
        CHECK (status_pedido IN ('EM_PREPARO', 'PRONTO', 'ENTREGUE', 'CANCELADO'));

ALTER TABLE tb_pagamento
    ADD CONSTRAINT chk_metodo_pagamento
        CHECK (metodo_pagamento IN ('CARTAO_CREDITO', 'CARTAO_DEBITO', 'DINHEIRO', 'PIX', 'VALE_ALIMENTACAO')),
    ADD CONSTRAINT chk_status_pagamento
        CHECK (status_pagamento IN ('PENDENTE', 'APROVADO', 'RECUSADO', 'CANCELADO'));


-- =============================================================
-- FOREIGN KEY CONSTRAINTS
-- =============================================================

ALTER TABLE tb_equipe
    ADD CONSTRAINT fk_equipe_perfil_acesso
        FOREIGN KEY (perfil_acesso_id) REFERENCES tb_perfil_acesso (id_perfil_acesso),
    ADD CONSTRAINT fk_equipe_unidade
        FOREIGN KEY (unidade_id) REFERENCES tb_unidade (id_unidade);

ALTER TABLE tb_unidade
    ADD CONSTRAINT fk_unidade_endereco
        FOREIGN KEY (endereco_id) REFERENCES tb_endereco (id_endereco);

ALTER TABLE tb_cliente
    ADD CONSTRAINT fk_cliente_atendente
        FOREIGN KEY (atendente_id) REFERENCES tb_equipe (id_equipe);

ALTER TABLE tb_pedido
    ADD CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id) REFERENCES tb_cliente (id_cliente),
    ADD CONSTRAINT fk_pedido_unidade
        FOREIGN KEY (unidade_id) REFERENCES tb_unidade (id_unidade),
    ADD CONSTRAINT fk_pedido_atendente
        FOREIGN KEY (atendente_id) REFERENCES tb_equipe (id_equipe);

ALTER TABLE tb_itens_pedido
    ADD CONSTRAINT fk_itens_pedido_produto
        FOREIGN KEY (produto_id) REFERENCES tb_produto (id_produto),
    ADD CONSTRAINT fk_itens_pedido_pedido
        FOREIGN KEY (pedido_id) REFERENCES tb_pedido (id_pedido);

ALTER TABLE tb_estoque
    ADD CONSTRAINT fk_estoque_produto
        FOREIGN KEY (produto_id) REFERENCES tb_produto (id_produto);

ALTER TABLE tb_pagamento
    ADD CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (pedido_id) REFERENCES tb_pedido (id_pedido);
