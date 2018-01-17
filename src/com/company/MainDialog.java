package com.company;

import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class MainDialog extends JDialog {
    private CurrentStats _currentStats;
    private int level;
    private JPanel contentPane;
    private JButton buttonExit;
    private JTextArea outputAreaLeft;
    private JComboBox platformCombo;
    private JLabel platformLabel;
    private JComboBox championCombo;
    private JLabel championLabel;
    private JPanel StatisticsPanel;
    private JPanel levelPanel;
    private JSpinner spinner1;
    private JLabel levelLabel;
    private JPanel itemsPanel;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JList itemList;
    private Engine engine;
    public java.util.List<Item> selectedItems;

    private MainDialog() {
        engine = new Engine();
        setContentPane(contentPane);
        setModal(true);
        spinner1.setModel(new SpinnerNumberModel(1, 1, 18, 1));
        getRootPane().setDefaultButton(buttonExit);
        addData();
        addListeners();
        engine.setCurrentChampionFromName((String) championCombo.getSelectedItem());
        onChampionChange();
        renderChampionStats();
    }

    private void addData() {
        selectedItems = new ArrayList<>();
        for(String platformName : engine.platformNamesList)
            platformCombo.addItem(platformName);
        platformCombo.setSelectedItem("NA");
        engine.championsMap.forEach((integer, champion) -> {
            championCombo.addItem(champion.getName());
        });
        DefaultListModel defaultListModel = new DefaultListModel();
        Engine.itemList.forEach((string, item) -> {
            defaultListModel.addElement(string);
        });
    }

    private void addListeners(){
        onPlatformChange();
        buttonExit.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        platformCombo.addItemListener(arg0 -> {
            engine.setCurrentPlatform(arg0.getItem().toString());
            onPlatformChange();
        });
        championCombo.addItemListener(arg0 -> {
            engine.setCurrentChampionFromName((String) arg0.getItem());
            onChampionChange();
        });
        spinner1.addChangeListener(changeEvent -> {
            level = (int) spinner1.getValue();
            renderChampionStats();
        });
        addItemButton.addActionListener(actionEvent -> {
            AddItemDialog addItemDialog = new AddItemDialog(this);
            addItemDialog.pack();
            addItemDialog.setPreferredSize(new Dimension(100, 100));
            addItemDialog.setVisible(true);
            onItemsChange();
        });
        removeItemButton.addActionListener(actionEvent -> {
            if(itemList.getSelectedValue() != null) {
                selectedItems.remove(itemList.getSelectedIndex());
                onItemsChange();
            }
        });
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onPlatformChange(){
        platformLabel.setText(engine.getCurrentPlatform().getName().toUpperCase());
        platformLabel.updateUI();
    }

    private void onChampionChange(){
        championLabel.setText(engine.getCurrentChampion().getName());
        championLabel.updateUI();
        renderChampionStats();
    }

    private void renderChampionStats(){
        Champion currentChampion = Engine.getCurrentChampion();
        _currentStats = new CurrentStats(currentChampion, level, selectedItems);
        StatisticsPanel.removeAll();
        StatisticsPanel.revalidate();
        StatisticsPanel.revalidate();
        StatisticsPanel.repaint();
        StatisticsPanel.setLayout(new GridLayout(0,1));
        for(ChampionStat param: ChampionStat.values()){
            JPanel aPanel= new JPanel();
            aPanel.setLayout(new FlowLayout());
            JLabel jLabelName = new JLabel();
            jLabelName.setText(param.toString().replaceAll("_", " "));
            JLabel jLabelVal = new JLabel();
            jLabelVal.setText(String.valueOf(_currentStats.get(param).intValue()));
            aPanel.add(jLabelName);
            aPanel.add(jLabelVal);
            StatisticsPanel.add(aPanel);
        }
    }

    private void onItemsChange(){
        DefaultListModel<Object> listModel = new DefaultListModel<>();
        selectedItems.forEach(item -> listModel.addElement(item.getName()));
        itemList.setModel(listModel);
        itemList.updateUI();
        renderChampionStats();
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
