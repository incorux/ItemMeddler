package com.company;

import net.rithms.riot.api.endpoints.static_data.dto.Item;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class AddItemDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList addItemList;
    private JTextField filterTextField;
    private MainDialog myOwner;
    private List<Item> allItemsSorted;

    AddItemDialog(MainDialog owner) {
        Objects.requireNonNull(owner);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        myOwner = owner;
        setListeners();
        setData();
    }

    private void setData() {
        setAndFilterItemList();
    }

    private void setAndFilterItemList() {
        allItemsSorted = new ArrayList<>(Engine.itemList.values());
        allItemsSorted.sort(Comparator.comparing(Item::getName));
        String filterText = filterTextField.getText();
        List<Item> allItemsSortedFiltered = new LinkedList<>(this.allItemsSorted);
        if(!filterText.isEmpty()){
            allItemsSortedFiltered.removeIf(s -> !s.getName().startsWith(filterText));
        }
        addItemList.setCellRenderer(new ImageTextCellRenderer());
        addItemList.setListData(allItemsSortedFiltered.toArray());
    }

    private void setListeners() {
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        filterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                setAndFilterItemList();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                setAndFilterItemList();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                setAndFilterItemList();
            }
        });
    }

    private void onOK() {
        myOwner.selectedItems.add((Item) addItemList.getSelectedValue());
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
