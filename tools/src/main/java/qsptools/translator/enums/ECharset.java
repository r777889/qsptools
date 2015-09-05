/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qsptools.translator.enums;

import java.nio.charset.Charset;

/**
 * All available Charset that can be used by this tool.
 * @author pseudo555
 */
public enum ECharset {
    UTF8("UTF-8"),
    UTF16LE("UTF-16LE");
    
    private final Charset charset;
    private final String name;
    
    /**
     * Constructor
     * @param nam name of the charset to be used 
     */
    private ECharset(final String nam){
        name = nam;
        charset = Charset.forName(nam);
    }

    /**
     * @return Charset implementation of this ECharset.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * @return name of this ECharset.
     */
    public String getName() {
        return name;
    }
}
