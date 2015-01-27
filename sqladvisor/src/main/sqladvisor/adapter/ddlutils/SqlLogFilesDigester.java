package sqladvisor.adapter.ddlutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;

public class SqlLogFilesDigester implements Transformer<File, Map<String, Integer>> {

    @SuppressWarnings("unchecked")
    public Map<String, Integer> transform(File input) {
	Map<String, Integer> filtered = new HashMap<String, Integer>();
	try {
	    List<String> lines = FileUtils.readLines(input);
	    String statement = "";
	    Integer duration = Integer.valueOf(0);
	    for( String line: lines ) {
		if ( line.contains("executing prepstmnt ") && line.contains(" SELECT ")) {
		    int offset = line.lastIndexOf("[params=") > -1 ? line.lastIndexOf("[params=") : line.length();
		    statement = line.substring(line.indexOf(" SELECT "), offset);
		} else if ( statement != null &&  line.contains("ms] spent")) {
		    duration = Integer.valueOf(line.substring(line.indexOf("[")+1, line.indexOf("ms] spent")).trim());
		    filtered.put(statement, duration);
		    statement = null;
		    duration = Integer.valueOf(0);
		} else if ( statement != null &&  !line.contains("ms] spent") ){
		    throw new RuntimeException("Fehler! Es konnte zu Statement " + statement + " keine Laufzeit ermittelt werden!");
		}
	    }
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
	return filtered;
    }
    
}
