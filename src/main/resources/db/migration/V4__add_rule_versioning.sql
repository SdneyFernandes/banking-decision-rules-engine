ALTER TABLE rule_definition
    ADD COLUMN version INTEGER NOT NULL DEFAULT 1;

ALTER TABLE rule_definition
    ADD COLUMN rule_key UUID;

UPDATE rule_definition
SET rule_key = gen_random_uuid()
WHERE rule_key IS NULL;

ALTER TABLE rule_definition
    ALTER COLUMN rule_key SET NOT NULL;

ALTER TABLE rule_definition
    ADD CONSTRAINT chk_rule_definition_version
        CHECK (version > 0);

ALTER TABLE rule_definition
    ADD CONSTRAINT uq_rule_definition_rule_key_version
        UNIQUE (rule_key, version);
