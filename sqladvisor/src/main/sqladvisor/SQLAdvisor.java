package sqladvisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.BinaryRelationalOperatorNode;
import com.foundationdb.sql.parser.ColumnReference;
import com.foundationdb.sql.parser.FromBaseTable;
import com.foundationdb.sql.parser.FromSubquery;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.ValueNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

public class SQLAdvisor {

    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private DataModel dataModelProvider;
    private SQLParser parser;
    private UnionAdvisor unionFilter;
    private TableVisitor tableFilter;
    private List<String> advised = new ArrayList<String>();
    private List<String> rejected = new ArrayList<String>();

    public SQLAdvisor(DataModel dataModelProvider) {
	this.dataModelProvider = dataModelProvider;
	parser = new SQLParser();
    }

    public List<String> adviseStatement(String sql) throws StandardException {
	StatementNode stmt;
	try {
	    stmt = parser.parseStatement(sql);
	} catch (Exception e) {
	    rejected.add("FEHLER bei Parsing von Statement " + sql + " -> "
		    + e.getMessage());
	    return EMPTY_LIST;
	}
	unionFilter = new UnionAdvisor(sql);
	tableFilter = new TableVisitor();
	stmt.accept(unionFilter);
	stmt.accept(tableFilter);

	Map<String, List<ColumnReference>> columns = unionFilter.getColumns();
	for (String k : columns.keySet()) {
	    List<ColumnReference> list = columns.get(k);
	    for (ColumnReference ref : list) {
		String tableName = tableFilter.getTableName(ref.getTableName());
		if (tableName == null) {
		    rejected.add("WARNUNG: Konnte für "
			    + ref.getColumnName()
			    + " in "
			    + sql
			    + " keinen Tabellenursprung bestimmen. Daher ist keine SQL-Empfehlung möglich!");
		    continue;
		}
		if (!dataModelProvider.isPrimaryKey(tableName,
			ref.getColumnName())
			&& !dataModelProvider.isInIndex(tableName,
				ref.getColumnName())) {
		    advised.add("Spalte " + tableName + "."
			    + ref.getColumnName()
			    + " ist in keinem Index erfasst. (Verwendung: "
			    + tableName + "." + k + ")");
		}
	    }
	}
	return advised;
    }

    public List<String> getRejectedStatements() {
	return rejected;
    }

}

class TableVisitor implements Visitor {

    private Map<String, String> tables = new HashMap<String, String>();

    public Visitable visit(Visitable node) throws StandardException {
	if (node instanceof FromBaseTable) {
	    FromBaseTable t = (FromBaseTable) node;

	    String aliasOrName = StringUtils.isBlank(t.getCorrelationName()) ? t
		    .getOrigTableName().getTableName() : t.getCorrelationName();
	    String name = t.getOrigTableName() == null ? t.getTableName()
		    .getTableName() : t.getOrigTableName().getTableName();
	    tables.put(aliasOrName, name);
	} else if (node instanceof FromSubquery) {
	    FromSubquery t = (FromSubquery) node;

	    String aliasOrName = StringUtils.isBlank(t.getCorrelationName()) ? t
		    .getOrigTableName().getTableName() : t.getCorrelationName();
	    String name = t.getOrigTableName() == null ? t.getTableName()
		    .getTableName() : t.getOrigTableName().getTableName();
	    tables.put(aliasOrName, name);
	}

	return node;
    }

    public boolean visitChildrenFirst(Visitable node) {
	return false;
    }

    public boolean stopTraversal() {
	return false;
    }

    public boolean skipChildren(Visitable node) throws StandardException {
	// TODO Auto-generated method stub
	return false;
    }

    /**
     * Lookup for reference table name via alias. If real name is provided, the
     * real name will be returned.
     * 
     * @param aliasOrName
     * @return real table name
     */
    public String getTableName(String aliasOrName) {
	String lookup = tables.get(aliasOrName);
	return lookup == null ? aliasOrName : lookup;
    }
}

class UnionAdvisor implements Visitor {

    private final String statementSQL;
    private Map<String, List<ColumnReference>> relevantColumns = new HashMap<String, List<ColumnReference>>();

    public UnionAdvisor(String sql) {
	statementSQL = sql;
    }

    public boolean skipChildren(Visitable arg0) throws StandardException {
	return false;
    }

    public boolean stopTraversal() {
	return false;
    }

    public Visitable visit(Visitable arg0) throws StandardException {
	if (arg0 instanceof BinaryRelationalOperatorNode) {
	    BinaryRelationalOperatorNode n = (BinaryRelationalOperatorNode) arg0;
	    append(getStatement(n), n.getLeftOperand());
	    append(getStatement(n), n.getRightOperand());
	}
	return arg0;
    }

    private String getStatement(BinaryRelationalOperatorNode n) {
	if (n.getBeginOffset() == -1
		&& n.getLeftOperand().getBeginOffset() != -1) {
	    return statementSQL.substring(n.getLeftOperand().getBeginOffset(),
		    n.getRightOperand().getEndOffset()).trim();
	}
	return n.getBeginOffset() > -1 && n.getEndOffset() > -1 ? statementSQL
		.substring(n.getBeginOffset(), n.getEndOffset()).trim()
		: "FEHLER bei Teilermittlung aus: " + statementSQL.trim();
    }

    private void append(String conditionString, ValueNode operand) {

	List<ColumnReference> list = relevantColumns.get(conditionString);
	if (list == null) {
	    list = new ArrayList<ColumnReference>();
	}

	if (operand instanceof ColumnReference) {
	    list.add((ColumnReference) operand);
	    relevantColumns.put(conditionString, list);
	}
    }

    public boolean visitChildrenFirst(Visitable arg0) {
	return false;
    }

    public Map<String, List<ColumnReference>> getColumns() {
	return relevantColumns;
    }

    public void debug() {
	for (String k : relevantColumns.keySet()) {
	    List<ColumnReference> list = relevantColumns.get(k);
	    System.out.println(k);
	    for (ColumnReference ref : list) {
		System.out.println("Column: " + ref.getTableName() + " -> "
			+ ref.getColumnName());
	    }
	}
    }

}