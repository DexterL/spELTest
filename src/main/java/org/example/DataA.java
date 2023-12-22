package org.example;

import java.math.BigDecimal;

public class DataA extends Entity{
    private BigDecimal bigDecimal1;
    private BigDecimal bigDecimal2;
    private Double double1;
    private Double double2;

    public DataA(BigDecimal bigDecimal1, BigDecimal bigDecimal2) {
        this.bigDecimal1 = bigDecimal1;
        this.bigDecimal2 = bigDecimal2;
    }

    public DataA(int i, int i1) {
        this(new BigDecimal(i), new BigDecimal(i1));
    }

    public DataA(double d, double d1) {
        this.double1 = d;
        this.double2 = d1;
    }

    public BigDecimal getBigDecimal1() {
        return bigDecimal1;
    }

    public void setBigDecimal1(BigDecimal bigDecimal1) {
        this.bigDecimal1 = bigDecimal1;
    }

    public BigDecimal getBigDecimal2() {
        return bigDecimal2;
    }

    public void setBigDecimal2(BigDecimal bigDecimal2) {
        this.bigDecimal2 = bigDecimal2;
    }
}
