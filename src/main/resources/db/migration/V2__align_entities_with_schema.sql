-- =============================================================
-- V2__align_entities_with_schema.sql
-- Alinha o schema V1 com o mapeamento atual das entidades JPA
-- =============================================================

-- -------------------------------------------------------------
-- CLIENTE: adiciona colunas exigidas pela entidade e padroniza FK
-- -------------------------------------------------------------
ALTER TABLE tb_cliente
    ADD COLUMN IF NOT EXISTS email VARCHAR(150),
    ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS data_nascimento DATE;

-- Garante unicidade de e-mail no cliente sem falhar em reexecucao
CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_cliente_email ON tb_cliente (email);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_cliente'
          AND column_name = 'atendente_id'
    ) THEN
        ALTER TABLE tb_cliente RENAME COLUMN atendente_id TO equipe_id;
    END IF;
END $$;

ALTER TABLE tb_cliente
    DROP CONSTRAINT IF EXISTS fk_cliente_atendente,
    DROP CONSTRAINT IF EXISTS fk_cliente_equipe;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_cliente'
          AND column_name = 'equipe_id'
    ) THEN
        ALTER TABLE tb_cliente
            ADD CONSTRAINT fk_cliente_equipe
            FOREIGN KEY (equipe_id) REFERENCES tb_equipe (id_equipe);
    END IF;
END $$;

-- -------------------------------------------------------------
-- EQUIPE: adiciona telefone principal (campo exigido na entidade)
-- -------------------------------------------------------------
ALTER TABLE tb_equipe
    ADD COLUMN IF NOT EXISTS fone_principal VARCHAR(20);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_equipe_fone_principal ON tb_equipe (fone_principal);

-- -------------------------------------------------------------
-- TOTEM: cria tabela e constraints para suportar relacionamento
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tb_totem (
    id_totem                 UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    codigo_indentificador    VARCHAR(100) NOT NULL,
    token_autenticacao       VARCHAR(255),
    status_maquina           VARCHAR(20)  NOT NULL,
    unidade_id               UUID
);

ALTER TABLE tb_totem
    DROP CONSTRAINT IF EXISTS chk_totem_status_maquina,
    DROP CONSTRAINT IF EXISTS fk_totem_unidade;

ALTER TABLE tb_totem
    ADD CONSTRAINT chk_totem_status_maquina
        CHECK (status_maquina IN ('ATIVO', 'MANUTENCAO', 'DESATIVADO')),
    ADD CONSTRAINT fk_totem_unidade
        FOREIGN KEY (unidade_id) REFERENCES tb_unidade (id_unidade);

-- -------------------------------------------------------------
-- PEDIDO: padroniza FK de equipe e adiciona relacionamento com totem
-- -------------------------------------------------------------
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_pedido'
          AND column_name = 'atendente_id'
    ) THEN
        ALTER TABLE tb_pedido RENAME COLUMN atendente_id TO equipe_id;
    END IF;
END $$;

ALTER TABLE tb_pedido
    DROP CONSTRAINT IF EXISTS fk_pedido_atendente,
    DROP CONSTRAINT IF EXISTS fk_pedido_equipe,
    DROP CONSTRAINT IF EXISTS fk_pedido_totem,
    ADD COLUMN IF NOT EXISTS totem_id UUID,
    ADD CONSTRAINT fk_pedido_equipe
        FOREIGN KEY (equipe_id) REFERENCES tb_equipe (id_equipe),
    ADD CONSTRAINT fk_pedido_totem
        FOREIGN KEY (totem_id) REFERENCES tb_totem (id_totem);

