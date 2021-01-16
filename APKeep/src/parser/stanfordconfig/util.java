package parser.stanfordconfig;

import java.io.File;
import java.util.HashSet;

/**
 * @version 1.0
 * @ClassName util
 * @Description
 * @Author liminglong
 * @Date 2021/1/7 21:06
 */
public class util {
    public static HashSet<String> getFileName(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        HashSet<String> fileName = new HashSet<>();
        for (int i = 0; i < files.length; i++) {
            fileName.add(files[i].getName());
        }
        return fileName;
    }
}
