package com.sdney.rulesengine;

import com.sdney.rulesengine.infrastructure.database.repository.ConditionGroupJpaRepository;
import com.sdney.rulesengine.infrastructure.database.repository.RuleConditionJpaRepository;
import com.sdney.rulesengine.infrastructure.database.repository.RuleDefinitionJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
@Import(BankingDecisionRulesEngineApplicationTests.RepositoryMocks.class)
class BankingDecisionRulesEngineApplicationTests {

    @Test
    void contextLoads() {
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class RepositoryMocks {

        @Bean
        RuleDefinitionJpaRepository ruleDefinitionJpaRepository() {
            return mock(RuleDefinitionJpaRepository.class);
        }

        @Bean
        ConditionGroupJpaRepository conditionGroupJpaRepository() {
            return mock(ConditionGroupJpaRepository.class);
        }

        @Bean
        RuleConditionJpaRepository ruleConditionJpaRepository() {
            return mock(RuleConditionJpaRepository.class);
        }
    }
}
