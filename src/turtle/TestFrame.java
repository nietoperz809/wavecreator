/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turtle;

import java.awt.BorderLayout;
import javax.swing.JButton;

/**
 *
 * @author Administrator
 */
public class TestFrame extends javax.swing.JFrame
{
    public Turtle turtle = new DoubleBufferedTurtle(800, 600);

    /**
     * Creates new form NewJFrame
     */
    public TestFrame()
    {
        initComponents();
        add(turtle, BorderLayout.CENTER);
        add(new JButton("hallo"), BorderLayout.NORTH);
        this.pack();
    }

    static void crazyBush (Turtle t) throws Exception
    {
        // Crazy Bush
        t.setPenPositionAbsolute(300, 600);
        t.setLindeLineLength(20.0);
        t.setLindeRecursionNumber(5);
        t.setLindeAngle(20.5);
        t.setLindeAxiom("x");
        t.setLindeRule("x->FF[+FFx]-FFx", 0.8);
        t.setLindeFinalRule("x->-R-F-R-F-R-F-R-F-R", 0.9);
        t.lindeDraw();
        t.doneGraphics();
    }

    static void esoTree (Turtle t) throws Exception
    {
        // Crazy Bush
        t.setPenPositionAbsolute(300, 600);
        t.setLindeLineLength(5.0);
        t.setLindeRecursionNumber(12);
        t.setLindeAngle(20.5);
        t.setLindeAxiom("LFV");
        t.setLindeRule("L->LF", 1.0);
        t.setLindeRule("V->[+LFV][-LFV]", 1.0);
        //t.setLindeFinalRule("x->-R-F-R-F-R-F-R-F-R", 0.9);
        t.lindeDraw();
        t.doneGraphics();
    }

    static void insel (Turtle t) throws Exception
    {
        // Crazy Bush
        t.setPenPositionAbsolute(300, 300);
        t.setLindeLineLength(5.0);
        t.setLindeRecursionNumber(2);
        t.setLindeAngle(90.0);
        t.setLindeAxiom("F+F+F+F");
        t.setLindeRule("f->ffff");
        t.setLindeRule("F->F+f-FF+F+FF+Ff+FF-f+FF-F-FF-Ff-FFF");
        //t.setLindeFinalRule("x->-R-F-R-F-R-F-R-F-R", 0.9);
        t.lindeDraw();
        t.doneGraphics();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception
    {
        TestFrame j = new TestFrame();
        //crazyBush (j.turtle);
        insel(j.turtle);
        System.out.println (j.turtle.getLindeResult());
        j.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}