package sqladvisor;

import java.util.HashMap;
import java.util.Map;

public enum DataModelType {
    
	VARCHAR,
	NVARCHAR,
	INTEGER,
	SMALLINT,
	BIGINT,
	BOOLEAN,
	DOUBLE,
	TIMESTAMP,
	LONG,
	FLOAT,
	CLOB,
	BLOB, 
	UNKNOWN;

	public static DataModelType parse(final String type) {
	    return DataModelTypeParser.parse(type);
	}
	
}

class DataModelTypeParser {
    private static final Map<String, DataModelType> STATIC_TYPE_MAPPING = new HashMap<String, DataModelType>();
    static {
	STATIC_TYPE_MAPPING.put("INTEGER", DataModelType.INTEGER);
	STATIC_TYPE_MAPPING.put("BIGINT", DataModelType.BIGINT);
	STATIC_TYPE_MAPPING.put("TIMESTAMP", DataModelType.TIMESTAMP);
	STATIC_TYPE_MAPPING.put("VARCHAR", DataModelType.VARCHAR);
	STATIC_TYPE_MAPPING.put("SMALLINT", DataModelType.SMALLINT);
	STATIC_TYPE_MAPPING.put("CLOB", DataModelType.CLOB);
	STATIC_TYPE_MAPPING.put("BLOB", DataModelType.BLOB);
	STATIC_TYPE_MAPPING.put("NVARCHAR", DataModelType.NVARCHAR);
    }
    
    public static DataModelType parse(String typeName) {
	DataModelType type = STATIC_TYPE_MAPPING.get(typeName.toUpperCase());
	return type == null ? DataModelType.UNKNOWN : type;
    }
}
