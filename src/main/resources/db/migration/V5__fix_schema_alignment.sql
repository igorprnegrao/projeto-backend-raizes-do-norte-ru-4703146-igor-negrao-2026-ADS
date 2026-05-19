-- =============================================================
-- V5__fix_schema_alignment.sql
-- Alinha definitivamente o schema com as entidades JPA:
--   1. Remove UNIQUE indevido de password_hash em tb_equipe
--   2. Adiciona colunas ausentes em tb_unidade
--   3. Corrige nullabilidade em tb_equipe.fone_principal
--   4. Corrige nullabilidade em tb_cliente (email, password_hash, data_nascimento)
-- =============================================================


-- -------------------------------------------------------------
-- 1. tb_equipe: remover UNIQUE indevido de password_hash
--    Senhas identicas geram o mesmo hash, o que violaria esta constraint
-- -------------------------------------------------------------
DO $$
DECLARE
    v_constraint_name TEXT;
BEGIN
    SELECT tc.constraint_name INTO v_constraint_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
       AND tc.table_schema    = kcu.table_schema
    WHERE tc.table_name    = 'tb_equipe'
      AND tc.constraint_type = 'UNIQUE'
      AND kcu.column_name   = 'password_hash'
    LIMIT 1;

    IF v_constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE tb_equipe DROP CONSTRAINT %I', v_constraint_name);
        RAISE NOTICE 'Constraint UNIQUE "%" removida de tb_equipe.password_hash', v_constraint_name;
    ELSE
        RAISE NOTICE 'Nenhuma constraint UNIQUE encontrada em tb_equipe.password_hash (ja foi removida ou nunca existiu)';
    END IF;
END $$;


-- -------------------------------------------------------------
-- 2. tb_unidade: adicionar colunas exigidas pela entidade Unidade
--    (email, password_hash, fone_principal_fixo, fone_secundario_celular
--     estavam ausentes em todas as migrations anteriores)
-- -------------------------------------------------------------
ALTER TABLE tb_unidade
    ADD COLUMN IF NOT EXISTS email                   VARCHAR(150),
    ADD COLUMN IF NOT EXISTS password_hash           VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fone_principal_fixo     VARCHAR(50),
    ADD COLUMN IF NOT EXISTS fone_secundario_celular VARCHAR(50);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_unidade_email
    ON tb_unidade (email);

CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_unidade_fone_secundario_celular
    ON tb_unidade (fone_secundario_celular);

-- Aplica NOT NULL somente se nao houver linhas com nulos
-- (preserva compatibilidade em ambientes com dados existentes)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM tb_unidade
        WHERE email                   IS NULL
           OR password_hash           IS NULL
           OR fone_principal_fixo     IS NULL
           OR fone_secundario_celular IS NULL
    ) THEN
        ALTER TABLE tb_unidade
            ALTER COLUMN email                   SET NOT NULL,
            ALTER COLUMN password_hash           SET NOT NULL,
            ALTER COLUMN fone_principal_fixo     SET NOT NULL,
            ALTER COLUMN fone_secundario_celular SET NOT NULL;
        RAISE NOTICE 'Restricoes NOT NULL aplicadas em tb_unidade';
    ELSE
        RAISE NOTICE 'tb_unidade possui linhas com nulos; NOT NULL nao aplicado. Corrija os dados e execute manualmente.';
    END IF;
END $$;


-- -------------------------------------------------------------
-- 3. tb_equipe.fone_principal: corrigir nullabilidade
--    (adicionada em V2 sem NOT NULL, entidade declara nullable = false)
-- -------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM tb_equipe WHERE fone_principal IS NULL) THEN
        ALTER TABLE tb_equipe ALTER COLUMN fone_principal SET NOT NULL;
        RAISE NOTICE 'NOT NULL aplicado em tb_equipe.fone_principal';
    ELSE
        RAISE NOTICE 'tb_equipe possui linhas com fone_principal nulo; NOT NULL nao aplicado.';
    END IF;
END $$;


-- -------------------------------------------------------------
-- 4. tb_cliente: corrigir nullabilidade de email, password_hash
--    e data_nascimento (adicionadas em V2 sem NOT NULL)
-- -------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM tb_cliente
        WHERE email          IS NULL
           OR password_hash  IS NULL
           OR data_nascimento IS NULL
    ) THEN
        ALTER TABLE tb_cliente
            ALTER COLUMN email           SET NOT NULL,
            ALTER COLUMN password_hash   SET NOT NULL,
            ALTER COLUMN data_nascimento SET NOT NULL;
        RAISE NOTICE 'Restricoes NOT NULL aplicadas em tb_cliente';
    ELSE
        RAISE NOTICE 'tb_cliente possui linhas com nulos; NOT NULL nao aplicado. Corrija os dados e execute manualmente.';
    END IF;
END $$;

