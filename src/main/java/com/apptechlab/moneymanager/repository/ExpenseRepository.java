package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDescIdDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);


    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

    void deleteByProfileId(Long profileId);

    @Query("SELECT MONTH(e.date) as month, SUM(e.amount) as total " +"FROM ExpenseEntity e " +"WHERE e.profile.id = :profileId " +
            "AND e.date BETWEEN :startDate AND :endDate " +"GROUP BY MONTH(e.date)")
    List<Object[]> aggregateDataByMonth(Long profileId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT YEAR(e.date) as year, MONTH(e.date) as month, SUM(e.amount) as total " +
            "FROM ExpenseEntity e WHERE e.profile.id = :profileId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(e.date), MONTH(e.date) " +
            "ORDER BY YEAR(e.date) ASC, MONTH(e.date) ASC")
    List<Object[]> aggregateDataByMonthYear(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // For "Last 7/28 Days": Groups by specific Date
    @Query("SELECT e.date as date, SUM(e.amount) as total " +
            "FROM ExpenseEntity e WHERE e.profile.id = :profileId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.date ORDER BY e.date ASC")
    List<Object[]> aggregateDataByDay(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.amount) FROM ExpenseEntity i " +
            "WHERE i.profile.id = :profileId " +
            "AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal findTotalExpenseByProfileIdAndDateBetween(
            @Param("profileId") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // For Donut Chart: Aggregates totals per category
    @Query("SELECT e.category.name, SUM(e.amount) " +
            "FROM ExpenseEntity e WHERE e.profile.id = :profileId " +
            "AND e.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.category.name")
    List<Object[]> aggregateByCategory(@Param("profileId") Long profileId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}
