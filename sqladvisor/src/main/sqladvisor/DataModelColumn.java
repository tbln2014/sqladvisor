package sqladvisor;

public class DataModelColumn {

	private final String table;
	private final String columnName;
	private final DataModelType columnDataType;
	private boolean isPrimaryKey;

	/**
	 * New Column of DataModel for table
	 * @param name
	 * @param type
	 * @param isPrimaryKey
	 */
	public DataModelColumn(String tableName, String name, DataModelType type, boolean isPrimaryKey) {
		this.table = tableName;
		this.isPrimaryKey = isPrimaryKey;
		this.columnDataType = type;
		this.columnName = name;
	}
	
	/**
	 * @return Column-Name
	 */
	public String getColumnName() {
		return columnName;
	}
	
	/**
	 * @return table of this column
	 */
	public String getTableName() {
		return table;
	}
	
	/**
	 * @return DataType Enum
	 */
	public DataModelType getColumnDataType() {
		return columnDataType;
	}
	
	/**
	 * @return true, if is primary key
	 */
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean b) {
	    isPrimaryKey = b;
	}
	
}
