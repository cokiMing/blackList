package com.cokiMing.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wuyiming on 2017/11/7.
 */
public class Configuration {

    private static Properties properties;

    public static String get(String key) {
        return properties.getProperty(key);
    }

    private static InputStream loadFromClassPath(String path) {
        InputStream is = null;
        if(is == null) {
            try {
//                is = new FileInputStream(System.getProperty("user.dir") + "/conf/" + path);
                is = Configuration.class.getResourceAsStream("/conf/" + path);
                System.out.println("读取到文件cfg.properties");
            } catch (Exception var3) {
                System.out.println("cfg.properties文件读取错误" + var3.getMessage());
                is = null;
            }
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(is == null) {
            is = loader.getResourceAsStream(path);
        }

        if(is == null) {
            loader = Configuration.class.getClassLoader();
            is = loader.getResourceAsStream(path);
        }

        if(is == null) {
            loader = ClassLoader.getSystemClassLoader();
            is = loader.getResourceAsStream(path);
        }

        return (InputStream)is;
    }

    static {
        try {
            properties = new Properties();
            properties.load(loadFromClassPath("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
