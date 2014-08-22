package org.networklibrary.primer.parsing;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;

public class DisgenetDiseaseParser extends FileBasedParser<IdData> {

	private List<String> columns = null;
	
	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		String line = readLine();
		List<IdData> res = null;
		
		if(!line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values =line.split("\\t",-1);
			
			res.add(new IdData(values[3], columns.get(3), values[3]));
			res.add(new IdData(values[3], columns.get(4),values[4]));
			
		}
		
		return res;
		
	}

	@Override
	public boolean hasExtraParameters() {
		return false;
	}

	@Override
	public void takeExtraParameters(List<String> extras) {
	}

	@Override
	protected boolean hasHeader() {
		return true;
	}

	@Override
	protected void parseHeader(String header) {
		columns = Arrays.asList(header.split("\\t",-1));
	}

}
