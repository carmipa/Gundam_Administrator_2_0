package br.com.gundam.service;

import br.com.gundam.model.AlturaPadrao;
import br.com.gundam.model.Escala;
import br.com.gundam.model.Grade;
import br.com.gundam.model.GundamKit;
import br.com.gundam.model.Universo;
import br.com.gundam.repository.AlturaPadraoRepository;
import br.com.gundam.repository.EscalaRepository;
import br.com.gundam.repository.GradeRepository;
import br.com.gundam.repository.GundamKitRepository;
import br.com.gundam.repository.UniversoRepository;
import br.com.gundam.spec.GundamKitSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class GundamKitService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GundamKitService.class);

    private final GundamKitRepository kits;
    private final GradeRepository grades;
    private final EscalaRepository escalas;
    private final AlturaPadraoRepository alturas;
    private final UniversoRepository universos;

    public GundamKitService(GundamKitRepository kits,
            GradeRepository grades,
            EscalaRepository escalas,
            AlturaPadraoRepository alturas,
            UniversoRepository universos) {
        this.kits = kits;
        this.grades = grades;
        this.escalas = escalas;
        this.alturas = alturas;
        this.universos = universos;
    }

    @Cacheable("grades_all")
    public List<Grade> findAllGrades() {
        return grades.findAll();
    }

    @Cacheable("escalas_all")
    public List<Escala> findAllEscalas() {
        return escalas.findAll();
    }

    @Cacheable("alturas_all")
    public List<AlturaPadrao> findAllAlturas() {
        return alturas.findAll();
    }

    @Cacheable("universos_all")
    public List<Universo> findAllUniversos() {
        return universos.findAll();
    }

    @CacheEvict(value = { "kit_by_id" }, allEntries = true)
    public GundamKit save(GundamKit g) {
        logger.info("Saving kit: {}", g.getModelo());
        return kits.save(g);
    }

    @CacheEvict(value = { "kit_by_id" }, allEntries = true)
    public void deleteById(Long id) {
        logger.warn("Deleting kit ID: {}", id);
        kits.deleteById(id);
    }

    @Cacheable(value = "kit_by_id", key = "#id")
    public GundamKit getById(Long id) {
        return kits.findById(id)
                .orElseThrow(() -> new br.com.gundam.exception.GundamResourceNotFoundException(
                        "Gundam Kit não encontrado com ID: " + id));
    }

    public Page<GundamKit> search(String modelo, Long gradeId, LocalDate de, LocalDate ate, Pageable pageable) {
        Specification<GundamKit> spec = Specification.allOf(
                GundamKitSpecifications.modeloLike(modelo),
                GundamKitSpecifications.gradeIdEquals(gradeId),
                GundamKitSpecifications.dataCompraBetween(de, ate));
        return kits.findAll(spec, pageable);
    }

    public Page<GundamKit> search(String modelo, Long gradeId, Long universoId, LocalDate de, LocalDate ate,
            Pageable pageable) {
        Specification<GundamKit> spec = Specification.allOf(
                GundamKitSpecifications.modeloLike(modelo),
                GundamKitSpecifications.gradeIdEquals(gradeId),
                GundamKitSpecifications.universoIdEquals(universoId),
                GundamKitSpecifications.dataCompraBetween(de, ate));
        return kits.findAll(spec, pageable);
    }

    public Map<String, Object> getFinancialReport() {
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("custoTotal", kits.findCustoTotal());
        reportData.put("custoMedio", kits.findCustoMedio());
        reportData.put("kitsMaisCaros", kits.findKitsMaisCaros());
        reportData.put("kitsMaisBaratos", kits.findKitsMaisBaratos());
        reportData.put("totalDeKits", kits.count());

        // Dados para Gráficos
        reportData.put("kitsPorGrade", kits.countKitsByGrade());
        reportData.put("kitsPorGrade", kits.countKitsByGrade());
        reportData.put("kitsPorUniverso", kits.countKitsByUniverso());
        reportData.put("custoPorGrade", kits.sumCostByGrade());

        // Custo no Tempo (Evolução Acumulada)
        List<GundamKit> allKits = kits.findAll();
        // Ordenar por data de compra (nulos no final ou excluidos)
        List<GundamKit> sortedKits = allKits.stream()
                .filter(k -> k.getDataCompra() != null)
                .sorted(Comparator.comparing(GundamKit::getDataCompra))
                .collect(Collectors.toList());

        Map<String, BigDecimal> evolution = new LinkedHashMap<>();
        BigDecimal cumulative = BigDecimal.ZERO;

        for (GundamKit k : sortedKits) {
            cumulative = cumulative.add(k.getPreco());
            // Usar string ISO (YYYY-MM-DD) como chave. Se houver múltiplas compras no dia,
            // sobrescreve com o novo total acumulado
            evolution.put(k.getDataCompra().toString(), cumulative);
        }
        reportData.put("evolutionData", evolution);

        return reportData;
    }

}
