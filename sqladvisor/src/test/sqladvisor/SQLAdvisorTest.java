package sqladvisor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.foundationdb.sql.StandardException;


public class SQLAdvisorTest {

	@Mock
	private DataModel dataModelMock;
	
	/**
	 * Setup
	 */
	@Before
	public void setup() {
		Map<String, List<DataModelIndex>> indices = new HashMap<String, List<DataModelIndex>>();
		dataModelMock = new DataModel("TEST", Arrays.asList("TAB1", "TAB2", "TAB3"), indices);
		dataModelMock.addIndices("TAB1", Arrays.asList( //
				DataModel.createIndex(new DataModelColumn("TAB1", "DATA1", DataModelType.INTEGER, false)), //
				DataModel.createIndex(new DataModelColumn("TAB1", "IDX", DataModelType.INTEGER, true)) //
				));
		dataModelMock.addIndices("TAB2",  Arrays.asList( //
				DataModel.createIndex(new DataModelColumn("TAB2", "DATA2", DataModelType.INTEGER, false)), //
				DataModel.createIndex(new DataModelColumn("TAB2", "REF_T1", DataModelType.INTEGER, false)) //
				));
	}

	@Test
	public void testAdvisor() throws StandardException {
		SQLAdvisor advisor = new SQLAdvisor(dataModelMock);
		advisor.adviseStatement("SELECT t1.DATA1, t2.DATA2 FROM TAB1 as t1, TAB2 as T2 WHERE t1.IDX = t2.REF_T1");
	}
	
	@Test
	public void testAdvisor_join() throws StandardException {
		SQLAdvisor advisor = new SQLAdvisor(dataModelMock);
		advisor.adviseStatement("SELECT t1.DATA1, t2.DATA2 FROM TAB1 as t1 JOIN TAB2 as T2 ON t1.IDX = t2.REF_T1");
	}
	
	@Test
	public void testAdvisor_join2() throws StandardException {
		SQLAdvisor advisor = new SQLAdvisor(dataModelMock);
		advisor.adviseStatement("SELECT t1.DATA1, t2.DATA2 FROM TAB1 as t1 JOIN TAB2 as T2 ON t1.IDX = t2.REF_T1 JOIN " 
		+ "TAB3 as T3 ON t3.ID = TAB2.REF_T1 WHERE t2.DATA2 = t1.DATA1 AND t1.DATA1 IN (1, 2, 3)");
	}	
	
}
