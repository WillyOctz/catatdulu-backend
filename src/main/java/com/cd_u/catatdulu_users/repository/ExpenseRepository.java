package com.cd_u.catatdulu_users.repository;

import com.cd_u.catatdulu_users.model.ExpenseModel;
import com.cd_u.catatdulu_users.model.UserModel;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseModel, Long> {

    // select * from expenses where profile_id = ?1 order by date desc
    @Query("SELECT e FROM ExpenseModel e WHERE e.profile.id = :profile_id ORDER BY e.date DESC")
    List<ExpenseModel> findByProfileIdOrderByDateDesc(@Param("profile_id") Long profileId);

    // select * from expenses where profile_id = ?1 order by top 5 date desc
    @Query("SELECT e FROM ExpenseModel e WHERE e.profile.id = :profile_id ORDER BY e.date DESC LIMIT 5")
    List<ExpenseModel> findTop5ByProfileIdOrderByDateDesc(@Param("profile_id") Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseModel e WHERE e.profile.id = :profile_id")
    BigDecimal findTotalExpenseByProfileId(@Param("profile_id") Long profileId);

    // select * from expenses where profile_id = ?1 and date between ?2 and ?3 and name like %s4%
    @Query("SELECT e FROM ExpenseModel e WHERE e.profile.id = :profile_id AND e.date BETWEEN :startDate AND :endDate AND LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ExpenseModel> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            @Param("profile_id") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword,
            Sort sort
    );

    @Query("SELECT e FROM ExpenseModel e WHERE e.profile.id = :profile_id AND e.date BETWEEN :startDate AND :endDate")
    List<ExpenseModel> findByProfileAndDateBetween(@Param("profile_id") Long profileId, @Param("startDate") LocalDate startDate ,@Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM ExpenseModel e WHERE e.profile.id = :profile_id AND e.date = :date")
    List<ExpenseModel> findByProfileIdAndDate(@Param("profile_id") Long profileId, @Param("date") LocalDate date);
}
