package qsptools.translator.renderer;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import qsptools.translator.enums.ECharset;
import translator.enums.EEngine;
import translator.enums.ELanguage;

/**
 * Default renderer for enum presents in qsptools.translator.enums
 * @author pseudo555
 */
public class EnumComoboboxRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
        Component c = super.getListCellRendererComponent(jlist, o, i, bln, bln1);
        
        if( c instanceof JLabel){
            if(o instanceof ELanguage){
                ((JLabel)c).setText(((ELanguage) o).name());
            } else if (o instanceof ECharset){
                ((JLabel)c).setText(((ECharset) o).getName());
            } else if (o instanceof EEngine){
                 ((JLabel)c).setText(((EEngine) o).getName());
            }
        }
        
        return c;
    }
    
    
}
