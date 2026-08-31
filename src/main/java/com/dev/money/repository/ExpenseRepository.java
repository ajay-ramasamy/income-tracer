package com.dev.money.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.money.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {

    // select * from tbl_expenses where profile_id=id and order by date desc;
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id=:profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate,LocalDate endDate,String keyword,Sort sort);

    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

}
