package ui;

import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

/** Default cell renderer with left/right padding and number formatting. */
public class PaddedRenderer extends DefaultTableCellRenderer {
    public PaddedRenderer() {
        setBorder(new EmptyBorder(0, 12, 0, 12));
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Double d) {
            setText(String.format("%,.2f", d));
        } else {
            super.setValue(value);
        }
        setBorder(new EmptyBorder(0, 12, 0, 12));
    }
}
