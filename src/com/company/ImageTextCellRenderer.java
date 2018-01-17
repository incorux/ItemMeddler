package com.company;

import net.rithms.riot.api.endpoints.static_data.dto.Item;

import javax.swing.*;
import java.awt.*;

public class ImageTextCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        Item item = (Item) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        label.setIcon(new ImageIcon(this.getClass().getResource("/item/" + item.getImage().getFull().replaceAll("png", "jpg")) ));
        label.setText(item.getName());
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setToolTipText(Utils.itemStatsToTooltip(item.getStats()));
        return label;
    }
}
