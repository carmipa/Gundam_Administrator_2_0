package br.com.gundam.repository;

import br.com.gundam.model.GundamKit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface GundamKitRepository
        extends JpaRepository<GundamKit, Long>, JpaSpecificationExecutor<GundamKit> {

    // NOVOS MÉTODOS PARA O RELATÓRIO
    @Query("SELECT SUM(k.preco) FROM GundamKit k")
    BigDecimal findCustoTotal();

    @Query("SELECT AVG(k.preco) FROM GundamKit k")
    Double findCustoMedio();

    @Query("FROM GundamKit WHERE preco = (SELECT MAX(preco) FROM GundamKit)")
    List<GundamKit> findKitsMaisCaros();

    @Query("FROM GundamKit WHERE preco = (SELECT MIN(preco) FROM GundamKit)")
    List<GundamKit> findKitsMaisBaratos();

    // AGREGADORES PARA GRÁFICOS
    @Query("SELECT k.grade.nome, COUNT(k) FROM GundamKit k GROUP BY k.grade.nome")
    List<Object[]> countKitsByGrade();

    @Query("SELECT k.universo.sigla, COUNT(k) FROM GundamKit k GROUP BY k.universo.sigla")
    List<Object[]> countKitsByUniverso();

    @Query("SELECT k.grade.nome, SUM(k.preco) FROM GundamKit k GROUP BY k.grade.nome")
    List<Object[]> sumCostByGrade();
}