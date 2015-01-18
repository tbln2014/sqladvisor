package sqladvisor.impl;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.BinaryRelationalOperatorNode;
import com.foundationdb.sql.parser.FromTable;
import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

import sqladvisor.IDataModel;

public class SQLAdvisor {
	
	private IDataModel dataModelProvider;
	private SQLParser parser;

	public SQLAdvisor(IDataModel dataModelProvider) {
		this.dataModelProvider = dataModelProvider;
		parser = new SQLParser();
	}
	
	public void adviseStatement(String sql) throws StandardException {
		StatementNode stmt = parser.parseStatement(sql);
		stmt.accept(new UnionAdvisor());
	}

}

class UnionAdvisor implements Visitor {

	public boolean skipChildren(Visitable arg0) throws StandardException {
		return false;
	}

	public boolean stopTraversal() {
		return false;
	}

	public Visitable visit(Visitable arg0) throws StandardException {
		if ( arg0 instanceof BinaryRelationalOperatorNode) {
			BinaryRelationalOperatorNode n = (BinaryRelationalOperatorNode) arg0;
			System.out.println(n.getLeftOperand().getClass());
			System.out.println(n.getRightOperand().getClass());
		}
		return null;
	}

	public boolean visitChildrenFirst(Visitable arg0) {
		return false;
	}
	
}