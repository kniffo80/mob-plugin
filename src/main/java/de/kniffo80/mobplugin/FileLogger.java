/**
 * FileLogger.java
 * 
 * Created on 10:34:06
 */
package de.kniffo80.mobplugin;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import cn.nukkit.Server;

/**
 * A simple file logger for logging into separate files
 * 
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 */
public class FileLogger {
    
    private static PrintWriter printWriter = null;
    
    static {
        try {
            printWriter = new PrintWriter(Server.getInstance().getDataPath() + "mobplugin.log", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                printWriter.close();
            }
        });
    }

    public static void debug(String text) {
        printWriter.println(String.format("%s | [DEBUG]  | %s", new Timestamp(System.currentTimeMillis()), text));
        printWriter.flush();
    }

    public static void info(String text) {
        printWriter.println(String.format("%s | [INFO]   | %s", new Timestamp(System.currentTimeMillis()), text));
        printWriter.flush();
    }
    
    public static void warn(String text) {
        printWriter.println(String.format("%s | [WARN]   | %s", new Timestamp(System.currentTimeMillis()), text));
        printWriter.flush();
    }
    
    public static void warn(String text, Throwable e) {
        printWriter.println(String.format("%s | [WARN]   | %s", new Timestamp(System.currentTimeMillis()), text));
        printWriter.println(String.format("%s | [WARN]   | %s", new Timestamp(System.currentTimeMillis()), stackTraceToString(e)));
        printWriter.flush();
    }
    
    private static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
