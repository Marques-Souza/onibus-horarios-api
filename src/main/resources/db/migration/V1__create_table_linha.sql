CREATE TABLE linha (
                       id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       codigo           VARCHAR(20)   NOT NULL,
                       nome             VARCHAR(150)  NOT NULL,
                       bairro_origem    VARCHAR(100)  NOT NULL,
                       terminal_destino VARCHAR(100)  NOT NULL,
                       operadora        VARCHAR(100)  NOT NULL,
                       tarifa           NUMERIC(8, 2) NOT NULL,
                       active           BOOLEAN       NOT NULL DEFAULT TRUE,
                       CONSTRAINT uk_linha_codigo UNIQUE (codigo)
)