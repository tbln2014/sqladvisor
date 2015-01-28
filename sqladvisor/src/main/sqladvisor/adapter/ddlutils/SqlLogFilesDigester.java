package sqladvisor.adapter.ddlutils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class SqlLogFilesDigester implements Transformer<File, Map<String, Integer>> {

    private SqlLogStats stats;

    @SuppressWarnings("unchecked")
    public Map<String, Integer> transform(File input) {
	Map<String, Integer> filtered = new HashMap<String, Integer>();
	Map<String, Integer> occurances = new HashMap<String, Integer>();
	Map<String, Integer> total = new HashMap<String, Integer>();
	Map<String, Integer> max = new HashMap<String, Integer>();
	try {
	    List<String> lines = FileUtils.readLines(input);
	    String statement = "";
	    Integer duration = Integer.valueOf(0);
	    for( String line: lines ) {
		
		if ( line.contains("executing prepstmnt ") && line.contains(" SELECT ")) {
		    int offset = line.lastIndexOf("[params=") > -1 ? line.lastIndexOf("[params=") : line.length();
		    statement = line.substring(line.indexOf(" SELECT "), offset);
		} else if ( StringUtils.isNotBlank(statement) &&  line.contains("ms] spent")) {
		    duration = Integer.valueOf(line.substring(line.indexOf("[")+1, line.indexOf("ms] spent")).trim());
		    filtered.put(statement, duration);
		    occurances.put(statement, occurances.get(statement) == null ? Integer.valueOf(1) : Integer.valueOf(occurances.get(statement).intValue()+1));
		    total.put(statement, total.get(statement) == null ? Integer.valueOf(duration) : Integer.valueOf(total.get(statement).intValue()+duration));
		    max.put(statement, max.get(statement) == null ? Integer.valueOf(duration) : duration > max.get(statement).intValue() ? Integer.valueOf(duration) : max.get(statement));
		    statement = null;
		    duration = Integer.valueOf(0);
		} else if ( StringUtils.isNotBlank(statement) &&  !line.contains("ms] spent") ){
		    throw new RuntimeException("Fehler! Es konnte zu Statement " + statement + " keine Laufzeit ermittelt werden!");
		}
	    }
	} catch (IOException e) {
	  throw new RuntimeException(e);
	}
	
	stats = new SqlLogStats(filtered, occurances, total, max);
	
	return filtered;
    }
    
    public SqlLogStats getStats() {
	return stats;
    }

    public static class SqlLogStats {
	Map<String, Integer> filtered;
	Map<String, Integer> occurances;
	Map<String, Integer> totalDuration;
	Map<String, Integer> max;
	
	public SqlLogStats(Map<String, Integer> filtered, Map<String, Integer> occurances,  Map<String, Integer> totalDuration, Map<String, Integer> max) {
	    this.filtered = filtered;
	    this.occurances = occurances;
	    this.totalDuration = totalDuration;
	    this.max = max;
	}
	
	public Map<String, Integer> getOccurances() {
	    return occurances;
	}
	
	public Map<String, Integer> getDuration() {
	    return filtered;
	}
	
	public Map<String, Integer> getTotalDuration() {
	    return totalDuration;
	}
	
	public Map<String, Integer> getMaxDuration() {
	    return max;
	}
	
	public int getTotalDurationForStatement(String sql) {
	    return totalDuration.get(sql);
	}

	public int getAvgDuration(String sql) {
	    return totalDuration.get(sql) / occurances.get(sql);
	}
	
	public Map<String, Integer> getAvgSqlDuration() {
	    Map<String, Integer> avg = new HashMap<String, Integer>();
	    for(String key: occurances.keySet()) {
		avg.put(key, getAvgDuration(key));
	    }
	    return avg;
	}
    }
    
}


