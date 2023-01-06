package com.mcreater.amcl.util.math;

public class Fraction extends Number {
    private final Number molecule;
    private final Number denominator;
    public Fraction(Number molecule, Number denominator) {
        if (denominator.doubleValue() == 0) throw new NumberFormatException("Denominator is zero.");
        this.molecule = molecule;
        this.denominator = denominator;
    }
    private double getDoubleInternal() {
        return molecule.doubleValue() / denominator.doubleValue();
    }
    public int intValue() {
        return (int) getDoubleInternal();
    }

    public long longValue() {
        return (long) getDoubleInternal();
    }

    public float floatValue() {
        return (float) getDoubleInternal();
    }

    public double doubleValue() {
        return getDoubleInternal();
    }
}
