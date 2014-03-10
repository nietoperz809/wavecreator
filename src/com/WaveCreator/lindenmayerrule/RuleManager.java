/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.WaveCreator.lindenmayerrule;

import java.util.ArrayList;

/**
 * Stores and applies Rules
 *
 * @author Administrator
 */
public final class RuleManager
{
    private String axiom = "";
    private final ArrayList<Rule> rules = new ArrayList<>();
    private final ArrayList<Rule> finalrules = new ArrayList<>();
    private int recursions = 1;
    private final String SEPARATOR = "->";

    /**
     * Empty Constructor
     */
    public RuleManager()
    {

    }

    /**
     * Clears the Rulemanager</br>
     * This involves all Rules and the Axiom
     */
    private void clear()
    {
        rules.clear();
        finalrules.clear();
        axiom = "";
    }

    /**
     * Constructor that sets the Axiom
     *
     * @param a the Axiom string
     */
    public RuleManager(String a)
    {
        setAxiom(a);
    }

    /**
     * Set number of Recursions e.g. how many times the Rule is applied
     *
     * @param rec
     */
    public void setRecursions(int rec)
    {
        recursions = rec;
    }

    /**
     * Set the axiom (that is the starting point of Lindenmayer Systems
     *
     * @param a Axiom String
     */
    public void setAxiom(String a)
    {
        clear();
        axiom = a;
    }

    /**
     * Sets a Rule of the form key->replacement. -> is used to separate them
     *
     * @param r the Rule as mentioned
     * @param probability Probability, 1.0 == apply always, 0.0 == apply never
     */
    public void setRule(String r, double probability)
    {
        Rule x = makeRule (r, probability);
        if (x != null)
            rules.add(x);
    }

    private Rule makeRule(String r, double probability)
    {
        if (r == null)
        {
            return null;
        }
        String[] two = r.split(SEPARATOR);
        if (two.length != 2)
        {
            return null;
        }
        return new Rule(two[0], two[1], probability);
    }
    
    /**
     * Sets final Rule that is: this rules are applied as the last step of the
     * transformation
     *
     * @param r the Rule as mentioned
     * @param probability Probability, 1.0 == apply always, 0.0 == apply never
     */
    public void setFinalRule(String r, double probability)
    {
        Rule x = makeRule (r, probability);
        if (x != null)
            finalrules.add(x);
    }

    /**
     * Get the Axiom
     *
     * @return the axiom
     */
    public String getAxiom()
    {
        return axiom;
    }

    /**
     * Apply all Rules in as List
     *
     * @param list List containing the Rule
     * @param temp Target string
     * @return string with all rules applied
     */
    private String applyRules(ArrayList<Rule> list, String temp)
    {
        for (Rule rule : list)
        {
            temp = temp.replace(rule.getKey(), "" + rule.hashCode());
        }
        for (Rule rule : list)
        {
            String hash = "" + rule.hashCode();
            while (temp.contains(hash))
            {
                if (Math.random() < rule.getProbability())
                {
                    temp = temp.replaceFirst(hash, rule.getReplacement());
                }
                else
                {
                    temp = temp.replaceFirst(hash, rule.getKey());
                }
            }
        }
        return temp;
    }

    /**
     * Get the final result
     *
     * @return Lindenmayer System after all Rules were applied
     */
    public String getResult()
    {
        String temp = axiom;
        for (int s = 0; s < recursions; s++)
        {
            temp = applyRules(rules, temp);
        }
        temp = applyRules(finalrules, temp);
        return temp;
    }
}
