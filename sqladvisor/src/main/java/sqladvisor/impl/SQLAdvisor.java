package sqladvisor.impl;

import sqladvisor.IDataModel;

public class SQLAdvisor {
	
	private IDataModel dataModelProvider;

	public SQLAdvisor(IDataModel dataModelProvider) {
		this.dataModelProvider = dataModelProvider;
	}

}
