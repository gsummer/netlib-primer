package org.networklibrary.primer.parsing;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.networklibrary.core.parsing.Parser;
import org.networklibrary.core.types.IdData;

public class TabFileParser implements Parser<IdData> {

	private List<String> columns = null;
	
	public TabFileParser() {
	}

	@Override
	public Collection<IdData> parse(String line) {
		List<IdData> res = null;
		
		if(!line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values =line.split("\\t",-1);
			
			if(values.length != columns.size()){
				throw new IllegalArgumentException("number of elements in row does not match number of columns " + line);
			}
			
			for(int i = 0; i < values.length; ++i){
				if(!values[i].isEmpty()){
					res.add(new IdData(values[0],columns.get(i),values[i]));
				}
			}
			
		}
		
		return res;
	}

	@Override
	public void parseHeader(String header) {
		columns = Arrays.asList(header.split("\\t",-1));
		
	}

	
}
