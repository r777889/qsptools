package qsptools;

import java.io.File;

/**
 *
 * @author pseudo555
 */
public class QspToolsUtil {
    
    private QspToolsUtil() {}
            
    /*
     * Get the extension of a file.
     */  
    public static String getExt(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
