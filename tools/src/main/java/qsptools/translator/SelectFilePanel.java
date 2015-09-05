package qsptools.translator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import hmi.log.QspLogger;
import qsptools.translator.bean.DicoEntryWrapper;
import qsptools.translator.bean.TranslatorConf;
import qsptools.translator.enums.ECharset;
import translator.enums.EEngine;
import qsptools.translator.model.DicoTableModel;
import translator.enums.ELanguage;
import qsptools.translator.model.QspGenericComboboxModel;
import qsptools.translator.renderer.EnumComoboboxRenderer;
import qsptools.translator.utils.TranslatorUtils;
import translator.enums.EIOLanguage;

/**
 *
 * @author pseudo555
 */
public class SelectFilePanel extends javax.swing.JPanel {

    private final DicoTableModel model;
    
    private File lastSelectedFile= null;
    
    /**
     * Creates new form InputFilePanel
     * @param nextAction
     * @param testAction
     */
    public SelectFilePanel(ActionListener nextAction, ActionListener testAction) {
        
        initComponents();
        // engine selection
        engineSelection.setModel( new QspGenericComboboxModel<>(EEngine.values()));
        engineSelection.setRenderer(new EnumComoboboxRenderer());
        engineSelection.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                EEngine selected = getEngine();
                if(selected.isRequiredAppId()){
                    if(selected.getAppId() == null || selected.getAppId().isEmpty()){
                        String resp = JOptionPane.showInputDialog(SelectFilePanel.this, "What is your appId for service "+selected.getName(), "App ID required !", JOptionPane.QUESTION_MESSAGE);
                        selected.setAppId(resp);
                    }
                }
                if (! selected.isIsAllDst()) {
                    selectedToLg.setSelectedItem(ELanguage.ENGLISH);
                }
                selectedToLg.setEnabled(selected.isIsAllDst());
            }
        });
        engineSelection.setSelectedIndex(0);
        // encoding combobox
        encodingChoice.setModel(new QspGenericComboboxModel<>(ECharset.values()));
        encodingChoice.setRenderer(new EnumComoboboxRenderer());
        encodingChoice.setSelectedItem(ECharset.UTF8);
        // from combobox
        selectedFromLg.setModel( new QspGenericComboboxModel<>(ELanguage.getList(EIOLanguage.INPUT)));
        selectedFromLg.setRenderer(new EnumComoboboxRenderer());
        selectedFromLg.setSelectedItem( ELanguage.RUSSIAN);
        selectedFromLg.setEnabled(false);
        selectedFromLg.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                updateNextBtn();
            }
        });
        // to combobox
        selectedToLg.setModel( new QspGenericComboboxModel<>(ELanguage.getList(EIOLanguage.OUTPUT)));
        selectedToLg.setRenderer(new EnumComoboboxRenderer());
        selectedToLg.setSelectedItem( ELanguage.ENGLISH);
        selectedToLg.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                updateNextBtn();
            }
        });
        
        // open btn
        openBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = TranslatorUtils.getTxtFileChooser();
                if (fc.showOpenDialog(SelectFilePanel.this) == JFileChooser.APPROVE_OPTION) {
                    selectFile(fc.getSelectedFile());
                    QspLogger.info("Opening file: "+ lastSelectedFile.getName());
                } else {
                    selectFile(null);
                    QspLogger.info("Open command cancelled by user.");
                }
            }
        });
        
        // table
        model = new DicoTableModel();
        customDicoTable.setModel(model);
        customDicoTable.setShowGrid(true);
        
        // row selection management in table
        customDicoTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting()){
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel)lse.getSource();
                deleteLineInDico.setEnabled(! lsm.isSelectionEmpty());
            }
        });
        
        // customEntries
        customDico.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                final boolean isEnable = customDico.isSelected();
                clearDico.setEnabled(isEnable);
                addLineInDico.setEnabled(isEnable);
                loadDico.setEnabled(isEnable);
                saveDico.setEnabled(isEnable);
            }
        });
        // reset dico
        clearDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.clear(true);
            }
        });
        //add line button
        addLineInDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                model.addNewItem();
            }
        });
        // delete line button
        deleteLineInDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                int[] selectedRows = customDicoTable.getSelectedRows();
