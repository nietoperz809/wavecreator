package com.WaveCreator.MethodInvoking;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Used to sort methods alphabetically
 */
public class MethodComparator implements Comparator<Method>
{
    /**
     * Comparison function for method names
     * @param o1 Method #1
     * @param o2 Method #2
     * @return Lexically comparison result
     */
    public int compare(Method o1, Method o2)
    {
        return o1.getName().compareTo(o2.getName());
    }
}
