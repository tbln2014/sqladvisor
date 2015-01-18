package sqladvisor.impl;

import org.junit.Test;
import org.mockito.Mock;

import com.foundationdb.sql.StandardException;

import sqladvisor.IDataModel;


public class SQLAdvisorTest {

	@Mock
	private IDataModel dataModelMock;

	@Test
	public void testAdvisor() throws StandardException {
		SQLAdvisor advisor = new SQLAdvisor(dataModelMock);
		advisor.adviseStatement("SELECT DATA1, DATA2 FROM TAB1 as t1, TAB2 as T2 WHERE t1.IDX = t2.REF_T1");
	}
	
}
