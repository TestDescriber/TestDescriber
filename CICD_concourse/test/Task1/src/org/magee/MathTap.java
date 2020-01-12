/*
 * MathTap.java
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

package org.magee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

import Jama.Matrix;

import org.magee.math.*;
import org.magee.util.*;
import org.magee.text.*;
import org.magee.ui.*;

/**
 * @author Anthony Magee
 */
@SuppressWarnings("serial")
public class MathTap extends JFrame {

    private static JTextArea text = new JTextArea();

    /** Creates a new instance of MathTap */
    public MathTap() {
        super("RealTap test UI");
        add(new JScrollPane(text));
        setBounds(30, 30, 600, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        text.append("Welcome to Math4J, " + System.getProperty("user.name") + ".\nBelow is a sample of some of the tools provided by this package.\n\n");

        text.append("-Package org.magee.math contains functions, complex numbers, and rational numbers.\n");
        text.append("\t+new Complex(Math.PI, 1)\n\t\t");
        text.append(new Complex(Math.PI, 1).toString());

        PolyFunction pf = new PolyFunction(new double[] { 1, 3, 4, -34, -51 });
        text.append("\n\n\t+new PolyFunction(new double[]{1, 3, 4, -34, -51})\n\t\t");
        text.append(pf.toString());
        text.append("\n\t\tReal roots: " + ArrayUtil.toString(pf.getRealRoots()));
        text.append("\n\t\tComplex roots: " + ArrayUtil.toString(pf.getComplexRoots()));

        Rational r = new Rational(13, 257);
        text.append("\n\n\t+new Rational(13, 257)\n\t\t");
        text.append(r.toString());
        text.append("\n\t\tatomic representaion: " + r.doubleValue());
        text.append("\n\t\tinverse: " + r.inverse());

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new MathTap();
        
        PolyFunction pf1 = new PolyFunction(new double[] {1, 0, 2}),
                     pf2 = new PolyFunction(new double[] {1, 0, 0}),
                     pf3 = new PolyFunction(new double[] {1, 0}),
                     pf4 = new PolyFunction(new double[] {1, 1}),
                     pf5 = new PolyFunction(new double[] {1});
        System.out.println(pf1.toString(true) + "  " + ArrayUtil.toString(pf1.getRealRoots()));
        System.out.println(pf2.toString(true) + "  " + ArrayUtil.toString(pf2.getRealRoots()));
        System.out.println(pf3.toString(true) + "  " + ArrayUtil.toString(pf3.getRealRoots()));
        System.out.println(pf4.toString(true) + "  " + ArrayUtil.toString(pf4.getRealRoots()));
        System.out.println(pf5.toString(true) + "  " + ArrayUtil.toString(pf5.getRealRoots()));
        
    }

    public static void getSystemInfo() {
        System.getProperties().list(System.out);
    }
}
