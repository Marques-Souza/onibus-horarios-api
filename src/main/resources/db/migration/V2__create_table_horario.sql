CREATE TABLE horario (
                         id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
                         linha_id     UUID         NOT NULL,
                         sentido      VARCHAR(10)  NOT NULL,
                         tipo_dia     VARCHAR(20)  NOT NULL,
                         hora_partida TIME         NOT NULL,
                         observacao   VARCHAR(255),
                         active       BOOLEAN      NOT NULL DEFAULT TRUE,

                         CONSTRAINT fk_horario_linha
                             FOREIGN KEY (linha_id) REFERENCES linha (id),

                         CONSTRAINT uk_horario_saida
                             UNIQUE (linha_id, sentido, tipo_dia, hora_partida)
);