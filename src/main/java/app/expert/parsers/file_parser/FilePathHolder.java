package app.expert.parsers.file_parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FilePathHolder {

    private static final String ABC3 = "ABC-3xx.csv";
    private static final String ABC4 = "ABC-4xx.csv";
    private static final String ABC8 = "ABC-8xx.csv";
    private static final String DEF9 = "DEF-9xx.csv";
    private static final String ABC_DEF_PATH_RESOURCES = "phone_codes/";

    private static final Logger log = LoggerFactory.getLogger(FilePathHolder.class.getSimpleName());


    public static String getAbc3 () { return ABC3; }

    public static String getAbc4 () {
        return ABC4;
    }

    public static String getAbc8 () { return ABC8; }

    public static String getDef9() {
        return  DEF9;
    }

    public static List<String> getAllResourcePaths() {
        List<String> paths= new ArrayList<>();
        paths.add(ABC_DEF_PATH_RESOURCES + getAbc3());
        paths.add(ABC_DEF_PATH_RESOURCES + getAbc4());
        paths.add(ABC_DEF_PATH_RESOURCES + getAbc8());
        paths.add(ABC_DEF_PATH_RESOURCES + getDef9());
        return paths;
    }
}
