package qsptools.translator.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Ditionary
 * @author pseudo555
 */
@XmlRootElement(name = "entry")
@XmlType(propOrder = { "original", "translated" })
@XmlAccessorType(XmlAccessType.PROPERTY) 
public class DicoEntry {
    
    /** Original sentence. */
    private String original;
    /** Trnaslated sentences. */
    private String translated;

    /** Default constructor. */
    public DicoEntry() {
        this(null, null);
    }
    
    /** 
     * Constructor.
     * @param ori Originale sentence to set
     * @param trans Translated sentence to set
     */
    public DicoEntry(String ori, String trans) {
        original = ori;
        translated = trans;
    }

    @XmlAttribute(name = "original")
    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    @XmlAttribute(name = "translated")
    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }

    @Override
    public String toString() {
        return "DicoEntry{" + "original=" + original + ", translated=" + translated + '}';
    }
}
