package sqladvisor;

public interface IDataModelColumn {

	public String getColumnName();
	
	public DataModelType getColumnDataType();
	
	public boolean isPrimaryKey();
	
}
