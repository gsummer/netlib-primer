package org.networklibrary.primer.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;
import org.networklibrary.primer.Primer;

public class TabFileParser extends FileBasedParser<IdData> {
	protected static final Logger log = Logger.getLogger(TabFileParser.class.getName());

	private List<String> columns = null;
	private String colsep = "\\t";
	private String valuesep = colsep;
	
	public TabFileParser() {
	}

	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		
		String line = readLine();
		List<IdData> res = null;
		
		if(!line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values =line.split(colsep,-1);
			
			for(int i = 1; i < values.length; ++i){
				if(!values[i].isEmpty()){		
					res.addAll(createIdData(values[0],columns.get(i),values[i]));
				}
			}
		}
		
		return res;
	}

	@Override
	public void parseHeader(String header) {
		columns = new ArrayList<String>();
		
		for(String colname : header.split(colsep,-1)){
			columns.add(checkDictionary(colname));
		}
	}
	
	private List<IdData> createIdData(String matchID, String propertyName, String value){
		List<IdData> res = new ArrayList<IdData>();
		
		String[] values = value.split(valuesep,-1);
		
		for(String v : values){
			if(v != null && !v.isEmpty()){
				res.add(new IdData(matchID,propertyName,v.trim()));
			}
		}
		
		return res;
	}

	@Override
	public boolean hasHeader() {
		return true;
	}

	@Override
	public boolean hasExtraParameters() {
		return true;
	}

	@Override
	public void takeExtraParameters(List<String> extras) {
		log.info("processing extra parameters: " + extras);
		if(extras != null) {
			for(String extra : extras){
				String values[] = extra.split("=",-1);
	
				switch(values[0]) {
				case "valuesep":
					valuesep = values[1];
					break;
					
				case "colsep":
					colsep = values[1];
					break;
				}
			}
		}

		log.info("using a valuesep = " + valuesep);
	}
	
}
