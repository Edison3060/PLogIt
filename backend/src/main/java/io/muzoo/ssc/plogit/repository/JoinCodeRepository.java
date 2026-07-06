package io.muzoo.ssc.plogit.repository;

import io.muzoo.ssc.plogit.domain.JoinCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoinCodeRepository extends JpaRepository<JoinCode, Long> {

    Optional<JoinCode> findByCode(String code);
}
