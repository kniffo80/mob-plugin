/**
 * ItemEnderPearl.java
 * 
 * Created on 10:16:06
 */
package de.kniffo80.mobplugin.items;


/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 *
 */
public class ItemInkSac extends MobPluginItems {

    public ItemInkSac() {
        this(0, 1);
    }

    public ItemInkSac(Integer meta) {
        this(meta, 1);
    }

    public ItemInkSac(Integer meta, int count) {
        super(INK_SAC, meta, count, "Ink Sac");
    }
    
}
