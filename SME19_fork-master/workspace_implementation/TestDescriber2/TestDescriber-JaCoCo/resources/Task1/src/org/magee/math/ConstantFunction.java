/**
 * ConstantFunction.java A class for describing a constant univariate function
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

import java.io.Serializable;

/**
 * The <code>ConstantFunction</code> models a constant function, a line.
 * There is nothing thricky about this class.
 * 
 * @author Anthony Magee
 * @version 1.0 November 6, 2005
 */

/* version changes
 * 
 */
public class ConstantFunction extends Function implements Serializable {
    static final long serialVersionUID = 1L;
    
    private double c;

    /**
     * An overloaded constructor that creates the function f(x) = 0.0.
     */
    public ConstantFunction() {
        this(0.0);
    }
    
    /**
     * Constructor to create a constant function where <code>c</code> is the value of the function.
     * 
     * @param c The value of the function.
     */
    public ConstantFunction(double c) {
        this.c = c;
    }

    /**
     * Returns a string representation of this function.
     */
    public String toString() {
        return "f( x ) = " + c;
    }
    /**
     * Finds the value of this function at x.
     * 
     * @param x The point in the domain.
     * @return c, always!
     */
    public double valueAt(double x) {
        return c;
    }

    /**
     * Finds the area under the curve from <code>x1</code> to <code>x2</code>.
     * 
     * @param x1 The lower bound.
     * @param x2 The upper bound.
     * @return The area under the curve.
     */
    public double area(double x1, double x2) {
        return (x2 - x1) * c;
    }

    /** 
     * Finds the derivative of this function.
     * 
     * @return A function that is the derivative of <code>this</code>.
     */
    public Function getDerivFunc() {
        return new ConstantFunction();
    }

    /**
     * Finds the antiderivative of this function.
     * 
     * @return A function that is the antiderivative of <code>this</code>.
     */
    public Function getAntiderivFunc() {
        return new PolyFunction(new double[] {c, 0});
    }
}
