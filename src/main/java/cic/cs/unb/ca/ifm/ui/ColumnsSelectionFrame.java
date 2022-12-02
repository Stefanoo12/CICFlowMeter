package cic.cs.unb.ca.ifm.ui;

import cic.cs.unb.ca.jnetpcap.FlowFeature;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class ColumnsSelectionFrame extends JFrame implements ActionListener, ItemListener, ListSelectionListener {

    public void setSelectedColumns(List<Column> selectedColumns) {
        listModelVisible.clear();
        listModelHidden.clear();

        for (Column column : columns) {
            if (selectedColumns.contains(column)) {
                listModelVisible.addElement(column);
            } else {
                listModelHidden.addElement(column);
            }
        }
    }

    public static class Column {

        private final FlowFeature feature;
        private final String displayName;

        public FlowFeature getFeature() {
            return feature;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Column column = (Column) o;
            return feature == column.feature;
        }

        @Override
        public int hashCode() {
            return Objects.hash(feature);
        }

        @Override
        public String toString() {
            return displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Column(FlowFeature feature, String displayName) {
            this.feature = feature;
            this.displayName = displayName;
        }
    }

    //Border
    private Border borderCenter = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    private Border borderContents = BorderFactory.createEmptyBorder(0, 0, 10, 0);
    private Border borderList = BorderFactory.createLineBorder(Color.BLUE, 1);

    //Containers
    private Box boxButtons = Box.createVerticalBox();
    private Box boxListHidden = Box.createVerticalBox();
    private Box boxListVisible = Box.createVerticalBox();
    private Box boxListBorder = Box.createVerticalBox();
    private JPanel contents;
    private JPanel panelCenter;
    private JPanel panelSouth;

    //Components
    private JButton btnAdd;
    private JButton btnAddAll;
    private JButton btnRemove;
    private JButton btnRemoveAll;
    private JButton btnSave;

    private JLabel lblListHidden;
    private JLabel lblListVisible;
    private JLabel lblSelectedHidden;
    private JLabel lblSelectedHiddenLabel;
    private JLabel lblSelectedVisible;
    private JLabel lblSelectedVisibleLabel;

    private JList<Column> listHidden;
    private JList<Column> listVisible;

    //Fonts
    private final Font fontBold = new Font(Font.DIALOG, Font.BOLD, 14);
    private final Font fontPlain = new Font(Font.DIALOG, Font.BOLD, 14);

    private Column[] columns;

    private DefaultListModel<Column> listModelHidden = new DefaultListModel<>();
    private DefaultListModel<Column> listModelVisible = new DefaultListModel<>();

    public void setSaveActionListener(Function<List<Column>, Void> saveActionListener) {
        this.saveActionListener = Optional.of(saveActionListener);
    }

    private Optional<Function<List<Column>, Void>> saveActionListener;

    public ColumnsSelectionFrame() {
        super("Output Columns");
        setFonts();

        columns = new Column[FlowFeature.values().length];

        for (int i = 0; i < FlowFeature.values().length; i++) {
            FlowFeature feature = FlowFeature.values()[i];
            columns[i] = new Column(feature, feature.getName());
        }

        //A JPanel is used for the main container
        contents = new JPanel();
        contents.setBorder(borderContents);
        contents.setLayout(new BorderLayout());
        //setSpecificSize(contents, new Dimension(700, 600));
        setContentPane(contents);

        //Create and add components

        //North region
        JLabel lblTitle = new JLabel("Columns Settings", SwingConstants.CENTER);
        lblTitle.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
        contents.add(lblTitle, BorderLayout.NORTH);

        //Center region
        panelCenter = new JPanel();
        panelCenter.setBorder(borderCenter);

        //Hidden list
        //To left-align components in a vertical box, they must each be given left alignment
        lblListHidden = new JLabel("Columns");
        lblListHidden.setAlignmentX(LEFT_ALIGNMENT);

        initHiddenModel();
        listHidden = new JList<>(listModelHidden);
        listHidden.setAlignmentX(LEFT_ALIGNMENT);
        listHidden.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listHidden.setBorder(borderList);

        JScrollPane scrollListHidden = new JScrollPane(listHidden);
        scrollListHidden.setAlignmentX(LEFT_ALIGNMENT);
        scrollListHidden.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setSpecificSize(scrollListHidden, new Dimension(200, 300));

        boxListHidden.add(lblListHidden);
        boxListHidden.add(scrollListHidden);
        panelCenter.add(boxListHidden);

        //Spacer
        panelCenter.add(Box.createRigidArea(new Dimension(10, 1)));

        //Add/Remove buttons
        btnAdd = new JButton("Add >>");
        btnAddAll = new JButton("Add All >>");
        btnRemove = new JButton("<< Remove");
        btnRemoveAll = new JButton("<< Remove All");

        Dimension dimRemoveAll = btnRemoveAll.getPreferredSize();
        setSpecificSize(btnAdd, dimRemoveAll);
        setSpecificSize(btnAddAll, dimRemoveAll);
        setSpecificSize(btnRemove, dimRemoveAll);

        boxButtons.add(btnAdd);
        boxButtons.add(Box.createRigidArea(new Dimension(1, 5)));
        boxButtons.add(btnAddAll);
        boxButtons.add(Box.createRigidArea(new Dimension(1, 20)));
        boxButtons.add(btnRemove);
        boxButtons.add(Box.createRigidArea(new Dimension(1, 5)));
        boxButtons.add(btnRemoveAll);
        boxButtons.add(Box.createRigidArea(new Dimension(1, 20)));
        panelCenter.add(boxButtons);

        //Spacer
        panelCenter.add(Box.createRigidArea(new Dimension(10, 1)));

        //Visible List
        lblListVisible = new JLabel("Selected Columns");
        lblListVisible.setAlignmentX(LEFT_ALIGNMENT);

        listVisible = new JList<>(listModelVisible);
        listVisible.setAlignmentX(LEFT_ALIGNMENT);
        listVisible.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listVisible.setBorder(borderList);

        JScrollPane scrollListVisible = new JScrollPane(listVisible);
        scrollListVisible.setAlignmentX(LEFT_ALIGNMENT);
        scrollListVisible.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setSpecificSize(scrollListVisible, new Dimension(200, 300));

        boxListVisible.add(lblListVisible);
        boxListVisible.add(scrollListVisible);
        panelCenter.add(boxListVisible);

        //Spacer
        panelCenter.add(Box.createRigidArea(new Dimension(10, 1)));

        contents.add(panelCenter, BorderLayout.CENTER);

        //South region
        panelSouth = new JPanel();
        lblSelectedHiddenLabel = new JLabel("Selected hidden:");
        lblSelectedHidden = new JLabel();
        lblSelectedVisibleLabel = new JLabel("Selected visible:");
        lblSelectedVisible = new JLabel();
        btnSave = new JButton("Save");

        panelSouth.add(btnSave);
        panelSouth.add(Box.createRigidArea(new Dimension(1, 5)));
        contents.add(panelSouth, BorderLayout.SOUTH);

        //Register event handlers
        btnAdd.addActionListener(this);
        btnAddAll.addActionListener(this);
        btnRemove.addActionListener(this);
        btnRemoveAll.addActionListener(this);
        btnSave.addActionListener(this);
        listHidden.addListSelectionListener(this);
        listVisible.addListSelectionListener(this);

        setSize(850, 460);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    //Button handler
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnAdd) {
            addItem();
            return;
        }
        if (source == btnAddAll) {
            addAllItems();
            return;
        }
        if (source == btnRemove) {
            removeItem();
            return;
        }
        if (source == btnRemoveAll) {
            removeAllItems();
            return;
        }
        if (source == btnSave) {

            saveActionListener.ifPresent(listener -> listener.apply(getSelectedColumns()));
        }
    }

    private void addItem() {
        int iSelected = listHidden.getSelectedIndex();
        if (iSelected == -1) {
            return;
        }

        Column addedItem = listHidden.getSelectedValue();

        //Remove from left list
        listModelHidden.remove(iSelected);
        displaySelectedItems();

        listModelVisible.addElement(addedItem);
    }

    private void addAllItems() {
        listModelVisible.clear();
        for (Column s : columns) {
            listModelVisible.addElement(s);
        }
        listModelHidden.clear();
    }

    /*private void changeBorder(){

    }*/

    private void displaySelectedItems() {
        int iSelected;
        Column itemName;

        iSelected = listHidden.getSelectedIndex();
        if (iSelected == -1) {
            lblSelectedHidden.setText("");
        } else {
            itemName = listHidden.getSelectedValue();
            lblSelectedHidden.setText(itemName.getDisplayName());
        }

        iSelected = listVisible.getSelectedIndex();
        if (iSelected == -1) {
            lblSelectedVisible.setText("");
        } else {
            itemName = listVisible.getSelectedValue();
            lblSelectedVisible.setText(itemName.getDisplayName());
        }
    }

    /*private void initBorderModel(){
        for(String s : border){
            listModelBorder.addElement(s);
        }
    }*/

    private void initHiddenModel() {
        for (Column s : columns) {
            listModelHidden.addElement(s);
        }
    }

    public void itemStateChanged(ItemEvent e) {

    }

    private void removeItem() {
        int iSelected = listVisible.getSelectedIndex();
        if (iSelected == -1) {
            return;
        }

        Column removedItem = listVisible.getSelectedValue();

        //Remove from right list
        listModelVisible.remove(iSelected);
        displaySelectedItems();

        //Add to left list
        listModelHidden.addElement(removedItem);
    }

    private void removeAllItems() {
        listModelHidden.clear();
        initHiddenModel();
        listModelVisible.clear();
        displaySelectedItems();
    }

    private void setFonts() {
        UIManager.put("Button.font", fontBold);
        UIManager.put("ComboBox.font", fontBold);
        UIManager.put("Label.font", fontBold);
        UIManager.put("List.font", fontPlain);
    }

    private void setSpecificSize(JComponent component, Dimension dimension) {
        component.setMinimumSize(dimension);
        component.setPreferredSize(dimension);
        component.setMaximumSize(dimension);
    }

    public void valueChanged(ListSelectionEvent e) {
        Object source = e.getSource();
        if (source == listHidden) {
            displaySelectedItems();
            return;
        }
        if (source == listVisible) {
            displaySelectedItems();
            return;
        }
    }

    public List<Column> getSelectedColumns() {
        List<Column> selectedColumns = new ArrayList<>();
        for (int i = 0; i < listVisible.getModel().getSize(); i++) {
            selectedColumns.add(listVisible.getModel().getElementAt(i));
        }
        return selectedColumns;
    }
}
