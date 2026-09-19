CREATE TABLE condition_group (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    rule_definition_id BIGINT NOT NULL,
    logical_operator VARCHAR(10) NOT NULL,


    CONSTRAINT fk_condition_group_rule_definition
                             FOREIGN KEY (rule_definition_id)
                             REFERENCES rule_definition(id),
    CONSTRAINT chk_condition_group_logical_operator
                             CHECK ( logical_operator IN ('AND', 'OR') )
);