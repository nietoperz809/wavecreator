package com.WaveCreator.MethodInvoking;

import com.WaveCreator.Functions.Functions;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This class is used to invoke methods by reflection
 */
public class MethodInvoker extends JDialog implements ActionListener
{
    private JTable m_table;
    private final Method m_method;
    private final Object m_object;
    private final MethodInvokerResult m_result = new MethodInvokerResult();

    /**
     * Reads comma-separated integer values and creates an integer array
     * @param value String containing all values
     * @return A new int-array
     */
    private int[] readIntegerArray(String value)
    {
        ArrayList<Integer> li = new ArrayList<>();
        String[] vals = value.split(",");
        for (String val : vals)
        {
            val = val.trim();
            li.add(Integer.parseInt(val));
        }
        int[] res = new int[li.size()];
        for (int s = 0; s < res.length; s++)
        {
            res[s] = li.get(s);
        }
        return res;
    }

    private String[] readStringArray (String value)
    {
        ArrayList<String> li = new ArrayList<>();
        String[] vals = value.split(",");
        for (String val : vals)
        {
            val = val.trim();
            li.add(val);
        }
        String[] res = new String[li.size()];
        for (int s = 0; s < res.length; s++)
        {
            res[s] = li.get(s);
        }
        return res;
    }
    
    private short[] readShortArray(String value)
    {
        ArrayList<Short> li = new ArrayList<>();
        String[] vals = value.split(",");
        for (String val : vals)
        {
            val = val.trim();
            li.add(Short.parseShort(val));
        }
        short[] res = new short[li.size()];
        for (int s = 0; s < res.length; s++)
        {
            res[s] = li.get(s);
        }
        return res;
    }

    /**
     * Reads comma-separated double values and creates a double array
     * @param value String containing all values
     * @return A new double-array
     */
    private double[] readDoubleArray(String value)
    {
        ArrayList<Double> li = new ArrayList<>();
        String[] vals = value.split(",");
        for (String val : vals)
        {
            val = val.trim();
            li.add(Double.parseDouble(val));
        }
        double[] res = new double[li.size()];
        for (int s = 0; s < res.length; s++)
        {
            res[s] = li.get(s);
        }
        return res;
    }

    /**
     * Creates object or array of objects
     * Called when user hit the OK button
     * @param typename String that is the type of that object
     * @param value    String that contains object value(s)
     * @return The new object
     */
    private Object parseObject(String typename, String value)
    {
        try
        {
            if (typename.equals("boolean"))
            {
                return Boolean.parseBoolean(value);
            }
            if (typename.equals("int"))
            {
                return Integer.parseInt(value);
            }
            if (typename.equals("float"))
            {
                return Float.parseFloat(value);
            }
            if (typename.equals("double"))
            {
                return Double.parseDouble(value);
            }
            if (typename.equals("char"))
            {
                return value.charAt(0);
            }
            if (typename.equals("long"))
            {
                return Long.parseLong(value);
            }
            if (typename.equals("String"))
            {
                return value;
            }
            if (typename.equals("String[]"))
            {
                return readStringArray(value);
            }
            if (typename.equals("int[]"))
            {
                return readIntegerArray(value);
            }
            if (typename.equals("double[]"))
            {
                return readDoubleArray(value);
            }
            if (typename.equals("short[]"))
            {
                return readShortArray(value);
            }
        }
        // Make default values if something failed
        catch (NumberFormatException ex)
        {
            if (typename.equals("boolean"))
            {
                return true;
            }
            if (typename.equals("int"))
            {
                return 0;
            }
            if (typename.equals("float"))
            {
                return (float) 0;
            }
            if (typename.equals("double"))
            {
                return (double) 0;
            }
            if (typename.equals("char"))
            {
                return (char) 0;
            }
            if (typename.equals("long"))
            {
                return (long) 0;
            }
            if (typename.equals("String"))
            {
                return "";
            }
            if (typename.equals("int[]"))
            {
                return new int[]{0};
            }
            if (typename.equals("double[]"))
            {
                return new double[]{0};
            }
            if (typename.equals("short[]"))
            {
                return new short[]{0};
            }
        }
        return null;
    }

