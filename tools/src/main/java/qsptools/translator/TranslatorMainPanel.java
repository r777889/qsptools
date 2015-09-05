package qsptools.translator;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author pseudo555
 */
public class TranslatorMainPanel extends javax.swing.JPanel {
    
    private static final String INPUT = "input";
    private static final String MULTI = "multi";
    private static final String TEST = "test";
    private static final String RESULT = "result";
    private boolean isMulti = false;

    private final SelectFilePanel inputPanel;
    private final SelectMultipleFilePanel multiple;
    private final TestPanel testPanel;
    private final TranslateInProgressPanel resultPanel;
    private final CardLayout cardLayout = new CardLayout();
    /**
     * Creates new form PasswordMainPanel
     */
    public TranslatorMainPanel() {
        
        testPanel = new TestPanel(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                testPanel.cancel();
                cardLayout.show(TranslatorMainPanel.this, INPUT);
            }
        });
        multiple = new SelectMultipleFilePanel(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cardLayout.show(TranslatorMainPanel.this, RESULT);
                resultPanel.translateText(multiple.getConf());
            }
        },new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cardLayout.show(TranslatorMainPanel.this, TEST);
                testPanel.setConf(multiple.getConf());
            }
        });
        inputPanel = new SelectFilePanel(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cardLayout.show(TranslatorMainPanel.this, RESULT);
                resultPanel.translateText(inputPanel.getConf());
            }
        },new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cardLayout.show(TranslatorMainPanel.this, TEST);
                testPanel.setConf(inputPanel.getConf());
            }
        });
        resultPanel = new TranslateInProgressPanel(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                inputPanel.reset(resultPanel.getConf());
                multiple.reset(resultPanel.getConf());
                resultPanel.cancel();
                cardLayout.show(TranslatorMainPanel.this, INPUT);
            }
        });
        
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(cardLayout);
        add(resultPanel, RESULT);
        add(testPanel, TEST);
        add(inputPanel, INPUT);
        add(multiple, MULTI);
        
        cardLayout.show(this, INPUT);
    }

    public void setMode(boolean equalsIgnoreCase) {
        if(equalsIgnoreCase){
            cardLayout.show(this, MULTI);
        }
    }
}