//                System.out.println(selectedRows.length);
                for(int idx = selectedRows.length -1; idx >= 0 ; idx--){
                    int modelIdx = customDicoTable.convertRowIndexToModel(selectedRows[idx]);
//                    System.out.println(selectedRows[idx]+ " -> " +modelIdx);
                    model.removeRow(modelIdx);
                }
            }
        });
        // load dico from file
        loadDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.clear(true);
                JFileChooser fc = TranslatorUtils.getDicoFileChooser();
                if( JFileChooser.APPROVE_OPTION == fc.showOpenDialog(SelectFilePanel.this)){
                    if(!TranslatorUtils.fillDico(model, fc.getSelectedFile())){
                        JOptionPane.showMessageDialog(SelectFilePanel.this, "Import failed.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        // save dico to file
        saveDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = TranslatorUtils.getDicoFileChooser();
                if( JFileChooser.APPROVE_OPTION == fc.showSaveDialog(SelectFilePanel.this)){
                    if(!TranslatorUtils.persistDico(model.getCustomDico(), fc.getSelectedFile())){
                        JOptionPane.showMessageDialog(SelectFilePanel.this, "export failed.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
                
        // nextBtn
        nextBtn.addActionListener( nextAction);
        
        // testButton
        testButton.addActionListener(testAction);
    }
    
    /**
     * Update part of the hmi according to selected file
     * @param file Selected file
     */
    private void selectFile(File file){
        if(file == null) {
            lastSelectedFile = null;
            filenameLabel.setText("Please, select a file");
        } else {
            lastSelectedFile = file;
            filenameLabel.setText(lastSelectedFile.getAbsolutePath());
        }
        updateNextBtn();
    }
    
    /**
     * Reset the HMI, set the given conf.
     * @param conf TranslatorConf to be used
     */
    void reset(TranslatorConf conf) {
        engineSelection.setSelectedItem( conf == null || conf.engine == null ? EEngine.GOOGLE_TRANSLATION : conf.engine);
        selectedFromLg.setSelectedItem( ELanguage.RUSSIAN );
        selectedToLg.setSelectedItem( conf == null || conf.to == null ? ELanguage.ENGLISH : conf.to);
        model.addItems(conf == null ? null : new DicoEntryWrapper(conf.customDico).getDico());
        selectFile(null);
    }
    
    /**
     * @return TranslatorConf to be used
     */
    TranslatorConf getConf() {
        TranslatorConf conf = new TranslatorConf();
        
        conf.qspFile = lastSelectedFile; 
        conf.engine = getEngine();
        conf.from = getFromLg();
        conf.to = getToLg();
        conf.encoding = getEncoding();
        conf.customDico = (customDico.isSelected() ? model.getCustomDico() : new HashMap<String, String>());
        conf.isMulti = false;
        
        return conf;
    }
    
    private EEngine getEngine() {
        return (EEngine) engineSelection.getSelectedItem();
    }
    
    private ECharset getEncoding() {
        return (ECharset) encodingChoice.getSelectedItem();
    }
    
    /**
     * @return Source language
     */
    private ELanguage getFromLg() {
        return null; //(ELanguage) selectedFromLg.getSelectedItem();
    }
    
    /**
     * @return Destination language
     */
    private ELanguage getToLg() {
        return (ELanguage) selectedToLg.getSelectedItem();
    }
    
    /**
     * Update NextButton enable state according to how the hmi is filled.
     */
    private void updateNextBtn() {
        boolean base = ! ( getToLg() == null || lastSelectedFile == null);
        nextBtn.setEnabled(base );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        explanation = new javax.swing.JLabel();
        selectfileLbl = new javax.swing.JLabel();
        filenameLabel = new javax.swing.JLabel();
        openBtn = new javax.swing.JButton();
        fromLg = new javax.swing.JLabel();
        selectedFromLg = new javax.swing.JComboBox();
        toLg = new javax.swing.JLabel();
        selectedToLg = new javax.swing.JComboBox();
        nextBtn = new javax.swing.JButton();
        customDico = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        customDicoTable = new javax.swing.JTable();
        addLineInDico = new javax.swing.JButton();
        testButton = new javax.swing.JButton();
        saveDico = new javax.swing.JButton();
        loadDico = new javax.swing.JButton();
        clearDico = new javax.swing.JButton();
        deleteLineInDico = new javax.swing.JButton();
        spacer1 = new javax.swing.JLabel();
        spacer2 = new javax.swing.JLabel();
        encodingChoice = new javax.swing.JComboBox();
        engineLbl = new javax.swing.JLabel();
        engineSelection = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        explanation.setText("This will allow to translate a QSP file in another language.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(explanation, gridBagConstraints);

        selectfileLbl.setText("File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectfileLbl, gridBagConstraints);

        filenameLabel.setText("Please, select a TXT2GAM file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(filenameLabel, gridBagConstraints);

        openBtn.setText("Open");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(openBtn, gridBagConstraints);

        fromLg.setText("From");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fromLg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedFromLg, gridBagConstraints);

        toLg.setText("To");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(toLg, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectedToLg, gridBagConstraints);

        nextBtn.setText("Next");
        nextBtn.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(nextBtn, gridBagConstraints);

        customDico.setSelected(true);
        customDico.setText("Use custom dictionnary entries");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(customDico, gridBagConstraints);

        customDicoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(customDicoTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane1, gridBagConstraints);

        addLineInDico.setText("Add line");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(addLineInDico, gridBagConstraints);

        testButton.setText("Test parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(testButton, gridBagConstraints);

        saveDico.setText("Save");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(saveDico, gridBagConstraints);

        loadDico.setText("Load");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(loadDico, gridBagConstraints);

        clearDico.setText("Reset");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(clearDico, gridBagConstraints);

        deleteLineInDico.setText("Del. line");
        deleteLineInDico.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(deleteLineInDico, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(spacer1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(spacer2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(encodingChoice, gridBagConstraints);

        engineLbl.setText("Service");
        engineLbl.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(engineLbl, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(engineSelection, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLineInDico;
    private javax.swing.JButton clearDico;
    private javax.swing.JCheckBox customDico;
    private javax.swing.JTable customDicoTable;
    private javax.swing.JButton deleteLineInDico;
    private javax.swing.JComboBox encodingChoice;
    private javax.swing.JLabel engineLbl;
    private javax.swing.JComboBox engineSelection;
    private javax.swing.JLabel explanation;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JLabel fromLg;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadDico;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton openBtn;
    private javax.swing.JButton saveDico;
    private javax.swing.JComboBox selectedFromLg;
    private javax.swing.JComboBox selectedToLg;
    private javax.swing.JLabel selectfileLbl;
    private javax.swing.JLabel spacer1;
    private javax.swing.JLabel spacer2;
    private javax.swing.JButton testButton;
    private javax.swing.JLabel toLg;
    // End of variables declaration//GEN-END:variables

}
