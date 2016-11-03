/**
 * Utils.java
 * 
 * Created on 10:18:38
 */
package de.kniffo80.mobplugin;

import java.util.Random;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 *
 */
public class Utils {
    
    private static final Server SERVER = Server.getInstance();
    
    public static final void logServerInfo (String text) {
        SERVER.getLogger().info(TextFormat.GOLD + "[MobPlugin] " + text);
    }
    
    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * Returns a random number between min (inkl.) and max (excl.) If you want a number between 1 and 4 (inkl) you need to call rand (1, 5)
     * 
     * @param min min inklusive value
     * @param max max exclusive value
     * @return
     */
    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return min + random.nextInt(max - min);
    }

    /**
     * Returns random boolean
     * @return  a boolean random value either <code>true</code> or <code>false</code>
     */
    public static boolean rand() {
        return random.nextBoolean();
    }

}
