package com.WaveCreator.MethodInvoking;

import javax.swing.table.TableCellEditor;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.util.EventObject;

/**
 * Helper Class for disabling table cell editing
 * User: Administrator
 * Date: 21.12.2008
 * Time: 23:02:02
 */
class NullEditor implements TableCellEditor
{
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        return null;
    }

    public Object getCellEditorValue()
    {
        return null;
    }

    public boolean isCellEditable(EventObject anEvent)
    {
        return false;
    }

    public boolean shouldSelectCell(EventObject anEvent)
    {
        return false;
    }

    public boolean stopCellEditing()
    {
        return false;
    }

    public void cancelCellEditing()
    {

    }

    public void addCellEditorListener(CellEditorListener l)
    {

    }

    public void removeCellEditorListener(CellEditorListener l)
    {

    }
}
