/**
 * MatrixTable.java Class for displaying the class <code>Jama.Matrix</code>.
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

package org.magee.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import Jama.Matrix;

/**
 * This class is a JPanel extension for displaying the class
 * <code>com.magee.math.Matrix</code> as a lightweight component.
 * 
 * @author Anthony W. Magee
 * @version 1.0, May 16, 2002
 * @version 1.1, April 6, 2005 Replaced <code>Vector</code> with
 *          <code>ArrayList</code> to save overhead.
 */

/*
 * version changes
 */
public class MatrixTable extends JPanel implements Serializable {

    /**
     * Storage for the matrix given for the table.
     */
    private Matrix matrix;

    /**
     * Storage for the font given for the table.
     */
    private Font font;

    /**
     * Storage for the color given for the table.
     */
    private Color color;

    /**
     * Array to keep track of the text fields created.
     */
    private ArrayList<JTextField> fields = new ArrayList<JTextField>();

    /**
     * Constructor that allows user to define the font and color of the text.
     * 
     * @param m The <code>Matrix</code> object to be presented.
     * @param f The font for the text of this object.
     * @param c The color for the text of this object.
     */
    public MatrixTable(Matrix m, Font f, Color c) {
        this.matrix = m;
        this.font = f;
        this.color = c;

        fields.ensureCapacity(matrix.getRowDimension() * matrix.getColumnDimension());
        setLayout(new GridLayout(matrix.getRowDimension(), matrix.getColumnDimension(), 1, 1));
        setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        setBackground(Color.gray);
        setElements(matrix);
    }

    /**
     * Default constructor with a plain black font.
     * 
     * @param matrix The <code>Matrix</code> object to be presented.
     */
    public MatrixTable(Matrix matrix) {
        this(matrix, new Font("monospaced", Font.PLAIN, 12), Color.black);
    }

    /**
     * Defines each element in the table from the elements in the matrix.
     * 
     * @param matrix
     */
    private void setElements(Matrix matrix) {
        removeAll();
        double[][] elements = matrix.getArray();
        int rowMax = matrix.getRowDimension();
        for(int row = 0; row < rowMax; row++) {
            for(int col = 0; col < matrix.getColumnDimension(); col++) {
                JTextField l = new JTextField(Double.toString(elements[row][col]));
                l.setForeground(color);
                l.setFont(font);
                l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                l.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                l.getDocument().putProperty("name", Integer.toString(row * rowMax + col));
                l.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        changed(e);
                    }
                    public void insertUpdate(DocumentEvent e){
                        changed(e);
                    }
                    public void removeUpdate(DocumentEvent e){
                        changed(e);
                    }
                });
                add(l);
                fields.add(l);
            }
        }
    }

    private void changed(DocumentEvent de) {
        Document doc = de.getDocument();
        int name = Integer.parseInt((String) doc.getProperty("name"));
        int cols = matrix.getColumnDimension();
        
        try {
            matrix.set(name / cols, name % cols, Double.parseDouble(doc.getText(0, doc.getLength())));
        } catch(NumberFormatException e) {
            matrix.set(name / cols, name % cols, Double.NaN);
        } catch(BadLocationException bl){
            System.out.println("bad location - contact author with details and steps to reproduce");
        }
    }
    
    /**
     * Returns the matrix for this table.
     * 
     * @return This table's <code>Matrix</code> object.
     */
    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * Sets the matrix for this table.
     * 
     * @param m The matrix to display in this table.
     */
    public void setMatrix(Matrix m) {
        this.matrix = m;
        setElements(matrix);
        revalidate();
    }

    /**
     * Returns the text font for this table.
     * 
     * @return This table's text font.
     */
    public Font getTextFont() {
        return font;
    }

    /**
     * Sets the font for this table.
     * 
     * @param f The font of the text in this table.
     */
    public void setTextFont(Font f) {
        this.font = f;
        setElements(matrix);
        revalidate();
    }

    /**
     * Returns the text color for this table.
     * 
     * @return This table's text color.
     */
    public Color getTextColor() {
        return color;
    }

    /**
     * Sets the color for this table.
     * 
     * @param c The color of the text in this table.
     */
    public void setTextColor(Color c) {
        this.color = c;
        setElements(matrix);
        revalidate();
    }
    
    static final long serialVersionUID = 12L;
}