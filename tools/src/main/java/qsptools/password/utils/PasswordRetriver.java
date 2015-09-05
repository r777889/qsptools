package qsptools.password.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import qsptools.QspToolsUtil;
import hmi.log.QspLogger;

/**
 *
 * @author pseudo555
 */
public final class PasswordRetriver {
    
    private PasswordRetriver() {}
    
    /* password management ---------------------------------------------------*/
    
    public static String getPasswordFromFile(File f){
        try {            
            long filesize = f.length();
            byte data[] = new byte[(int) filesize];
            
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            in.readFully(data);
            in.close();
            
            StringBuilder sb = new StringBuilder();
            int i = 0x2E;
            boolean doItAgain = true;
            while (doItAgain) {
                byte[] bytes = new byte[2];
                bytes[0] = data[i];
                bytes[1]  = data[i+1];
                QspLogger.error(String.format("@ %s, Read: %s %s",Integer.toHexString(i), Integer.toHexString(bytes[0]), Integer.toHexString(bytes[1])));
                if(bytes[0] == 0x0d && bytes[1] == 0x00){
                    if(data[i+2]== 0x0a && data[i+3] == 0x00) {
                        return sb.toString();
                    }
                } 
                bytes[0] += 5;
                String str = new String(bytes, "UTF-16LE");
                sb.append(str);
                i += 2; 
            }
            return sb.toString();
        } catch (Exception e) {
            QspLogger.error("Retrieve pwd failed: "+ e.getMessage());
            return null;
        }
    }
    
//    public static void main(String[] args) {
//        File f = new File("D:\\___DL\\_new\\bd\\svscomics\\to read 2\\__tosee\\qsp\\__poub\\pwd.qsp");
//        System.out.println("result: "+getPasswordFromFile(f));
//    }
    
    /* File chooser for QSP --------------------------------------------------*/
    
    private static JFileChooser fc = null;
    
    public static JFileChooser getFileChooser() {
        if(fc == null) {
            fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new FileFilter() {

                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() || "qsp".equalsIgnoreCase(QspToolsUtil.getExt(file)));
                }

                @Override
                public String getDescription() {
                    return "QSP files";
                }
            });
        }
        return fc;
    }
    
}
