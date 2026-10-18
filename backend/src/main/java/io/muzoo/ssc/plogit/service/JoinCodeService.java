package io.muzoo.ssc.plogit.service;

import io.muzoo.ssc.plogit.domain.Engagement;
import io.muzoo.ssc.plogit.domain.EngagementAuditEvent;
import io.muzoo.ssc.plogit.domain.JoinCode;
import io.muzoo.ssc.plogit.domain.User;
import io.muzoo.ssc.plogit.repository.JoinCodeRepository;
import io.muzoo.ssc.plogit.repository.EngagementRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Map;

@Service
@Transactional
public class JoinCodeService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private static final String CODE_PREFIX = "PL0G-";

    private final JoinCodeRepository joinCodeRepository;
    private final EngagementRepository engagementRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SecureRandom random = new SecureRandom();

    public JoinCodeService(
        JoinCodeRepository joinCodeRepository,
        EngagementRepository engagementRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.joinCodeRepository = joinCodeRepository;
        this.engagementRepository = engagementRepository;
        this.eventPublisher = eventPublisher;
    }

    public String generateFor(Engagement engagement, User generatedBy) {
        boolean regenerated = engagement.getCurrentJoinCode() != null;
        if (regenerated) {
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
        engagementRepository.save(engagement);
        eventPublisher.publishEvent(new EngagementAuditEvent(
            generatedBy.getId(), engagement.getId(),
            regenerated ? "REGENERATE_JOIN_CODE" : "GENERATE_JOIN_CODE",
            "ENGAGEMENT", String.valueOf(engagement.getId()),
            Map.of("regenerated", regenerated)
        ));
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
