package com.calculator;

public class Calculator {

    private final Adder adder;

    public Calculator(Adder adder) {
        this.adder = adder;
    }

    public int add(int a, int b) {
        return adder.add(a, b);
    }

    public int subtract(int a, int b) {
        return adder.add(a, -b);
    }

    public int multiply(int a, int b) {
        int result = 0;

        if (b < 0) {
            b = -b;
            a = -a;
        }

        for (int i = 0; i < b; i = adder.add(i, 1)) {
            result = adder.add(result, a);
        }

        return result;
    }
}
