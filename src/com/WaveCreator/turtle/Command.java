/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.WaveCreator.turtle;

/**
 * Class that serves as skeleton for Commands and arguments
 * @author Administrator
 */
public class Command
{
    /**
     * All commands as ENUM
     */
    enum CMD
    {
        F, f, R, r, plus, minus, draw, drawabs, move, moveabs, mult, color, reset, pensize, finalrule, push, pop, lindedraw, lindeaxiom, linderule, linderec, lindeangle, lindestep
    }
    /**
     * The actual command
     */
    CMD command;
    /**
     * Up to three arguments
     */
    Object n1;
    Object n2;
    Object n3;
}
