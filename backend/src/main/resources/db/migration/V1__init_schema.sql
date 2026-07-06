-- PLogIt - V1 initial schema.
-- Tables mirror the JPA entities in io.muzoo.ssc.plogit.domain.
-- Run by Flyway on application startup.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL    PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS engagements (
    id                     BIGSERIAL      PRIMARY KEY,
    name                   VARCHAR(255)   NOT NULL,
    description            VARCHAR(4000)  NOT NULL,
    start_date             DATE,
    due_date               DATE,
    status                 VARCHAR(20)    NOT NULL,
    leader_id              BIGINT         NOT NULL REFERENCES users(id),
    current_join_code      VARCHAR(255)   UNIQUE,
    allowed_hours          VARCHAR(1000),
    allowed_techniques     VARCHAR(4000),
    forbidden_techniques   VARCHAR(4000),
    emergency_contacts     VARCHAR(4000),
    out_of_scope           VARCHAR(4000),
    in_scope_targets       JSONB,
    objectives             JSONB,
    created_at             TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ    NOT NULL DEFAULT now(),
    archived_at            TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS engagement_members (
    id              BIGSERIAL    PRIMARY KEY,
    engagement_id   BIGINT       NOT NULL REFERENCES engagements(id),
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    role            VARCHAR(20)  NOT NULL,
    joined_via      VARCHAR(20)  NOT NULL,
    joined_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    removed_at      TIMESTAMPTZ,
    CONSTRAINT uk_engagement_members_engagement_user UNIQUE (engagement_id, user_id)
);

CREATE TABLE IF NOT EXISTS join_codes (
    id              BIGSERIAL    PRIMARY KEY,
    code            VARCHAR(255) NOT NULL,
    engagement_id   BIGINT       NOT NULL REFERENCES engagements(id),
    generated_by_id BIGINT       NOT NULL REFERENCES users(id),
    revoked_at      TIMESTAMPTZ,
    generated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_join_codes_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS logs (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    engagement_id       BIGINT       NOT NULL REFERENCES engagements(id),
    author_id           BIGINT       NOT NULL REFERENCES users(id),
    activity_type       VARCHAR(40)  NOT NULL,
    title               VARCHAR(255) NOT NULL,
    description         TEXT         NOT NULL,
    result              TEXT         NOT NULL,
    target              VARCHAR(255),
    tool_used           VARCHAR(255),
    outcome             VARCHAR(20)  NOT NULL,
    tags                JSONB,
    code_block          TEXT,
    code_language       VARCHAR(40),
    review_state        VARCHAR(20)  NOT NULL,
    rejection_comment   TEXT,
    rejected_at         TIMESTAMPTZ,
    rejected_by_id      BIGINT       REFERENCES users(id),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_edited_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_edited_by_id   BIGINT       REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS log_versions (
    id              BIGSERIAL    PRIMARY KEY,
    log_id          UUID         NOT NULL REFERENCES logs(id) ON DELETE CASCADE,
    version_number  INTEGER      NOT NULL,
    snapshot        JSONB        NOT NULL,
    edited_by_id    BIGINT       NOT NULL REFERENCES users(id),
    edited_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS attachments (
    id              BIGSERIAL    PRIMARY KEY,
    log_id          UUID         NOT NULL REFERENCES logs(id) ON DELETE CASCADE,
    filename        VARCHAR(255) NOT NULL,
    storage_path    VARCHAR(1024) NOT NULL,
    mime_type       VARCHAR(100) NOT NULL,
    size            BIGINT       NOT NULL,
    uploaded_by_id  BIGINT       NOT NULL REFERENCES users(id),
    uploaded_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS scope_warnings (
    id                  BIGSERIAL    PRIMARY KEY,
    engagement_id       BIGINT       NOT NULL REFERENCES engagements(id),
    log_id              UUID         REFERENCES logs(id),
    target              VARCHAR(255) NOT NULL,
    acknowledged_by_id  BIGINT       NOT NULL REFERENCES users(id),
    acknowledged_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_log (
    id          BIGSERIAL    PRIMARY KEY,
    actor_id    BIGINT       NOT NULL REFERENCES users(id),
    action      VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id   VARCHAR(100),
    metadata    JSONB,
    timestamp   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_logs_engagement_created ON logs (engagement_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_logs_review_state        ON logs (review_state);
CREATE INDEX IF NOT EXISTS idx_audit_log_timestamp      ON audit_log (timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_engagement_members_user  ON engagement_members (user_id);
