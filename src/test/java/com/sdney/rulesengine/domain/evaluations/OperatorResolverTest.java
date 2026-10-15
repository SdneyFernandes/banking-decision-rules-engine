package com.sdney.rulesengine.domain.evaluations;

import com.sdney.rulesengine.domain.rule.ComparisonOperator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OperatorResolverTest {

    @Test
    void shouldResolveRegisteredStrategy() {
        ComparisonStrategy strategy = mock(ComparisonStrategy.class);
        when(strategy.operator()).thenReturn(ComparisonOperator.EQUALS);

        OperatorResolver resolver = new OperatorResolver(
                List.of(strategy)
        );

        ComparisonStrategy result = resolver.resolve(ComparisonOperator.EQUALS);
        assertSame(strategy, result);
    }

    @Test
    void shouldRejectEmptyStrategies() {
        List<ComparisonStrategy> strategies = List.of();
        assertThrows(IllegalArgumentException.class, () -> new OperatorResolver(strategies));
    }

    @Test
    void shouldRejectNullStrategyInsideList() {

        List<ComparisonStrategy> strategies = new ArrayList<>();
        strategies.add(null);
        assertThrows(IllegalArgumentException.class, () -> new OperatorResolver(strategies));
    }

    @Test
    void shouldRejectDuplicateStrategyForOperator() {

        ComparisonStrategy firstStrategy = mock(ComparisonStrategy.class);
        ComparisonStrategy secondStrategy = mock(ComparisonStrategy.class);

        when(firstStrategy.operator()).thenReturn(ComparisonOperator.EQUALS);
        when(secondStrategy.operator()).thenReturn(ComparisonOperator.EQUALS);

        List<ComparisonStrategy> strategies = List.of(firstStrategy, secondStrategy);
        assertThrows(IllegalStateException.class, () -> new OperatorResolver(strategies)
        );
    }

    @Test
    void shouldRejectNullOperator() {

        ComparisonStrategy strategy = mock(ComparisonStrategy.class);
        when(strategy.operator()).thenReturn(ComparisonOperator.EQUALS);

        OperatorResolver resolver = new OperatorResolver(List.of(strategy));

        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(null));
    }

    @Test
    void shouldRejectWhenStrategyForOperatorDoesNotExist() {

        ComparisonStrategy strategy = mock(ComparisonStrategy.class);
        when(strategy.operator()).thenReturn(ComparisonOperator.EQUALS);

        OperatorResolver resolver = new OperatorResolver(List.of(strategy));
        assertThrows(IllegalStateException.class, () -> resolver.resolve(ComparisonOperator.GREATER_THAN)
        );
    }

}