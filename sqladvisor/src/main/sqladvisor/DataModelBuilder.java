package sqladvisor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DataModelBuilder {

	private String schema;
	private HashSet<String> tables = new HashSet<String>();
	private Map<String, List<DataModelColumn>> indices = new HashMap<String, List<DataModelColumn>>();
	
	/**
	 * @param schema
	 * @return {@link DataModelBuilder}
	 */
	public DataModelBuilder withSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	/**
	 * @param tableName
	 * @return {@link DataModelBuilder}
	 */
	public DataModelBuilder withTable(String tableName) {
		tables.add(tableName);
		return this;
	}
	
	/**
	 * @param tableName
	 * @param columns
	 * @return {@link DataModelBuilder}
	 */
	public DataModelBuilder withIndex(String tableName, List<DataModelColumn> columns) {
		if ( indices.get(tableName) != null ) {
			throw new RuntimeException("Indices for Table " + tableName + " already defined.");
		}
		indices.put(tableName, columns);
		return this;
	}
	
}
