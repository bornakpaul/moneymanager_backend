package com.apptechlab.moneymanager.repository;

import com.apptechlab.moneymanager.entity.ExpenseEntity;
import com.apptechlab.moneymanager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {


    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);


    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    void deleteByProfileId(Long profileId);

    @Query("SELECT MONTH(i.date) as month, SUM(i.amount) as total " +"FROM IncomeEntity i " +"WHERE i.profile.id = :profileId " +
            "AND i.date BETWEEN :startDate AND :endDate " +"GROUP BY MONTH(i.date)")
    List<Object[]> aggregateDataByMonth(Long profileId, LocalDate startDate, LocalDate endDate);
}
