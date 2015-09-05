package translator.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pseudo555
 */
public enum ELanguage {
    ENGLISH("en", EIOLanguage.OUTPUT),
    FRENCH("fr", EIOLanguage.OUTPUT),
    GERMAN("de", EIOLanguage.OUTPUT),
    ITALIAN("it", EIOLanguage.OUTPUT),
    PORTUGUESE("p", EIOLanguage.OUTPUT),
    RUSSIAN("ru", EIOLanguage.INPUT),
    SPANISH("sp", EIOLanguage.OUTPUT),
    ;
    
    private final String code;
    private final EIOLanguage mode;
    
    private ELanguage(String code, EIOLanguage mode) {
        this.code = code;
        this.mode = mode;
    }
    
    public String getCode() {
        return this.code;
    }
    
    
    
    public static ELanguage[] getList(EIOLanguage wanted){
        List<ELanguage> res = new ArrayList<>();
        for(ELanguage el : values()){
            switch (wanted) {
                case INPUT:
                    // we want both or input only
                    if(EIOLanguage.OUTPUT != el.mode){
                        res.add(el);
                    }
                    break;
                case OUTPUT:
                    // we want both or output only
                    if(EIOLanguage.INPUT != el.mode){
                        res.add(el);
                    }
                    break;
                case BOTH:
                    if(wanted == el.mode){
                        res.add(el);
                    }
                    break;
                case NONE:
                default:
                    // do nothing
                    break;
            }
        }
        return res.toArray(new ELanguage[res.size()]);
    }
}
