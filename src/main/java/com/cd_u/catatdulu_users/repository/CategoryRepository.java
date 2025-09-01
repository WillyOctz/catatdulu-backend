package com.cd_u.catatdulu_users.repository;

import com.cd_u.catatdulu_users.model.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

    // return a list of profile id
    List<CategoryModel> findByProfileId(Long profileId);

    // return an id and profileid
    Optional<CategoryModel> findByIdAndProfileId(Long id, Long profileId);

    // return a list of type and profile id from the Categoty Model
    List<CategoryModel> findByTypeAndProfileId(String type, Long profileId);

    // searching for a record of the name and profile id, if exists, true. if not, false
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
