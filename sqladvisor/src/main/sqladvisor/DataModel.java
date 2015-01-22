package sqladvisor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataModel {

    private final String schemaName;
    private final List<String> tableNames;
    private final Map<String, List<DataModelIndex>> indices;
    private Map<String, List<DataModelColumn>> columns;

    public DataModel(String schemaName, List<String> tables,
	    Map<String, List<DataModelColumn>> columns,
	    Map<String, List<DataModelIndex>> incides) {
	this.schemaName = schemaName;
	tableNames = tables;
	this.columns = columns;
	this.indices = incides;
    }

    /**
     * @return Schema
     */
    public String getSchemaName() {
	return schemaName;
    }

    /**
     * @return Tablenames
     */
    public List<String> getTableNames() {
	return tableNames;
    }

    /**
     * @param tableName
     * @return Map
     */
    public List<DataModelIndex> getIndicesForTable(String tableName) {
	return indices.get(tableName);
    }

    /**
     * @param columns
     * @return {@link DataModelIndex}
     */
    public static DataModelIndex createIndex(DataModelColumn... columns) {
	return new DataModelIndex(Arrays.asList(columns));
    }

    public boolean isInIndex(String table, String columnName) {
	return getIndex(table, columnName) != null;
    }

    private DataModelIndex getIndex(String table, String columnName) {
	List<DataModelIndex> list = indices.get(table.toLowerCase());
	if (list != null) {
	    for (DataModelIndex index : list) {
		List<DataModelColumn> columns = index.getColumns();
		for (DataModelColumn c : columns) {
		    if (c.getColumnName().equalsIgnoreCase(columnName)) {
			return index;
		    }
		}
	    }
	}
	return null;
    }

    public void addIndices(String table, List<DataModelIndex> index) {
	indices.put(table.toLowerCase(), index);
    }

    public boolean isPrimaryKey(String tableName, String columnName) {
	List<DataModelColumn> list = columns.get(tableName);
	if (list != null) {
	    for (DataModelColumn c : list) {
		if (c.getColumnName().equalsIgnoreCase(columnName)) {
		    return c.isPrimaryKey();
		}
	    }
	}
	return false;
    }

}
