/**
 * ExponentialFunction.java A rudimentary class that describes an exponential
 * function.
 */

/* This file is part of Math4J.
 * Math4J is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Math4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Math4J; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * Copyright 2005 Anthony Magee
 */

package org.magee.math;

// import other packages

/**
 * The <code>ExponentialFunction</code> class defines an exponential function.
 * <p>
 * 
 * It contains methods for exploring the properties of such a function. There
 * are methods for finding values, area under the curve, derivative, etc.
 * <p>
 * 
 * Stored in this class is the base to represent an equation of the form<br>
 * base ^ x
 * 
 * @author Anthony W. Magee
 * @version 1.0,November 6, 2005
 */

/*
 * version changes
 */
public class ExponentialFunction extends Function {
    static final long serialVersionUID = 1L;

    // private members
    private double base; // the base the exponential

    /**
     * Simple constructor that defines base to be 0.
     * The function is 0 ^ x.
     */
    public ExponentialFunction() {
        base = 0.0;
    }

    /**
     * Default constructor that defines the varibles of the function.
     * 
     * @param base The double that is the base of the exponential.
     */
    public ExponentialFunction(double base) {
        this.base = base;
    }

    /**
     * Returns the double that is the base of the exponential.
     * 
     * @return The base.
     */
    public double getBase() {
        return base;
    }

    /**
     * Sets the double that is the base of the exponential.
     * 
     * @param base
     */
    public void setBase(double base) {
        this.base = base;
    }
    
    /**
     * Evaluates the value of the function at x.
     * 
     * @param x The value to evaluate the function at.
     * @return The y value.
     */
    public double valueAt(double x) {
        return Math.pow(base, x);
    }

    /**
     * Evaluates the value of the derivative of the function at x.
     */
    public double derivValueAt(double x) {
        return Math.pow(base, x) * Math.log(base);
    }

    /**
     * Evaluates the value of the antiderivative of the function at x.
     */
    public double antiderivValueAt(double x) {
        return Math.pow(base, x) / Math.log(base);
    }

    /**
     * Evaluates the area under the curve from <code>x1</code> to <code>x2</code>.
     * 
     * @param x1 Lower bound.
     * @param x2 Upper bound.
     * @return The total area under the curve.
     */
    public double area(double x1, double x2) {
        return antiderivValueAt(x2) - antiderivValueAt(x1);
    }

    /**
     * Returns a string representation of this exponential function.
     */
    public String toString() {
        return base + " ^ x";
    }

    @Override
    public Function getDerivFunc() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Function getAntiderivFunc() {
        // TODO Auto-generated method stub
        return null;
    }
}

final class NaturalExpFunction extends ExponentialFunction {
    static final long serialVersionUID = 1L;
    
    public NaturalExpFunction() {
        super(Math.E);
    }
}