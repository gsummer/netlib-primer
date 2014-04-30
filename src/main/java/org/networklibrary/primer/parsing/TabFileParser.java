package org.networklibrary.primer.parsing;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;

public class TabFileParser extends FileBasedParser<IdData> {

	private List<String> columns = null;
	
	public TabFileParser() {
	}

	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		
		String line = readLine();
		List<IdData> res = null;
		
		if(!line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values =line.split("\\t",-1);
			
			for(int i = 1; i < values.length; ++i){
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

	@Override
	public boolean hasHeader() {
		return true;
	}

	@Override
	public boolean hasExtraParameters() {
		return false;
	}

	@Override
	public void takeExtraParameters(List<String> extras) {
	}

	
}
