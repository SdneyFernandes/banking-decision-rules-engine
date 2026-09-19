CREATE TABLE rule_definition (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_rule_definition_priority
                             CHECK ( priority >= 0 ),
    CONSTRAINT chk_rule_definition_status
                             CHECK (
                                 status IN (
                                     'DRAFT',
                                           'APPROVED',
                                           'PUBLISHED',
                                           'RETIRED'
                                     )
                                 )
);