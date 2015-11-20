package org.networklibrary.primer.parsing;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;

public class GmtIdParser extends FileBasedParser<IdData> {
protected static final Logger log = Logger.getLogger(GmtIdParser.class.getName());
	
	protected int idcol = 0;
	protected String idprefix = ""; // a fix for reactome...

	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		String line = readLine();
		List<IdData> res = null;
		
		if(line != null && !line.isEmpty()){
			res = new LinkedList<IdData>();
			
			String[] values = line.split("\\t",-1);
			
			String[] col0 = values[0].split("%",-1);
			
			res.add(new IdData(col0[col0.length-1], "id", idprefix + col0[col0.length-1]));
			res.add(new IdData(col0[col0.length-1], "name", values[1]));
			res.add(new IdData(col0[col0.length-1], "source", col0[1]));
			
			System.out.println(col0[col0.length-1] + " -> name = " + values[1]);
		}
		
		return res;
	}

	@Override
	public boolean hasExtraParameters() {
		return true;
	}

	@Override
	public void takeExtraParameters(List<String> extras) {
		if(extras != null){
			log.info("processing extra parameters: " + extras.toString());

			for(String extra : extras){
				String values[] = extra.split("=",-1);

				switch(values[0]) {					
				case "idcol":
					idcol = Integer.valueOf(values[1]);
					break;
					
				case "idprefix":
					idprefix = values[1];
					break;
				}
			}

			log.info("using idcol =" + idcol);
		}
		
	}

	@Override
	protected boolean hasHeader() {
		return false;
	}

	@Override
	protected void parseHeader(String header) {		
	}

}
