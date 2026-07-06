package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.JoinCode;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.JoinCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
public class JoinCodeService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private static final String CODE_PREFIX = "PL0G-";

    private final JoinCodeRepository joinCodeRepository;
    private final SecureRandom random = new SecureRandom();

    public JoinCodeService(JoinCodeRepository joinCodeRepository) {
        this.joinCodeRepository = joinCodeRepository;
    }

    public String generateFor(Engagement engagement, User generatedBy) {
        if (engagement.getCurrentJoinCode() != null) {
            revokeCode(engagement.getCurrentJoinCode());
        }

        String code;
        do {
            code = generateCode();
        } while (joinCodeRepository.findByCode(code).isPresent());

        JoinCode joinCode = JoinCode.builder()
            .code(code)
            .engagement(engagement)
            .generatedById(generatedBy.getId())
            .build();
        joinCodeRepository.save(joinCode);

        engagement.setCurrentJoinCode(code);
        return code;
    }

    @Transactional(readOnly = true)
    public Engagement resolveCode(String code) {
        JoinCode joinCode = joinCodeRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Invalid join code"));
        if (joinCode.getRevokedAt() != null) {
            throw new IllegalArgumentException("This join code has been revoked");
        }
        return joinCode.getEngagement();
    }

    private void revokeCode(String code) {
        joinCodeRepository.findByCode(code).ifPresent(jc -> {
            jc.setRevokedAt(java.time.Instant.now());
            joinCodeRepository.save(jc);
        });
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_PREFIX);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
