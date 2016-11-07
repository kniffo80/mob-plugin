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
public class ItemEnderPearl extends MobPluginItems {

    public ItemEnderPearl() {
        this(0, 1);
    }

    public ItemEnderPearl(Integer meta) {
        this(meta, 1);
    }

    public ItemEnderPearl(Integer meta, int count) {
        super(ENDER_PEARL, meta, count, "Ender Pearl");
    }
    
}
