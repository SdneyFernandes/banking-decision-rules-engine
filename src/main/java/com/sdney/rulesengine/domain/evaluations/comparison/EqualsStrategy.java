package com.sdney.rulesengine.domain.evaluations.comparison;

import com.sdney.rulesengine.domain.evaluations.ComparisonStrategy;
import com.sdney.rulesengine.domain.rule.ComparisonOperator;

import java.util.Objects;

public class EqualsStrategy implements ComparisonStrategy {

    @Override
    public ComparisonOperator operator() {
        return ComparisonOperator.EQUALS;
    }

    @Override
    public boolean evaluate(Object expectedValue, Object actualValue) {

        /*
        poderimaos fazer simplesmente actualValue.equals(expectedValue) mais isso nao nos protegeria de
        nullpointerexception, se actualValue for null ele vai lançar essa exceção, dai esse Objects.equals
        é uma ferramenta muito boa do java para le dar com null porque se os dois forem null ele retorna true,
        se apenas um e nul ele retorna false, se nenhum dois dois é nul ele faz o equal normal , a ideia base é
        mesmo a de comparação , ele compara até o null como valor ao inves de lançar nullpointerexception
         */
        return Objects.equals(expectedValue, actualValue);
    }
}
