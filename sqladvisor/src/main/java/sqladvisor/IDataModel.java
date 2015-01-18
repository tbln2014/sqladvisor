package sqladvisor;

import java.util.List;
import java.util.Map;

public interface IDataModel {

	public String getSchemaName();
	
	public List<String> getTableNames();
	
	public Map<String, List<IDataModelIndex>> getIndicesForTable(String tableName);
	
	
	
}
