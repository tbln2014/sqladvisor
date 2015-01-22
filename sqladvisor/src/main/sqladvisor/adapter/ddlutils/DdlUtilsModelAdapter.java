package sqladvisor.adapter.ddlutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Index;
import org.apache.ddlutils.model.IndexColumn;
import org.apache.ddlutils.model.Table;

import sqladvisor.DataModel;
import sqladvisor.DataModelColumn;
import sqladvisor.DataModelIndex;
import sqladvisor.DataModelType;
import sqladvisor.adapter.IDataModelAdapter;

public class DdlUtilsModelAdapter implements IDataModelAdapter<Database> {

    private Transformer<IndexColumn[], List<DataModelColumn>> getColumnsTransformer(
	    final String tableName, final  Transformer<DataModelColumn, DataModelColumn> postTransformer) {
	return new Transformer<IndexColumn[], List<DataModelColumn>>() {
	    public List<DataModelColumn> transform(IndexColumn[] input) {
		List<DataModelColumn> columns = new ArrayList<DataModelColumn>();
		for (IndexColumn c : input) {
		    columns.add(postTransformer.transform(new DataModelColumn(tableName, c.getName(),
			    DataModelType.UNKNOWN, false)));
		}
		return columns;
	    }
	};
    }

    private Transformer<Index[], List<DataModelIndex>> getIndicesTransformer(
	    final Transformer<IndexColumn[], List<DataModelColumn>> columnsTransformer) {
	return new Transformer<Index[], List<DataModelIndex>>() {
	    public List<DataModelIndex> transform(Index[] input) {
		List<DataModelIndex> indices = new ArrayList<DataModelIndex>();
		for (Index i : input) {
		    indices.add(new DataModelIndex(columnsTransformer
			    .transform(i.getColumns())));
		}
		return indices;
	    }
	};
    }
    
    private Transformer<Column[], List<DataModelColumn>> getTableColumnsTransformer(final String tableName) {
	return new Transformer<Column[], List<DataModelColumn>>() {
	    public List<DataModelColumn> transform(Column[] input) {
		List<DataModelColumn> columns = new ArrayList<DataModelColumn>();
		for(Column c: input) {
		    columns.add(new DataModelColumn(tableName, c.getName(), DataModelType.parse(c.getType()), c.isPrimaryKey()));
		}
		return columns;
	    }
	};
    }

    private String schemaName;
    private Transformer<DataModelColumn, DataModelColumn> postColumnsTransformer;

    public DdlUtilsModelAdapter(String schemaName, Transformer<DataModelColumn, DataModelColumn> postColumnsTransformer) {
	this.schemaName = schemaName;
	this.postColumnsTransformer = postColumnsTransformer;
    }

    public DataModel transform(Database db) {
		List<String> tables = new ArrayList<String>();
		Map<String, List<DataModelColumn>> columns = new HashMap<String, List<DataModelColumn>>();
		Map<String, List<DataModelIndex>> indices = new HashMap<String, List<DataModelIndex>>();
		
		for(Table t: db.getTables()) {
			String tableName = t.getName().toLowerCase();
			tables.add(tableName);
			columns.put(tableName, getTableColumnsTransformer(tableName).transform(t.getColumns()));
			indices.put(tableName, getIndicesTransformer(getColumnsTransformer(tableName, 
				postColumnsTransformer)).transform(t.getIndices()));
		}
		
		return new DataModel(schemaName, tables, columns, indices);
	}

}
