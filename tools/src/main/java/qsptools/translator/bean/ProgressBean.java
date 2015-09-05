package qsptools.translator.bean;

/**
 * Bean use to display progress in SwingWorker that do the translation
 */
public class ProgressBean {
    
    private final String txt;

    public ProgressBean(final String tx) {
        txt = tx;
    }

    public String getTxt() {
        return txt;
    }
}
