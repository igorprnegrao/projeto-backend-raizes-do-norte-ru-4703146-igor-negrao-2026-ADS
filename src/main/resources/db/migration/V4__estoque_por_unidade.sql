-- =============================================================
-- V4__estoque_por_unidade.sql
-- Regra: estoque por unidade (um registro por produto + unidade)
-- =============================================================

ALTER TABLE tb_estoque
    ADD COLUMN IF NOT EXISTS unidade_id UUID;

ALTER TABLE tb_estoque
    DROP CONSTRAINT IF EXISTS fk_estoque_unidade;

ALTER TABLE tb_estoque
    ADD CONSTRAINT fk_estoque_unidade
        FOREIGN KEY (unidade_id) REFERENCES tb_unidade (id_unidade);

DROP INDEX IF EXISTS ux_tb_estoque_produto_id;

CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_estoque_produto_unidade
    ON tb_estoque (produto_id, unidade_id);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM tb_estoque WHERE unidade_id IS NULL) THEN
        ALTER TABLE tb_estoque ALTER COLUMN unidade_id SET NOT NULL;
    END IF;
END $$;

