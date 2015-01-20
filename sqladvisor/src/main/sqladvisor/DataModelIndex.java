package sqladvisor;

import java.util.List;

public class DataModelIndex {

	private final List<DataModelColumn> columns;

	/** 
	 * @param columns
	 */
	public DataModelIndex(List<DataModelColumn> columns) {
		this.columns = columns;
	}
	
	/**
	 * @return colums which are used by index in order of index
	 */
	public List<DataModelColumn> getColumns() {
		return columns;
	}
	
}
