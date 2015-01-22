package sqladvisor.adapter.ddlutils;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.junit.Before;
import org.junit.Test;

import sqladvisor.DataModel;
import sqladvisor.DataModelColumn;
import sqladvisor.SQLAdvisor;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.SQLParserException;

public class DdlUtilsModelAdapterTest {

    private final String logFileSample = "testdigest.txt";
    private DataModel model;

    @Before
    public void setup() {
	DdlUtilsModelAdapter ddl = new DdlUtilsModelAdapter("text",
		new Transformer<DataModelColumn, DataModelColumn>() {
		    public DataModelColumn transform(DataModelColumn input) {
			if (input.getColumnName().equalsIgnoreCase("jdoidx")) {
			    input.setPrimaryKey(true);
			}
			return input;
		    }
		});
	System.out.println(new File(".").getAbsolutePath());
	Database db = new DatabaseIO()
		.read(new File(
			"dbmodel.xml"));
	model = ddl.transform(db);
    }

    @Test
    public void testDdlUtils() throws StandardException {
	new SQLAdvisor(model)
		.adviseStatement("SELECT * FROM TAB1 t1, TAB2 t2 WHERE t1.JDOIDX = t2.REF_IDX");
    }

    @Test
    public void testDdlUtils_withSQLLoggingFile() throws StandardException {
	SqlLogFilesDigester sqlLoggingDigester = new SqlLogFilesDigester();
	Map<String, Integer> sqls = sqlLoggingDigester.transform(new File(logFileSample));
	for (String sql : sqls.keySet()) {
	    try {
		List<String> adviseStatement = new SQLAdvisor(model)
			.adviseStatement(sql);
		if (adviseStatement.size() > 0) {
		    System.out.println("SQL: " + sql);
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
