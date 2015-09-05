package qsptools.translator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import hmi.log.QspLogger;
import qsptools.translator.bean.ProgressBean;
import qsptools.translator.bean.TranslatorConf;
import qsptools.translator.utils.TranslatorUtils;

/**
 *
 * @author pseudo555
 */
public class TranslateInProgressPanel extends javax.swing.JPanel {

    private TranslatorConf conf = null;
    private String translatedText;
    private Charset charsetToUse;
    
    private SwingWorker<String, ProgressBean> sw;
    
    /**
     * Creates new form TranslateInPrograssPanel
     * @param prevAction
     */
    public TranslateInProgressPanel(ActionListener prevAction) {
        initComponents();
        
        prevBtn.addActionListener(prevAction);
        // save translated file
        saveBtn.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = TranslatorUtils.getTxtFileChooser();
                if (fc.showSaveDialog(TranslateInProgressPanel.this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = fc.getSelectedFile();
                        Files.write(Paths.get(f.getAbsolutePath()), translatedText.getBytes( charsetToUse));
                    } catch(IOException e) {
                        QspLogger.error("Save failed: "+ e.getMessage());
                    }
                } else {
                    QspLogger.info("Open command cancelled by user.");
                }
            }
        });
        // save dico to file
        exportDico.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = TranslatorUtils.getDicoFileChooser();
                if( JFileChooser.APPROVE_OPTION == fc.showSaveDialog(TranslateInProgressPanel.this)){
                    if(!TranslatorUtils.persistDico(conf.customDico, fc.getSelectedFile())){
                        JOptionPane.showMessageDialog(TranslateInProgressPanel.this, "Save failed.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        // useUserInput retranslate missing part...
        useUserInput.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //block abuse
                useUserInput.setEnabled(false);
                
                String[] allMiss = missTrans.getText().split("\n");
                String[] allTrans = userFixTrans.getText().split("\n");
                
                if(allMiss.length != allTrans.length) {
                    JOptionPane.showMessageDialog(TranslateInProgressPanel.this, String.format("Missing translation and user input doesn't have the same number of lines: %s vs %s.\nPlease Fix this issue before retrying",allMiss.length, allTrans.length), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //add new trans to the dico
                for(int i = 0; i < allMiss.length; i++) {
                    final String tra = fixUserInput(allTrans[i]);
                    if(! conf.customDico.containsKey(allMiss[i])) {
                        conf.customDico.put(allMiss[i], tra);
                    } else {
                        QspLogger.error(">"+allMiss[i]+"< is already present in dictionnary");
                    }
                    translatedText = translatedText.replace(allMiss[i], tra);
                }
                
                missTrans.setText("");
                userFixTrans.setText("");
            }
        });
        
        textPB.setStringPainted(true);
        textPB.setString("0 %");
    }
    
    private String fixUserInput(final String input) {
        String res = input.replace('\'', '`');
        if(! res.trim().contains(" ")){
            res  = res.replace(" ", "_");
        }
        return res;
    }
    
    public void translateText(final TranslatorConf conf) {
        
        if(! conf.isMulti){
            if(conf.qspFile.isDirectory()) {
                QspLogger.error("expected file, got directory: "+conf.qspFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Expected file, got directory: "+conf.qspFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            innerTranslateText(conf);
        } else {
            for(String s : TranslatorUtils.fileList(conf.qspFile.toPath(), "rpy")){
                TranslatorConf conf2 = new TranslatorConf(conf, s);
                innerTranslateText(conf2);
            }
        }
    } 
        
    private void innerTranslateText(final TranslatorConf conf) {   
        charsetToUse = conf.encoding.getCharset();
        try {      
            List<String> allLines = Files.readAllLines(conf.qspFile.toPath(), charsetToUse);
            translateText(conf, allLines);
        } catch(IOException e) {
            if(e instanceof MalformedInputException){
                QspLogger.error("this txt files is not encoded in "+ conf.encoding.getName());
                JOptionPane.showMessageDialog(this, "Translate text impossible, encoding not supported.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                QspLogger.error("Translate text failed: "+ e.getMessage());
                JOptionPane.showMessageDialog(this, "Translate text impossible, something went terribly wrong.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void translateText(final TranslatorConf conf, final List<String> allLines) {
        this.conf = conf;
        sw = new SwingWorker<String, ProgressBean>() {

            @Override
            protected String doInBackground() throws Exception {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                final int nbLine = allLines.size();
                for(String txtOri : allLines){
                    String res = conf.translate(txtOri);
                    sb.append(res).append("\r\n");
                    i++;
                    setProgress( (int)((i * 100.0) / nbLine));
                    publish(new ProgressBean(res));
                    if(i % 100 == 0){
                        QspLogger.info("Read lines "+ i);
                    }
                }
                Thread.sleep(1l);
                return sb.toString();
            }

            @Override
            protected void process(List<ProgressBean> chunks) {
                if(conf.isTransIncomplete()) {
                    missTrans.append(conf.getAllMissTrans());
                }
            }

            @Override
            protected void done() {
                try { 
                    translatedText = get();
                    //trans done, set 100 %
                    QspLogger.info("Task finish.");
                    textPB.setValue(100);
                    //allow save & export
                    saveBtn.setEnabled(true);
                    exportDico.setEnabled(true);
                    // if there is some sentences to translate by hand, activate userInput part
                    if(conf.isTransIncomplete()) {
                        textPB.setString("Some sentences are not translated");
                        missTrans.append(conf.getAllMissTrans());
                        missTrans.setEnabled(true);
                        missTrans.setEditable(false);
                        userFixTrans.setEnabled(true);
                        useUserInput.setEnabled(true);
                    } else {
                        textPB.setString("100 %");
                    }
                    
                    //auto save mode activated
                    if(conf.silentMode){
                        try {
                            conf.qspFile.renameTo(new File(conf.qspFile.getAbsolutePath()+"ori"));
                            Files.write(conf.qspFile.toPath(), translatedText.getBytes( charsetToUse));
                        } catch(IOException e) {
                            QspLogger.error("Save failed for "+conf.qspFile+": "+ e.getMessage());
                        }
                    }
                } catch(InterruptedException | ExecutionException e) {
                    QspLogger.error("Translation thread error: "+ e.getMessage());
                } 
            }
        };
        sw.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                //On vérifie tout de même le nom de la propriété
                if("progress".equals(event.getPropertyName())){
                    //On récupère sa nouvelle valeur
                    textPB.setValue((Integer) event.getNewValue());
                    textPB.setString(event.getNewValue().toString()+ " %"+(conf.isTransIncomplete() ? " with errors" : ""));
                }
            }
        });
        sw.execute();
    }
    
    /**
     * Cancel the task.
     */
    void cancel() {
        conf = null;
        if(sw != null && ! sw.isCancelled() && ! sw.isDone()) {
            sw.cancel(true);
        } 
        saveBtn.setEnabled(false);
        exportDico.setEnabled(false);
    }
    
    /**
     * @return TranslatorConf used to do the translation
     */
    TranslatorConf getConf() {
        return conf;
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

        textLbl = new javax.swing.JLabel();
        textPB = new javax.swing.JProgressBar();
        saveBtn = new javax.swing.JButton();
        prevBtn = new javax.swing.JButton();
        exportDico = new javax.swing.JButton();
        sp1 = new javax.swing.JScrollPane();
        missTrans = new javax.swing.JTextArea();
        sp2 = new javax.swing.JScrollPane();
        userFixTrans = new javax.swing.JTextArea();
        useUserInput = new javax.swing.JButton();
        transMissLbl = new javax.swing.JLabel();
        userInputLbl = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        textLbl.setText("Text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(textLbl, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(textPB, gridBagConstraints);

        saveBtn.setText("Save");
        saveBtn.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(saveBtn, gridBagConstraints);

        prevBtn.setText("Previous");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(prevBtn, gridBagConstraints);

        exportDico.setText("Export dictionary");
        exportDico.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(exportDico, gridBagConstraints);

        missTrans.setColumns(20);
        missTrans.setRows(5);
        missTrans.setEnabled(false);
        sp1.setViewportView(missTrans);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sp1, gridBagConstraints);

        userFixTrans.setColumns(20);
        userFixTrans.setRows(5);
        userFixTrans.setEnabled(false);
        sp2.setViewportView(userFixTrans);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sp2, gridBagConstraints);

        useUserInput.setText("Use User input");
        useUserInput.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(useUserInput, gridBagConstraints);

        transMissLbl.setText("Translation missed:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(transMissLbl, gridBagConstraints);

        userInputLbl.setText("User translation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(userInputLbl, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exportDico;
    private javax.swing.JTextArea missTrans;
    private javax.swing.JButton prevBtn;
    private javax.swing.JButton saveBtn;
    private javax.swing.JScrollPane sp1;
    private javax.swing.JScrollPane sp2;
    private javax.swing.JLabel textLbl;
    private javax.swing.JProgressBar textPB;
    private javax.swing.JLabel transMissLbl;
    private javax.swing.JButton useUserInput;
    private javax.swing.JTextArea userFixTrans;
    private javax.swing.JLabel userInputLbl;
    // End of variables declaration//GEN-END:variables
}
