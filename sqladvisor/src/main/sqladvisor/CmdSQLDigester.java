package sqladvisor;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections15.Transformer;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;

import sqladvisor.adapter.ddlutils.DdlUtilsModelAdapter;
import sqladvisor.adapter.ddlutils.SqlLogFilesDigester;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.SQLParserException;

public class CmdSQLDigester {

    public static void main(String[] args) throws StandardException {
        System.out.println("Usage: <dbmodel.xml> <sql-statement log> [-statsonly]");
        
        CmdSQLDigester cmd = new CmdSQLDigester();
        
        if ( args.length < 3 ) {
            System.out.println("Argument mismatch! Exiting.");
            return;
        }
        
        boolean statsOnly = false;
        if ( args.length == 4 ) {
            statsOnly = true;
        }
        
        cmd.setup(args[1]);
        cmd.testDdlUtils_withSQLLoggingFile(args[2], statsOnly);
    }


    private DataModel model;

    public void setup(String dbmodel) {
	DdlUtilsModelAdapter ddl = new DdlUtilsModelAdapter("text",
		new Transformer<DataModelColumn, DataModelColumn>() {
		    public DataModelColumn transform(DataModelColumn input) {
			if (input.getColumnName().equalsIgnoreCase("jdoidx")) {
			    input.setPrimaryKey(true);
			}
			return input;
		    }
		});
	Database db = new DatabaseIO().read(new File(dbmodel));
	model = ddl.transform(db);
    }

    public void testDdlUtils_withSQLLoggingFile(String logFileSample, boolean statsOnly) throws StandardException {
	SqlLogFilesDigester sqlLoggingDigester = new SqlLogFilesDigester();
	Map<String, Integer> sqls = sqlLoggingDigester.transform(new File(logFileSample));
	Map<String, Integer> sqlAvg = sqlLoggingDigester.getStats().getAvgSqlDuration();
	
	TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(new ValueComparator(sqlAvg));
	sorted_map.putAll(sqlAvg);
	
	if ( statsOnly ) {
	    System.out.println("Reporting statements with highest average cost:");
	    for (String sql : sorted_map.keySet()) {
		System.out.println("SQL (occurances=" + sqlLoggingDigester.getStats().getOccurances().get(sql) + //
			", avg duration=" + sqlLoggingDigester.getStats().getAvgDuration(sql) + "ms" + //
			", max duration=" + sqlLoggingDigester.getStats().getMaxDuration().get(sql) + "ms" + //
			", total duration=" + sqlLoggingDigester.getStats().getTotalDurationForStatement(sql) + "ms) : " + sql);
	    }
	} else {	
        	for (String sql : sorted_map.keySet()) {
        	    try {
        		List<String> adviseStatement = new SQLAdvisor(model)
        			.adviseStatement(sql);
        		if (adviseStatement.size() > 0) {
        		    System.out.println("SQL (occurances=" + sqlLoggingDigester.getStats().getOccurances().get(sql) + ", avg duration=" + sqlLoggingDigester.getStats().getAvgDuration(sql) + ", total duration=" + sqlLoggingDigester.getStats().getTotalDurationForStatement(sql) + "ms) : " + sql);
        		    for (String s : adviseStatement) {
        			System.out.println(s);
        		    }
        		}
        	    } catch (SQLParserException e) {
        		System.out.println("Fehler beim parsen von: " + sql + " -> "
        			+ e.getMessage());
        	    }
        	}
	}
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}