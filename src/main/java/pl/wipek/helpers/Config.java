package pl.wipek.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Krzysztof Adamczyk on 27.04.2017.
 * Helper is responding for reading from config file
 */
public class Config {

    /**
     * Log4j2 logger is responding for log events to files
     */
    private final static Logger logger = LogManager.getRootLogger();

    /**
     * path to config file containing ip and port to connect with server
     */
    private final String configPath = "../app.txt";

    /**
     * @author Krzysztof Adamczyk
     * @return ArrayList what is containing content of config file
     * every index is the next line in config file
     */
    public ArrayList<String> readConfig() {
        Scanner in = null;
        ArrayList<String> result = new ArrayList<>(0);
        try{
            ClassLoader classLoader = getClass().getClassLoader();
            File config = new File(classLoader.getResource(this.configPath).getFile());
            in = new Scanner(config, "UTF-8");
            while(in.hasNextLine()) {
                result.add(in.nextLine());
            }
        }catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }finally {
            if(in != null) {
                in.close();
            }
        }
        return result;
    }
}
