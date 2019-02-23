package io.breezil.queryfier.dto;

import java.util.ArrayList;
import java.util.List;

public class BaseDto {
	private List<String> columns;
	
	public BaseDto() {
		this.columns = new ArrayList<String>();
	}

}
