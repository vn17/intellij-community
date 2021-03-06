/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.application.options.colors.fileStatus;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class FileStatusColorsTable extends JBTable {

  public FileStatusColorsTable() {
    setShowGrid(false);
    getColumnModel().setColumnSelectionAllowed(false);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setDefaultRenderer(String.class, new MyStatusCellRenderer());
    setDefaultRenderer(Boolean.class, new MyDefaultStatusRenderer());
    setTableHeader(null);
    setRowHeight(JBUI.scale(22));
  }

  public void adjustColumnWidths() {
    for (int col = 0; col < getColumnCount(); col++) {
      DefaultTableColumnModel colModel = (DefaultTableColumnModel) getColumnModel();
      TableColumn column = colModel.getColumn(col);
      Class colClass = getColumnClass(col);
      int width = 0;
      int rightGap = 0;
      if (getColumnClass(col).equals(Boolean.class)) {
        width = JBUI.scale(10);
      }
      else {
        rightGap = isColorColumn(col) ? JBUI.size(10, 1).width : 0;
        TableCellRenderer renderer;
        for (int row = 0; row < getRowCount(); row++) {
          renderer = getCellRenderer(row, col);
          Component comp = renderer.getTableCellRendererComponent(this, getValueAt(row, col),
                                                                  false, false, row, col);
          width = Math.max(width, comp.getPreferredSize().width);
        }
      }
      width += rightGap;
      column.setPreferredWidth(width);
      if (colClass.equals(Color.class) || colClass.equals(Boolean.class)) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
      }
    }
  }

  private boolean isColorColumn(int col) {
    return getModel().getColumnClass(col).equals(Color.class);
  }

  private class MyStatusCellRenderer extends DefaultTableCellRenderer {

    private final JLabel myLabel = new JLabel();

    public MyStatusCellRenderer() {
      myLabel.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (value instanceof String) {
        FileStatusColorDescriptor descriptor = ((FileStatusColorsTableModel)getModel()).getDescriptorByName((String)value);
        if (descriptor != null) {
          myLabel.setText((String)value);
          myLabel.setForeground(isSelected ? UIUtil.getTableSelectionForeground() : descriptor.getColor());
          myLabel.setBackground(UIUtil.getTableBackground(isSelected));
          return myLabel;
        }
      }
      return c;
    }
  }

  private static class MyDefaultStatusRenderer extends DefaultTableCellRenderer {
    private final JLabel myLabel = new JLabel();
    private final Color myLabelColor;

    public MyDefaultStatusRenderer() {
      myLabel.setOpaque(true);
      myLabelColor = ColorUtil.withAlpha(myLabel.getForeground(), 0.5);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof Boolean) {
        myLabel.setForeground(isSelected ? UIUtil.getTableSelectionForeground() : myLabelColor);
        myLabel.setBackground(UIUtil.getTableBackground(isSelected));
        myLabel.setText((Boolean)value ? "" : "*");
        myLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return myLabel;
      }
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
  }
}
