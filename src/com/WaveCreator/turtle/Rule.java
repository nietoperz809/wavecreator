/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.WaveCreator.turtle;

/**
 *
 * @author Administrator
 */
public class Rule
{
    private final String key;
    private final String replacement;
    private final double probability;

    /**
     * Constructor: makes the Rule
     * @param k Key
     * @param r Replacement
     * @param p Probability
     */
    public Rule(String k, String r, double p)
    {
        key = k;
        replacement = r;
        probability = p;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the replacement
     */
    public String getReplacement()
    {
        return replacement;
    }

    /**
     * @return the probability
     */
    public double getProbability()
    {
        return probability;
    }
}
