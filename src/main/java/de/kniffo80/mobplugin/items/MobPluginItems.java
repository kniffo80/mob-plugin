/**
 * MobPluginItems.java
 * 
 * Created on 15:36:32
 */
package de.kniffo80.mobplugin.items;

import cn.nukkit.item.ItemEdible;

/**
 * @author <a href="mailto:mige@whatevermobile.com">Michael Gertz (mige)</a>
 *
 */
public abstract class MobPluginItems extends ItemEdible {
    
    public static final int RAW_MUTTON = 423;
    public static final int COOKED_MUTTON = 424;

    public MobPluginItems(int networkId, Integer meta, int count, String name) {
        super(networkId, meta, count, name);
    }

}
