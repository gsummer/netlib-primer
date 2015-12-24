package org.networklibrary.primer.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;

public class MirbaseAliasParser extends FileBasedParser<IdData> {

	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		String line = readLine();
		List<IdData> res = null;
		
		if(!line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values =line.split("\\t",-1);
			
			for(int i = 1; i < values.length; ++i){
				if(!values[i].isEmpty()){		
					res.addAll(createIdData(values[0],"alias",values[i]));
				}
			}
		}
		
		return res;
	}
	
	private List<IdData> createIdData(String matchID, String propertyName, String value){
		List<IdData> res = new ArrayList<IdData>();
		
		String[] values = value.split(";",-1);
		
		for(String v : values){
			if(v != null && !v.isEmpty()){
				res.add(new IdData(matchID,propertyName,v));
			}
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
		return false;
	}

	@Override
	protected void parseHeader(String header) {
	}

}
