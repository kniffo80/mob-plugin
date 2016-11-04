package de.kniffo80.mobplugin.items;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package cn.nukkit.item in project nukkit.
 */
public class ItemMuttonCooked extends MobPluginItems {

    public ItemMuttonCooked() {
        this(0, 1);
    }

    public ItemMuttonCooked(Integer meta) {
        this(meta, 1);
    }

    public ItemMuttonCooked(Integer meta, int count) {
        super(COOKED_MUTTON, meta, count, "Cooked Mutton");
    }
}