    /**
     * User clicked OK button
     * @param e Swing Action event
     */
        @Override
    public void actionPerformed(ActionEvent e)
    {
        // Possibly cancel cell editing
        CellEditor ce = m_table.getCellEditor();
        if (ce != null)
        {
            ce.stopCellEditing();
        }

        // Build Object array depending on table content
        // and invoke the method with that information
        int rows = m_table.getModel().getRowCount();
        Object[] args = new Object[rows];
        String params = "";
        for (int s = 0; s < rows; s++)
        {
            String type = (String) m_table.getModel().getValueAt(s, 0);
            String value = (String) m_table.getModel().getValueAt(s, 1);
            args[s] = parseObject(type, value);
            params = params + type + ":" + value + " ";
        }
        try
        {
            m_result.parameters = params;
            m_result.resultobject = m_method.invoke(m_object, args);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
        {
            e1.printStackTrace();
        }

        // Close the dialog after that
        dispose();
    }

    /**
     * Constructor
     * @param owner                    Owning frame
     * @param functions Object instance for method that we should call
     * @param method                   The method to be called
     */
    public MethodInvoker (Frame owner, Functions functions, Method method)
    {
        // Initialize dialog and instance variables
        super(owner, true);
        setTitle (method.getName());
        m_method = method;
        m_object = functions;

        // If method has no arguments
        // call it immediately and dispose the dialog and exit from here
        if (method.getParameterTypes().length == 0)
        {
            try
            {
                m_result.parameters = null;
                m_result.resultobject = m_method.invoke(m_object);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
            dispose();
            return;
        }

        // get param types and descriptions
        // and build an Object[][] for the table
        Class<?>[] paramTypes = m_method.getParameterTypes();
        Annotation[][] ann = method.getParameterAnnotations();
        Object[][] data = new Object[paramTypes.length][];

        for (int s = 0; s < paramTypes.length; s++)
        {
            String type = paramTypes[s].getName().replace("java.lang.", "");
            switch (type)
            {
                case "[LString;":
                    type = "String[]";
                    break;
                case "[I":
                    type = "int[]";
                    break;
                case "[D":
                    type = "double[]";
                    break;
                case "[S":
                    type = "short[]";
                    break;
            }
            String descr = "";
            if (ann != null)
            {
                if (ann[s].length != 0)
                {
                    descr = ann[s][0].toString();
                    descr = descr.substring(6 + descr.indexOf("value="), descr.length() - 1);
                }
            }
            data[s] = new Object[]{type, "", descr};
        }

        // Create and adjust the table
        m_table = new JTable(data, new String[]{"Type", "Value", "Description"});
        TableColumnModel cm = m_table.getColumnModel();
        cm.getColumn(0).setCellEditor(new NullEditor());
        cm.getColumn(2).setCellEditor(new NullEditor());
        cm.getColumn(2).setPreferredWidth(400);

        // Set the layout and createEmptyCopy a button
        setLayout(new BorderLayout());
        JButton button = new JButton("Do it !!!");
        button.addActionListener(this);

        // Add everything to the dialog
        add(m_table.getTableHeader(), BorderLayout.NORTH);
        add(m_table, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

        // Center the dialog window and show it
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /**
     * Get the result object after method was invoked
     * @return Return value of invoked method
     */
    public MethodInvokerResult getResult()
    {
        return m_result;
    }

    /**
     * Convenience function that returns a string conatining all method param descriptions
     * @param method Method to be queried
     * @return The string ^^
     */
    public static String getMethodParameterDescriptions(Method method)
    {
        String res = " [";
        Annotation[][] ann = method.getParameterAnnotations();
        if (ann != null)
        {
            for (Annotation[] anAnn : ann)
            {
                if (anAnn.length != 0)
                {
                    String descr = anAnn[0].toString();
                    descr = descr.substring(6 + descr.indexOf("value="), descr.length() - 1);
                    res = res + descr + ", ";
                }
            }
        }
        if (res.charAt(res.length() - 1) != '[')
        {
            res = res.substring(0, res.length() - 2);
        }
        res += "]";
        return res;
    }
}
