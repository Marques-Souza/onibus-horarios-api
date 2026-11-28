CREATE TABLE importacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_arquivo VARCHAR(255) NOT NULL,
    importado_em TIMESTAMPTZ NOT NULL,
    linhas_criadas INTEGER NOT NULL,
    horarios_criados INTEGER NOT NULL,
    horarios_ignorados INTEGER NOT NULL
);
