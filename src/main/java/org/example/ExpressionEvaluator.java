package org.example;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;

public class ExpressionEvaluator {
    public Object invoke(Object data, String expression) throws ExpressionEvaluatorException{
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            StandardEvaluationContext context = new StandardEvaluationContext(data);
            context.setVariable("BigDecimal", BigDecimal.class);
            return exp.getValue(context);
        } catch (SpelEvaluationException e) {
            throw new ExpressionEvaluatorException(e);
        }
    }
    public Object invoke(String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);
        return exp.getValue();
    }

    public Boolean check(String expression) {
        return (Boolean) invoke(expression);
    }

    public Boolean check(Object data, String expression) throws ExpressionEvaluatorException {
        return (Boolean) invoke(data, expression);
    }

    public Boolean check(Object data, String expression, String assert_expression) throws ExpressionEvaluatorException {
        Object value = invoke(data, expression);
        return check(String.format("(%s) %s", value, assert_expression));
    }
}
