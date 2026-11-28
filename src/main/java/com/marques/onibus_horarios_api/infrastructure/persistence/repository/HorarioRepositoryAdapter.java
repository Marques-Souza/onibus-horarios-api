package com.marques.onibus_horarios_api.infrastructure.persistence.repository;

import com.marques.onibus_horarios_api.domain.model.Horario;
import com.marques.onibus_horarios_api.domain.model.HorarioDaLinha;
import com.marques.onibus_horarios_api.domain.model.Linha;
import com.marques.onibus_horarios_api.domain.model.Sentido;
import com.marques.onibus_horarios_api.domain.model.TipoDia;
import com.marques.onibus_horarios_api.domain.repository.HorarioRepository;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.HorarioEntity;
import com.marques.onibus_horarios_api.infrastructure.persistence.entity.LinhaEntity;
import com.marques.onibus_horarios_api.infrastructure.persistence.mapper.HorarioPersistenceMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Repository
public class HorarioRepositoryAdapter implements HorarioRepository {


    private final HorarioJpaRepository horarioJpaRepository;
    private final HorarioPersistenceMapper horarioPersistenceMapper;

    public HorarioRepositoryAdapter(
            HorarioJpaRepository horarioJpaRepository,
            HorarioPersistenceMapper horarioPersistenceMapper
    ) {
        this.horarioJpaRepository = horarioJpaRepository;
        this.horarioPersistenceMapper = horarioPersistenceMapper;
    }

    @Override
    public Horario save(Horario horario) {
        var entity = horarioPersistenceMapper.toEntity(horario);
        var salvo = horarioJpaRepository.save(entity);
        return horarioPersistenceMapper.toDomain(salvo);
    }

    @Override
    public boolean existsByLinhaIdAndSentidoAndTipoDiaAndHoraPartida(
            UUID linhaId,
            Sentido sentido,
            TipoDia tipoDia,
            LocalTime horaPartida
    ) {
        return horarioJpaRepository.existsByLinha_IdAndSentidoAndTipoDiaAndHoraPartida(
                linhaId,
                sentido,
                tipoDia,
                horaPartida
        );
    }

    @Override
    public List<Horario> buscarAtivosPorLinha(UUID linhaId, Sentido sentido, TipoDia tipoDia) {
        List<HorarioEntity> horarios;

        if (sentido != null && tipoDia != null) {
            horarios = horarioJpaRepository
                    .findAllByLinha_IdAndSentidoAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(
                            linhaId, sentido, tipoDia
                    );
        } else if (sentido != null) {
            horarios = horarioJpaRepository
                    .findAllByLinha_IdAndSentidoAndActiveTrueOrderByHoraPartidaAsc(linhaId, sentido);
        } else if (tipoDia != null) {
            horarios = horarioJpaRepository
                    .findAllByLinha_IdAndTipoDiaAndActiveTrueOrderByHoraPartidaAsc(linhaId, tipoDia);
        } else {
            horarios = horarioJpaRepository.findAllByLinha_IdAndActiveTrueOrderByHoraPartidaAsc(linhaId);
        }

        return horarios.stream()
                .map(horarioPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HorarioDaLinha> buscarAtivosPorTrajeto(
            String origem,
            String destino,
            String codigoLinha,
            String bairroOrigem,
            Sentido sentido,
            TipoDia tipoDia
    ) {
        Specification<HorarioEntity> specification = (root, query, builder) -> {
            Fetch<HorarioEntity, LinhaEntity> linhaFetch =
                    root.<HorarioEntity, LinhaEntity>fetch("linha", JoinType.INNER);
            Join<HorarioEntity, LinhaEntity> linha = (Join<HorarioEntity, LinhaEntity>) linhaFetch;
            query.distinct(true);

            var predicates = new ArrayList<Predicate>();
            predicates.add(builder.isTrue(root.get("active")));
            predicates.add(builder.isTrue(linha.get("active")));

            if (sentido != null) {
                predicates.add(builder.equal(root.get("sentido"), sentido));
            }
            if (tipoDia != null) {
                predicates.add(builder.equal(root.get("tipoDia"), tipoDia));
            }
            if (codigoLinha != null && !codigoLinha.isBlank()) {
                predicates.add(builder.equal(
                        builder.lower(linha.get("codigo")),
                        codigoLinha.trim().toLowerCase(Locale.ROOT)
                ));
            }
            if (bairroOrigem != null && !bairroOrigem.isBlank()) {
                predicates.add(builder.like(
                        semAcentos(builder, linha.get("bairroOrigem")),
                        contemNormalizado(bairroOrigem)
                ));
            }
            if (origem != null && !origem.isBlank()) {
                var origemNormalizada = normalizarTexto(origem);
                boolean origemAguasLindas = origemNormalizada.contains("aguas lindas");
                var origemNoDistritoFederal = builder.or(
                        builder.like(semAcentos(builder, linha.get("terminalDestino")), contemNormalizado(origem)),
                        builder.like(semAcentos(builder, linha.get("nome")), contemNormalizado(origem))
                );
                var origemEmAguasLindas = origemAguasLindas
                        ? builder.conjunction()
                        : builder.like(
                                semAcentos(builder, linha.get("bairroOrigem")),
                                contemNormalizado(origem)
                        );

                predicates.add(builder.or(
                        builder.and(
                                builder.equal(root.get("sentido"), Sentido.IDA),
                                origemEmAguasLindas
                        ),
                        builder.and(
                                builder.equal(root.get("sentido"), Sentido.VOLTA),
                                origemNoDistritoFederal
                        )
                ));
            }
            if (destino != null && !destino.isBlank()) {
                var destinoNormalizado = normalizarTexto(destino);
                boolean destinoAguasLindas = destinoNormalizado.contains("aguas lindas");
                var destinoNoDistritoFederal = builder.or(
                        builder.like(semAcentos(builder, linha.get("terminalDestino")), contemNormalizado(destino)),
                        builder.like(semAcentos(builder, linha.get("nome")), contemNormalizado(destino))
                );
                var destinoEmAguasLindas = destinoAguasLindas
                        ? builder.conjunction()
                        : builder.like(
                                semAcentos(builder, linha.get("bairroOrigem")),
                                contemNormalizado(destino)
                        );

                predicates.add(builder.or(
                        builder.and(
                                builder.equal(root.get("sentido"), Sentido.IDA),
                                destinoNoDistritoFederal
                        ),
                        builder.and(
                                builder.equal(root.get("sentido"), Sentido.VOLTA),
                                destinoEmAguasLindas
                        )
                ));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };

        return horarioJpaRepository.findAll(
                        specification,
                        Sort.by(Sort.Order.asc("horaPartida"), Sort.Order.asc("linha.codigo"))
                )
                .stream()
                .map(horario -> new HorarioDaLinha(
                        horarioPersistenceMapper.toDomain(horario),
                        toDomain(horario.getLinha())
                ))
                .toList();
    }

    private String contemNormalizado(String valor) {
        return "%" + normalizarTexto(valor) + "%";
    }

    private Expression<String> semAcentos(CriteriaBuilder builder, Expression<String> campo) {
        return builder.function("unaccent", String.class, builder.lower(campo));
    }

    private String normalizarTexto(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private Linha toDomain(LinhaEntity linha) {
        return new Linha(
                linha.getId(),
                linha.getCodigo(),
                linha.getNome(),
                linha.getBairroOrigem(),
                linha.getTerminalDestino(),
                linha.getOperadora(),
                linha.getTarifa(),
                linha.isActive()
        );
    }
}
