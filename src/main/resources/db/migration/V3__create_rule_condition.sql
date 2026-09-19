CREATE TABLE rule_condition (
                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                condition_group_id BIGINT NOT NULL,
                                fact_key VARCHAR(100) NOT NULL,
                                operator VARCHAR(30) NOT NULL,
                                value_type VARCHAR(20) NOT NULL,
                                expected_value VARCHAR(500) NOT NULL,

                                CONSTRAINT fk_rule_condition_condition_group
                                    FOREIGN KEY (condition_group_id)
                                        REFERENCES condition_group(id),

                                CONSTRAINT chk_rule_condition_operator
                                    CHECK (
                                        operator IN (
                                                     'EQUALS',
                                                     'NOT_EQUALS',
                                                     'GREATER_THAN',
                                                     'GREATER_THAN_OR_EQUALS',
                                                     'LESS_THAN',
                                                     'LESS_THAN_OR_EQUALS',
                                                     'IN',
                                                     'NOT_IN'
                                            )
                                        ),

                                CONSTRAINT chk_rule_condition_value_type
                                    CHECK (
                                        value_type IN (
                                                       'STRING',
                                                       'INTEGER',
                                                       'DECIMAL',
                                                       'BOOLEAN',
                                                       'DATE'
                                            )
                                        )
);