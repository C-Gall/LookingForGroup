package com.lookingforgroup.util.filters;

import java.util.List;

public class FilterResult {
	private String SQL;
	private List<Object> values;
	
	public FilterResult() {
		
	}

	public String getSQL() {
		return SQL;
	}

	public void setSQL(String sQL) {
		SQL = sQL;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}
}
