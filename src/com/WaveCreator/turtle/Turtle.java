/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WaveCreator.turtle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class Turtle extends JPanel
{
    /**
     * Factor to make radians from degrees
     */
    static final double DEGFACTOR = (2 * Math.PI) / 360.0;
    /**
     * Handles and applies Rules
     */
    RuleManager rules = new RuleManager();
    /**
     * Current turtle position x/y
     */
    private Point currentTurtlePosition;
    /**
     * Current angle 
     */
    private double currentAngle;
    /**
     * Current command that is executed 
     */
    private int linenumber;
    /**
     * Holds all commands
     */
    private final ArrayList<Command> commands = new ArrayList<>();
    /**
     * Stack for positions and angles 
     */
    private final Stack<StackElement> stack = new Stack<>();
    /**
     * Background Color 
     */
    Color penColor = Color.BLACK;
    
//    static public Turtle fromString(String s)
//    {
//        Turtle t = new Turtle();
//        t.commands.append(s);
//        return t;
//    }

    /**
     * Constructor that sets Frame size
     * @param width Frame width
     * @param height Frame height
     */
    public Turtle(int width, int height)
    {
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Constructor that sets frame size to default values
     */
    public Turtle()
    {
        this(800, 600);
    }

    /**
     * Draws by using x/y as offsets to the current position
     * @param x X
     * @param y Y
     */
    public void drawToRelative(int x, int y)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.draw;
        cmd.n1 = x;
        cmd.n2 = y; 
        commands.add(cmd);
    }

    /**
     * Draws by using x/y as next position
     * @param x X
     * @param y Y
     */
    public void drawToAbsolute(int x, int y)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.drawabs;
        cmd.n1 = x;
        cmd.n2 = y; 
        commands.add(cmd);
    }

    /**
     * Calls drawToRelative but using a point
     * @param pt the Point
     */
    public void drawToRelative(Point pt)
    {
        drawToRelative(pt.x, pt.y);
    }

    /**
     * Set pen to new positions using x/y as offset
     * @param x X
     * @param y Y
     */
    public void setPenPositionRelative(int x, int y)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.move;
        cmd.n1 = x;
        cmd.n2 = y; 
        commands.add(cmd);
    }

    /**
     * Set pen to new absolute position
     * @param x X
     * @param y Y
     */
    public void setPenPositionAbsolute(int x, int y)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.moveabs;
        cmd.n1 = x;
        cmd.n2 = y; 
        commands.add(cmd);
    }

    /**
     * Calls setPenPositionRelative using a Point
     * @param pt the Point
     */
    public void setPenPositionRelative(Point pt)
    {
        setPenPositionRelative(pt.x, pt.y);
    }

    /**
     * Set multiplier e.g. step width
     * @param x Factor to multiply x
     * @param y Factor to multiply y
     */
    public void setStepFactor(int x, int y)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.mult;
        cmd.n1 = x;
        cmd.n2 = y; 
        commands.add(cmd);
    }

    /**
     * Same as above but using Point
     * @param pt Point that contains both multipliers for x/y   
     */
    public void setStepFactor(Point pt)
    {
        setStepFactor(pt.x, pt.y);
    }

    /**
     * Set new line color
     * @param r Red value 
     * @param g Green value
     * @param b Blue value
     */
    public void setPenColor(int r, int g, int b)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.color;
        cmd.n1 = r;
        cmd.n2 = g; 
        cmd.n3 = b; 
        commands.add(cmd);
    }

    /**
     * Same as above but uses color object
     * @param c The new color
     */
    public void setColor (Color c)
    {
        setPenColor (c.getRed(), c.getGreen(), c.getBlue());
    }
    
    /**
     * Resets the turtle e.g. moves pen to default position
     */
    public void reset()
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.reset;
        commands.add(cmd);
    }

    /**
     * Draws forward by using distance d and stored angle
     * @param d Distance to draw to
     */
    public void drawForward(int d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.F;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Moves pen forward using distance d and stored angle
     * @param d Distance to move to
     */
    public void moveForward(int d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.f;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Draws backwards by using distance d and stored angle
     * @param d Distance to draw to
     */
    public void drawBackward(int d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.R;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Moves pen backwards using distance d and stored angle
     * @param d Distance to move to
     */
    public void moveBackward(int d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.r;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Turns write direction left 
     * @param d Angle to turn
     */
    public void rotateLeft(double d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.plus;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Turns write direction right
     * @param d Angle to turn
     */
    public void rotateRight(double d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.minus;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Sets new pen size
     * @param p new pen size
     */
    public void setPenSize(int p)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.pensize;
        cmd.n1 = p;
        commands.add(cmd);
    }

    /**
     * Stores current position and angle on Stack
     */
    public void store()
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.push;
        commands.add(cmd);
    }

    /**
     * Restore current position and angle from stack.
     */
    public void restore()
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.pop;
        commands.add(cmd);
    }

    /**
     * Draws a Lindenmayer structure that was previously generated using other functions 
     */
    public void lindeDraw()
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.lindedraw;
        commands.add(cmd);
    }

    /**
     * Set Lindenmayer start axiom using {F,-,+} and constants
     * @param ax Axiom as String eg FF+F-F-F
     */
    public void setLindeAxiom(String ax)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.lindeaxiom;
        cmd.n1 = ax;
        commands.add(cmd);
    }

    /**
     * Sets Lindenmayer replacement rule of the form a->b
     * This means that a is replaced by b
     * -> serves as separator
     * More the one rules can be set simultaneously
     * @param r Lindenmayer Rule as sting
     * @param probability 1.0 -> Rule applies always, 0.0 -> never
     */
    public void setLindeRule(String r, double probability)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.linderule;
        cmd.n1 = r;
        cmd.n2 = probability;
        commands.add(cmd);
    }

    public void setLindeRule(String r)
    {
        setLindeRule (r, 1.0);
    }

    /**
     * Sets final replacement rule of the form a->b
     * Same as above but this rule is only applied as final step
     * -> serves as separator
     * More the one rules can be set simultaneously
     * @param r Final Rule as sting
     * @param probability 1.0 -> Rule applies always, 0.0 -> never
     */
    public void setLindeFinalRule(String r, double probability)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.finalrule;
        cmd.n1 = r;
        cmd.n2 = probability;
        commands.add(cmd);
    }

    public void setLindeFinalRule(String r)
    {
        setLindeFinalRule (r, 1.0);
    }
    
    /**
     * Set number of recursions (that is, how many times the rules are applied)
     * The default number is 1 if this function is never called
     * @param rec 
     */
    public void setLindeRecursionNumber(int rec)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.linderec;
        cmd.n1 = rec;
        commands.add(cmd);
    }
    
    /**
     * Sets the angle that is used when + or - is executed
     * The default value is 0.0 if this function is never called
     * @param d the Angle in Degrees
     */
    public void setLindeAngle(double d)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.lindeangle;
        cmd.n1 = d;
        commands.add(cmd);
    }

    /**
     * Set the length of as step when F or f are executed
     * The default value is 1 
     * @param n The step width
     */
    public void setLindeLineLength(double n)
    {
        Command cmd = new Command();
        cmd.command = Command.CMD.lindestep;
        cmd.n1 = n;
        commands.add(cmd);
    }

    public String getLindeResult() throws Exception
    {
        drawTurtleSteps(null);    
        return rules.getResult();
    }
    
    /**
     * Pushes current values (position and angle) onto stack
     */
    private void push()
    {
        stack.push(new StackElement(currentAngle, currentTurtlePosition));
    }

    /**
     * Pops new Values (position and angle) from stack
     * @throws Exception 
     */
    private void pop() throws Exception
    {
        StackElement e = stack.pop();
        currentAngle = e.getAngle();
        currentTurtlePosition = e.getPoint();
    }

    /**
     * Returns in * sc if sc is positive, else in/-sc
     * @param in Value
     * @param sc Multiplier
     * @return Result (See above)
     */
    private int applyScaling(int in, int sc)
    {
        if (sc < 0)
        {
            return in / -sc;
        }
        else
        {
            return in * sc;
        }
    }

    /**
     * Calculates new point from Data using polar coordinates
     * @param in Input point
     * @param distance Distance
     * @param angle Angle
     * @param mult Distance Multiplier
     * @param reverse 1 means forward, -1 means backwards
     * @return The new point
     */
    private Point newPoint(Point in, double distance, double angle, Point mult, int reverse)
    {
        Point n = new Point();
        angle *= DEGFACTOR;
        n.x = in.x + reverse * applyScaling((int) (distance * Math.cos(angle)), mult.x);
        n.y = in.y + reverse * applyScaling((int) (distance * Math.sin(angle)), mult.y);
        return n;
    }

    /**
     * Resembles Graphics.drawline with thickness
     * @param g Graphics context
     * @param x1 X from
     * @param y1 Y from
     * @param x2 X to
     * @param y2 Y to
     * @param thickness thickness of line 
     */
    private void drawThickLine (Graphics g, int x1, int y1, int x2, int y2, int thickness)
    {
        if (g == null)
            return;
        if (thickness == 0)
        {
            g.drawLine(x1, y1, x2, y2);
            return;
        }
        int dX = x2 - x1;
        int dY = y2 - y1;
        double linelength = Math.sqrt(dX * dX + dY * dY);
        double scale = (thickness) / (2 * linelength);
        double ddx = -scale * dY;
        double ddy = scale * dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;
        int xPoints[] = new int[4];
        int yPoints[] = new int[4];
        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;
        g.fillPolygon(xPoints, yPoints, 4);
    }

    /**
     * Executes one draw operation and updates Point to the next position 
     * @param g Graphics context
     * @param pos Current position
     * @param angle Angle
     * @param mult Distance Multiplier
     * @param pensize Pen Size
     * @param distance Distance
     * @param cmd F or R that means Forward or Reverse
     */
    private void doDraw (Graphics g, Point pos, double angle,
            Point mult, int pensize, double distance, char cmd)
    {
        Point p = newPoint(pos, distance, angle, mult, cmd == 'F' ? 1 : -1);
        if (g != null)
            drawThickLine (g, pos.x, pos.y, p.x, p.y, pensize);
        pos.x = p.x;
        pos.y = p.y;
    }

    void doneGraphics() throws Exception
    {
    }
    
    /**
     * Does all the drawing based on ArrayList with commands
     * @throws Exception Ex. that can occurs
     * @param g Graphics Context
     */
    void drawTurtleSteps (Graphics g1) throws Exception
    {
        Dimension dim = this.getSize();
        currentTurtlePosition = new Point(dim.width / 2, dim.height / 2);
        Point multiplicator = new Point(1, 1);
        if (g1 != null)
            g1.setColor(penColor);
        int x;
        int y;
        linenumber = 0;
        int pensize = 0;
        currentAngle = -90.0;
        double fixangle = 90.0;
        double fixstep = 10.0;
        for (Command cmd : commands)
        {
            linenumber++;
            switch (cmd.command)
            {
                case lindeangle:
                    fixangle = (Double) cmd.n1;
                    break;

                case lindestep:
                    fixstep = (Double) cmd.n1;
                    break;

                case lindeaxiom:
                    rules.setAxiom((String)cmd.n1);
                    break;

                case linderule:
                    rules.setRule((String)cmd.n1, (double)cmd.n2);
                    break;

                case finalrule:
                    rules.setFinalRule((String)cmd.n1, (double)cmd.n2);
                    break;

                case linderec:
                    rules.setRecursions((Integer)cmd.n1);
                    break;

                case lindedraw:
                    String tmp = rules.getResult();
                    //System.out.println("drawing:" + tmp);
                    for (int s=0; s<tmp.length(); s++)
                    {
                        char c = tmp.charAt(s);
                        switch (c)
                        {
                            case '/':
                                fixstep /= 2;
                                if (fixstep < 1) {
                                    fixstep = 1;
                }
                                break;
                            
                            case '*':
                                fixstep *= 2;
                                break;
                                
                            case '[':
                                push();
                                break;

                            case ']':
                                pop();
                                break;

                            case 'F':
                            case 'R':
                                doDraw (g1, currentTurtlePosition, currentAngle, multiplicator, pensize, fixstep, c);
                                break;

                            case 'f':
                            case 'r':
                                currentTurtlePosition = newPoint(currentTurtlePosition, fixstep, currentAngle, multiplicator, c == 'f' ? 1 : -1);
                                break;

                            case '+':
                                currentAngle -= fixangle % 360;
                                break;

                            case '-':
                                currentAngle += fixangle % 360;
                                break;
                        }
                    }
                    break;

                case pensize:
                    pensize = (Integer)cmd.n1;
                    break;

                case push:
                    push();
                    break;

                case pop:
                    pop();
                    break;

                case reset:
                    multiplicator = new Point(1, 1);
                    //abs = false;
                    currentTurtlePosition = new Point(dim.width / 2, dim.height / 2);
                    currentAngle = 0.0;
                    if (g1 != null)
                        g1.setColor(penColor);
                    break;

                case R:  // Draw Backwards
                case F:  // Draw Forward
                    doDraw (g1, currentTurtlePosition, currentAngle, multiplicator, 
                            pensize, (double)cmd.n1, cmd.command.name().charAt(0));
                    break;

                case r:  // Go Back
                case f:  // Go Forward
                    currentTurtlePosition = newPoint(currentTurtlePosition, (double)cmd.n1, 
                            currentAngle, multiplicator, cmd.command == Command.CMD.f ? 1 : -1);
                    break;

                case plus:
                    currentAngle -= (double)cmd.n1 % 360;
                    break;

                case minus:
                    currentAngle += (double)cmd.n1 % 360;
                    break;

                case color:
                    penColor = new Color((Integer)cmd.n1, (Integer)cmd.n2, (Integer)cmd.n3);
                    if (g1 != null)
                        g1.setColor(penColor);
                    break;

                case mult:
                    multiplicator.x = (Integer)cmd.n1;
                    multiplicator.y = (Integer)cmd.n2;
                    break;

                case moveabs:
                case drawabs:
                    x = applyScaling((Integer)cmd.n1, multiplicator.x);
                    y = applyScaling((Integer)cmd.n2, multiplicator.y);
                    if (cmd.command == Command.CMD.drawabs)
                    {
                        drawThickLine (g1, currentTurtlePosition.x, currentTurtlePosition.y, x, y, pensize);
                    }
                    currentTurtlePosition.x = x;
                    currentTurtlePosition.y = y;
                    break;

                case move:
                case draw:
                    x = currentTurtlePosition.x + applyScaling((Integer)cmd.n1, multiplicator.x);
                    y = currentTurtlePosition.y + applyScaling((Integer)cmd.n2, multiplicator.y);
                    if (cmd.command == Command.CMD.draw)
                    {
                        drawThickLine (g1, currentTurtlePosition.x, currentTurtlePosition.y, x, y, pensize);
                    }
                    currentTurtlePosition.x = x;
                    currentTurtlePosition.y = y;
                    break;
            }
        }
    }

    /**
     * Overridden paint function
     * Draws turtle graphics
     * @param g Graphics context
     */
    @Override
    public void paint(Graphics g)
    {
        try
        {
            drawTurtleSteps(g);
        }
        catch (Exception ex)
        {
            System.exit(-1);
        }
    }

    /**
     * Empty update function to prevent senseless canvas repaints
     * @param g 
     */
    @Override
    public void update(Graphics g)
    {
        //paint (g);
    }

    /**
     * Gets command list as human readable strings
     * TODO add useful content
     * @return 
     */
    @Override
    public String toString()
    {
        return commands.toString();
    }
}
