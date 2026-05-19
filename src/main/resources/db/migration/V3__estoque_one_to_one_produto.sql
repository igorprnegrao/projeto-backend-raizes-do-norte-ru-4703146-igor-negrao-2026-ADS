-- =============================================================
-- V3__estoque_one_to_one_produto.sql
-- Regra: um estoque por produto (global)
-- =============================================================

-- Garante unicidade do produto em tb_estoque
CREATE UNIQUE INDEX IF NOT EXISTS ux_tb_estoque_produto_id
    ON tb_estoque (produto_id);

