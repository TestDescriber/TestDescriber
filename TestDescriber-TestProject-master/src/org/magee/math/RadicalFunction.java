/**
 * RadicalFunction.java A class for defining and exploring a radical function.
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

import org.magee.math.Function;

/**
 * @author Anthony Magee
 * @version 1.0 November 6, 2005
 */
public class RadicalFunction extends Function implements Serializable {
    static final long serialVersionUID = 1L;
    
    private double index;
    
    /**
     * 
     */
    public RadicalFunction() {
        this(1);
    }

    /**
     * 
     */
    public RadicalFunction(double index) {
        this.index = index;
    }

    /**
     * 
     */
    public String toString() {
        return "<" + index + "> √x";
    }

    /**
     * 
     */
    public double valueAt(double x) {
        return Math.pow(x, 1 / index);
    }

    /**
     * 
     */
    public double area(double x1, double x2) {
        if(x1 == x2)
            return 0.0;
        
        if(x2 > x1)
            if(x1 > 0.0)
                return antiderivValueAt(x2) - antiderivValueAt(x1);
            else
                throw new ArithmeticException("This function does not exist for x < 0");
        else
            throw new ArithmeticException("Error x1 > x2; please enter as x1 < x2");               
    }

    /**
     * Evaluates the value of the derivative of the function at x.
     */
    public double derivValueAt(double x) {
        return Math.pow(x, 1 / index - 1) / index;
    }

    /**
     * Evaluates the value of the antiderivative of the function at x.
     */
    public double antiderivValueAt(double x) {
        return Math.pow(x, 1 / index + 1) * index / (index + 1);
    }
    
    /**
     * 
     */
    public Function getDerivFunc() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     */
    public Function getAntiderivFunc() {
        // TODO Auto-generated method stub
        return null;
    }
}
