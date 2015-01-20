package sqladvisor;

import java.util.ArrayList;
import java.util.List;

public class Advise {

	private String codeFragment;
	private String tableName;
	private List<String> columns = new ArrayList<String>();

	public Advise(String table, String codeFragment, List<DataModelColumn> cols) {
		tableName = table;
		this.codeFragment = codeFragment;
		for(DataModelColumn c: cols) {
			columns.add(c.getColumnName());
		}
	}

	public String getCodeFragment() {
		return codeFragment;
	}

	public String getTableName() {
		return tableName;
	}
	
	public List<String> getColumns() {
		return columns;
	}

}
