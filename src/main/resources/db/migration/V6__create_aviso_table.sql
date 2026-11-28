CREATE TABLE aviso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    linha_id UUID REFERENCES linha (id),
    tipo VARCHAR(30) NOT NULL,
    mensagem VARCHAR(500) NOT NULL,
    horario_referencia TIMESTAMP,
    criado_em TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_aviso_status_criado_em ON aviso (status, criado_em DESC);
