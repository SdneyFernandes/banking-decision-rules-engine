package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import com.sdney.rulesengine.domain.rule.RuleCondition;
import com.sdney.rulesengine.domain.rule.ValueType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConditionEvaluatorTest {

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private OperatorResolver operatorResolver;

    @Mock
    private ComparisonStrategy strategy;

    @Mock
    private RuleCondition condition;

    @Mock
    private Facts facts;

    private ConditionEvaluator conditionEvaluator;

    @BeforeEach
    void setUp() {
        conditionEvaluator = new ConditionEvaluator(
                        valueConverter,
                        operatorResolver
                );
    }

    @Test
    void shouldEvaluateCondition() {

        when(condition.getFactKey()).thenReturn("amount");
        when(condition.getValueType()).thenReturn(ValueType.INTEGER);
        when(condition.getExpectedValue()).thenReturn("1000");
        when(condition.getOperator()).thenReturn(ComparisonOperator.GREATER_THAN);
        when(facts.get("amount")).thenReturn("1500");
        when(valueConverter.convert("1500", ValueType.INTEGER)).thenReturn(1500);
        when(valueConverter.convert("1000", ValueType.INTEGER)).thenReturn(1000);
        when(operatorResolver.resolve(ComparisonOperator.GREATER_THAN)).thenReturn(strategy);
        when(strategy.evaluate(1500, 1000)).thenReturn(true);

        boolean result = conditionEvaluator.evaluate(
                        condition,
                        facts
                );

        assertTrue(result);

        verify(facts).get("amount");
        verify(valueConverter).convert(
                        "1500",
                        ValueType.INTEGER
                );
        verify(valueConverter).convert(
                        "1000",
                        ValueType.INTEGER
                );
        verify(operatorResolver).resolve(
                        ComparisonOperator.GREATER_THAN
                );
        verify(strategy).evaluate(
                        1500,
                        1000
                );
    }

    @Test
    void shouldConvertExpectedValueAsListWhenOperatorIsIn() {

        List<Object> expectedValues =
                List.of(
                        "BR",
                        "AO",
                        "PT"
                );

        when(condition.getFactKey()).thenReturn("country");
        when(condition.getValueType()).thenReturn(ValueType.STRING);
        when(condition.getExpectedValue()).thenReturn("BR,AO,PT");
        when(condition.getOperator()).thenReturn(ComparisonOperator.IN);
        when(facts.get("country")).thenReturn("AO");
        when(valueConverter.convert(
                "AO",
                ValueType.STRING
        )).thenReturn("AO");

        when(valueConverter.convertList(
                "BR,AO,PT",
                ValueType.STRING
        )).thenReturn(expectedValues);

        when(operatorResolver.resolve(ComparisonOperator.IN)).thenReturn(strategy);
        when(strategy.evaluate(
                "AO",
                expectedValues
        )).thenReturn(true);

        boolean result = conditionEvaluator.evaluate(
                        condition,
                        facts
                );

        assertTrue(result);
        verify(valueConverter).convertList(
                        "BR,AO,PT",
                        ValueType.STRING
                );

        verify(strategy).evaluate(
                        "AO",
                        expectedValues
                );
    }

    @Test
    void shouldRejectNullRuleCondition() {

        assertThrows(IllegalArgumentException.class, () -> conditionEvaluator.evaluate(
                        null,
                        facts
                )
        );
    }

    @Test
    void shouldRejectNullFacts() {
        assertThrows(IllegalArgumentException.class, () -> conditionEvaluator.evaluate(condition, null));
    }

    @Test
    void shouldRejectNullValueConverter() {

        assertThrows(IllegalArgumentException.class, () -> new ConditionEvaluator(null, operatorResolver));
    }

    @Test
    void shouldRejectNullOperatorResolver() {

        assertThrows(IllegalArgumentException.class, () -> new ConditionEvaluator(
                        valueConverter,
                        null
                )
        );
    }
}