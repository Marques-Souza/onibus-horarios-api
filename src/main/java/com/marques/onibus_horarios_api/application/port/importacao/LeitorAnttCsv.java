package com.marques.onibus_horarios_api.application.port.importacao;

import com.marques.onibus_horarios_api.application.dto.importacao.AnttHorarioCsv;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface LeitorAnttCsv {
    List<AnttHorarioCsv> ler(InputStream arquivo) throws IOException;
}
