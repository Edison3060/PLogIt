-- Prevent duplicate snapshot numbers for the same log.
-- Resolve any pre-existing duplicates before applying this constraint.

ALTER TABLE log_versions
    ADD CONSTRAINT uk_log_versions_log_version UNIQUE (log_id, version_number);
