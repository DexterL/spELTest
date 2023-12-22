package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionEvaluatorTest {

    @DisplayName("基本运算")
    @Test
    void checkCases() throws ExpressionEvaluatorException {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        Assertions.assertTrue(evaluator.check("true and true"));
        Assertions.assertTrue(evaluator.check("-1 <= -1"));
        Assertions.assertFalse(evaluator.check("-1 < -1"));

        DataA data = new DataA(1, 2);
        Assertions.assertTrue(evaluator.check(data, "bigDecimal1 - bigDecimal2" + "== -1"));
        Assertions.assertFalse(evaluator.check(data, "bigDecimal1 - bigDecimal2", "< -1"));

        // in 没玩明白，暂时不写了
        //Assertions.assertFalse(evaluator.check(data, String.format("isMember(%s, {-1,-2})", res.toString())));
    }

    @DisplayName("数值计算用例（含空值默认处理）")
    @Test
    void invokeCases() throws ExpressionEvaluatorException {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        DataA data = new DataA(1, 2);
        Assertions.assertEquals(new BigDecimal(-1), evaluator.invoke(data, "bigDecimal1 - bigDecimal2"));

        data = new DataA(null, new BigDecimal(-1));
        Assertions.assertEquals(BigDecimal.ZERO, evaluator.invoke(data, "(bigDecimal1 ?: T(java.math.BigDecimal).ZERO)"));
        // 这样省事一点。其他变量如果想支持，需要在ExpressionEvaluator中添加
        Assertions.assertEquals(BigDecimal.ZERO, evaluator.invoke(data, "(bigDecimal1 ?: #BigDecimal.ZERO)"));

        Assertions.assertEquals(new BigDecimal(1), evaluator.invoke(data, "(bigDecimal1 ?: #BigDecimal.ZERO) - bigDecimal2"));

        data = new DataA(null, null);
        Assertions.assertEquals(BigDecimal.ZERO, evaluator.invoke(data, "(bigDecimal1 ?: #BigDecimal.ZERO) - (bigDecimal2 ?: #BigDecimal.ZERO)"));

    }

    @DisplayName("逻辑计算用例")
    @Test
    void invokeLogicalCases() throws ExpressionEvaluatorException {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        Assertions.assertFalse((boolean) evaluator.invoke("true and false"));

        Assertions.assertTrue((boolean) evaluator.invoke("1==1 and 2==2"));
        Assertions.assertTrue((boolean) evaluator.invoke("1==1 or 2!=2"));

        Assertions.assertTrue(evaluator.check(new DataA(1, 1), "bigDecimal1==1 and bigDecimal2==1"));
        Assertions.assertTrue(evaluator.check(new DataA(1, 2), "bigDecimal1==1 and bigDecimal2!=1"));

        // 由于expression部分最后一个字符可能是数字，连接assert的==字符串会产生歧义。需要使用时统一对expression加上括号
        // 下面check的方法中自带了加括号逻辑，如果时后面自己拼接的话，要注意加括号
        Assertions.assertTrue(evaluator.check(new DataA(1, 2), "bigDecimal1==1 and bigDecimal2!=1", "== true"));
        Assertions.assertTrue(evaluator.check(new DataA(1, 2), "bigDecimal1>0 and bigDecimal2>0", "== true"));
        Assertions.assertTrue(evaluator.check(new DataA(1, -2), "bigDecimal1>0 or bigDecimal2>0", "== true"));
        // 另一个写法
        Assertions.assertTrue(evaluator.check(new DataA(1, 2), "bigDecimal1==1 and bigDecimal2!=1" ));
        Assertions.assertTrue(evaluator.check(new DataA(1, 2), "bigDecimal1==1 and bigDecimal2!=1" , ""));
    }

    @Test
    void invokeNullExceptionCases() {
       ExpressionEvaluator evaluator = new ExpressionEvaluator();
       assertThrows(ExpressionEvaluatorException.class, () -> evaluator.invoke(
               new DataA(new BigDecimal(1), null), "bigDecimal1 - bigDecimal2"));
       assertThrows(ExpressionEvaluatorException.class, () -> evaluator.invoke(
               new DataA(null, null), "bigDecimal1 - bigDecimal2"));
    }

    @Test
    void invokeDivZeroExceptionCases() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        // 空值异常
        assertThrows(ArithmeticException.class, () -> evaluator.invoke(new DataA(1, 0), "bigDecimal1 / bigDecimal2"));
    }

    @Test
    void invokeCasesBetween() throws ExpressionEvaluatorException {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        DataA data = new DataA(1, 2);

        // between [-10, -1]
        Assertions.assertTrue((boolean) evaluator.invoke(data, "1 - 2 between {-10,-1}"));
        Assertions.assertTrue((boolean) evaluator.invoke(data, "1 - 2 between {-1,10}"));
        // between [-1,-10] illegal because : -1 > -10
        Assertions.assertFalse((boolean) evaluator.invoke(data, "1 - 2 between {-1,-10}"));
    }

    @DisplayName("Test for performance")
    @Disabled
    @Test()
    public void performanceTest() throws ExpressionEvaluatorException {
        ExpressionEvaluator evaluator = new ExpressionEvaluator();
        DataA data = new DataA(1, 2);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 1,000,000 times
        for (int i = 0; i < 10000 * 100; i++) {
            Assertions.assertTrue((boolean) evaluator.invoke(data, "1 - 2 between {-10,-1}"));
        }
        stopWatch.stop();
        System.out.println("time elapsed(sec):" + stopWatch.getTotalTimeSeconds());
    }

}