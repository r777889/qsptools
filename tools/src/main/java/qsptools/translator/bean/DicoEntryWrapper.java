package qsptools.translator.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper around DioEntry
 * @author pseudo555
 */
@XmlRootElement(name = "dictionary")
@XmlAccessorType(XmlAccessType.FIELD)
public class DicoEntryWrapper {
    
    @XmlElement(name = "entry" )
    private ArrayList<DicoEntry> dico;
    
    /** Default constructor. */
    public DicoEntryWrapper() {
        //do nothing
    }

    /**
     * Create a DicoEntryWrapper using given map.
     * @param dicoMap map to be converted
     */
    public DicoEntryWrapper(Map<String, String> dicoMap) {
        dico = new ArrayList<>();
        if(dicoMap != null) {
            for(Map.Entry<String, String> line : dicoMap.entrySet()){
                String ori = line.getKey();
                if(!(ori.isEmpty() || ori.isEmpty())) {
                    dico.add(new DicoEntry(ori, line.getValue()));
                }
            }
        } 
    }
    
    /**
     * Create a DicoEntryWrapper using given list.
     * @param values values to be used
     */
    public DicoEntryWrapper(List<DicoEntry> values){
        this.dico = new ArrayList<>(values);
    }
    
    /**
     * @return Map version of DicoEntryWrapper content
     */
    public Map<String, String> getMap(){
        Map<String, String> res = new HashMap<>();
        for(DicoEntry de : dico){
            if(!(de.getOriginal().isEmpty() || de.getTranslated().isEmpty())) {
                res.put( de.getOriginal(), de.getTranslated());
            }
        }
        return res;
    }

    public ArrayList<DicoEntry> getDico() {
        return dico;
    }

    public void setDico(ArrayList<DicoEntry> dico) {
        this.dico = dico;
    }
}
