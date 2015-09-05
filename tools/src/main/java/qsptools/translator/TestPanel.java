package qsptools.translator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import qsptools.translator.bean.TranslatorConf;

/**
 *
 * @author pseudo555
 */
public class TestPanel extends javax.swing.JPanel {
    
    private SwingWorker<String, Integer> sw;
    private TranslatorConf conf;
    
    /**
     * Creates new form TestPanel
     * @param prevAction
     */
    public TestPanel( ActionListener prevAction) {
        initComponents();
        
        // prev action
        prevButton.addActionListener(prevAction);
        
        // translate action
        transButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                translate();
            }
        });
        
        // clear button
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                input.setText("");
                output.setText("");
            }
        });
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

        scrolInput = new javax.swing.JScrollPane();
        input = new javax.swing.JTextArea();
        scrollOutput = new javax.swing.JScrollPane();
        output = new javax.swing.JTextArea();
        prevButton = new javax.swing.JButton();
        oriLbl = new javax.swing.JLabel();
        transLbl = new javax.swing.JLabel();
        expanation = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        transButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        input.setColumns(20);
        input.setRows(5);
        scrolInput.setViewportView(input);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrolInput, gridBagConstraints);

        output.setColumns(20);
        output.setRows(5);
        scrollOutput.setViewportView(output);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrollOutput, gridBagConstraints);

        prevButton.setText("Previous");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(prevButton, gridBagConstraints);

        oriLbl.setText("Original text:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(oriLbl, gridBagConstraints);

        transLbl.setText("Translated text:");
        transLbl.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(transLbl, gridBagConstraints);

        expanation.setText("<html>This panel allow you to check the translation function using typed text directly.<br /><b>Warning:</b> pictures names will be translated anyway.</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(expanation, gridBagConstraints);

        transButton.setText("Translate");
        jPanel1.add(transButton);

        clearButton.setText("Clear");
        jPanel1.add(clearButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel expanation;
    private javax.swing.JTextArea input;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel oriLbl;
    private javax.swing.JTextArea output;
    private javax.swing.JButton prevButton;
    private javax.swing.JScrollPane scrolInput;
    private javax.swing.JScrollPane scrollOutput;
    private javax.swing.JButton transButton;
    private javax.swing.JLabel transLbl;
    // End of variables declaration//GEN-END:variables

    void cancel() {
        if(sw != null && ! sw.isCancelled() && ! sw.isDone()) {
            sw.cancel(true);
        } 
    }

    private void translate() {
        final String[] allLines = input.getText().split("\r\n");
        sw = new SwingWorker<String, Integer>() {

            @Override
            protected String doInBackground() throws Exception {
                StringBuilder sb = new StringBuilder();
                for(String txtOri : allLines){                    
                    String fix = conf.translate(new String( txtOri.getBytes("UTF-8")));
                    sb.append(fix).append("\r\n");     
                }
                Thread.sleep(1l);
                return sb.toString();
            }

            @Override
            protected void done() {
                try { 
                    output.setText(get());
                } catch(InterruptedException | ExecutionException e) {
                    output.setText("Translation failed: "+ e.getMessage());                    
                }
            }
        };
        sw.execute();
    }

    void setConf(TranslatorConf conf) {
        this.conf = conf;
    }
}
