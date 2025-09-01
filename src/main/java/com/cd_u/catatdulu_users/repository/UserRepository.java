package com.cd_u.catatdulu_users.repository;

import com.cd_u.catatdulu_users.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {

    // for searching an email within the UserModel, ex. SELECT * from UserModel WHERE email = ?
    Optional<UserModel> findByEmail(String email);

    // for searching a activation_token generated in the UserModel
    Optional<UserModel> findByActivationToken(String activationToken);
}
