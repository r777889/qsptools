package qsptools.password;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import hmi.log.QspLogger;
import qsptools.password.utils.PasswordRetriver;

/**
 *
 * @author pseudo555
 */
public class InputFilePanel extends javax.swing.JPanel {
    
    private final JFileChooser fc;
    
    private File lastSelectedFile;
    
    /**
     * Creates new form InputFile
     * @param actionOnRun
     */
    public InputFilePanel(ActionListener actionOnRun) {
        fc = PasswordRetriver.getFileChooser();
        initComponents();
        selectFileBtn.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                //Handle open button action.
                if (fc.showOpenDialog(InputFilePanel.this) == JFileChooser.APPROVE_OPTION) {
                    selectFile(fc.getSelectedFile());
                    QspLogger.info("Opening: "+ lastSelectedFile.getName());
                } else {
                    selectFile(null);
                    QspLogger.info("Open command cancelled by user.");
                }
            }
        });
        nextBtn.addActionListener( actionOnRun);
    }
    
    private void selectFile(File file){
        if(file == null) {
            lastSelectedFile = null;
            nextBtn.setEnabled(false);
            filenameLabel.setText("Please, select a file");
        } else {
            lastSelectedFile = fc.getSelectedFile();
            nextBtn.setEnabled(true);
            filenameLabel.setText(lastSelectedFile.getAbsolutePath());
        }
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
        fileLabel = new javax.swing.JLabel();
        selectFileBtn = new javax.swing.JButton();
        filenameLabel = new javax.swing.JLabel();
        nextBtn = new javax.swing.JButton();
        empty = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        explanation.setText("Select the file you want to retrive the password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(explanation, gridBagConstraints);

        fileLabel.setText("File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fileLabel, gridBagConstraints);

        selectFileBtn.setText("Select file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(selectFileBtn, gridBagConstraints);

        filenameLabel.setText("Please, select a file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(filenameLabel, gridBagConstraints);

        nextBtn.setText("Next");
        nextBtn.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(nextBtn, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(empty, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel empty;
    private javax.swing.JLabel explanation;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JLabel filenameLabel;
    private javax.swing.JButton nextBtn;
    private javax.swing.JButton selectFileBtn;
    // End of variables declaration//GEN-END:variables
    
    void reset() {
        selectFile(null);
    }
    
    File getFile(){
        return lastSelectedFile; 
    }
}