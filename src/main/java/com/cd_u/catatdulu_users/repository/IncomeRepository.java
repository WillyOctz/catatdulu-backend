package com.cd_u.catatdulu_users.repository;


import com.cd_u.catatdulu_users.model.IncomeModel;
import com.cd_u.catatdulu_users.model.UserModel;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeModel, Long> {

    // select * from incomes where profile_id = ?1 order by date desc
    @Query("SELECT i FROM IncomeModel i WHERE i.profile.id = :profile_id ORDER BY i.date DESC")
    List<IncomeModel> findByProfileIdOrderByDateDesc(@Param("profile_id") Long profileId);

    // select * from incomes where profile_id = ?1 order by top 5 date desc
    @Query("SELECT i FROM IncomeModel i WHERE i.profile.id = :profile_id ORDER BY i.date DESC LIMIT 5")
    List<IncomeModel> findTop5ByProfileIdOrderByDateDesc(@Param("profile_id") Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeModel i WHERE i.profile.id = :profile_id")
    BigDecimal findTotalIncomeByProfileId(@Param("profile_id") Long profileId);

    // select * from incomes where profile_id = ?1 and date between ?2 and ?3 and name like %s4%
    @Query("SELECT i FROM IncomeModel i WHERE i.profile.id = :profile_id AND i.date BETWEEN :startDate AND :endDate AND LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<IncomeModel> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            @Param("profile_id") Long profileId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword,
            Sort sort
    );
    @Query("SELECT i FROM IncomeModel i WHERE i.profile.id = :profile_id AND i.date BETWEEN :startDate AND :endDate")
    List<IncomeModel> findByProfileAndDateBetween(@Param("profile_id") Long profileId ,@Param("startDate") LocalDate startDate ,@Param("endDate") LocalDate endDate);
}
