package com.dev.money.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dev.money.entity.ProfileEntity;


public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // optional is used to handle null pointer exception
    // behind it run select * from tbl_profiles where email=email;
    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByActivationToken(String activationToken);

}
