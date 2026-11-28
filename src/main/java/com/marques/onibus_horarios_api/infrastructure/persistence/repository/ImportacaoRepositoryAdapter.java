package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Importacao;
import com.marques.onibus_horarios_api.domain.repository.ImportacaoRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.ImportacaoEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ImportacaoRepositoryAdapter implements ImportacaoRepository {

    private final ImportacaoJpaRepository repository;

    public ImportacaoRepositoryAdapter(ImportacaoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Importacao save(Importacao importacao) {
        ImportacaoEntity entity = new ImportacaoEntity();
        entity.setNomeArquivo(importacao.nomeArquivo());
        entity.setImportadoEm(importacao.importadoEm());
        entity.setLinhasCriadas(importacao.linhasCriadas());
        entity.setHorariosCriados(importacao.horariosCriados());
        entity.setHorariosIgnorados(importacao.horariosIgnorados());
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Importacao> buscarUltima() {
        return repository.findFirstByOrderByImportadoEmDesc().map(this::toDomain);
    }

    private Importacao toDomain(ImportacaoEntity entity) {
        return new Importacao(
                entity.getId(),
                entity.getNomeArquivo(),
                entity.getImportadoEm(),
                entity.getLinhasCriadas(),
                entity.getHorariosCriados(),
                entity.getHorariosIgnorados()
        );
    }
}
