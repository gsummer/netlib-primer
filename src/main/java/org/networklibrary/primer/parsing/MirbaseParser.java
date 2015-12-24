package org.networklibrary.primer.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.networklibrary.core.parsing.FileBasedParser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.types.IdData;

public class MirbaseParser extends FileBasedParser<IdData> {

	protected static final Logger log = Logger.getLogger(MirbaseParser.class.getName());
	
	protected String organism =  null;
	
	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		
		List<IdData> result = new ArrayList<IdData>();
		
		try {
			String entry = advanceEntry();
			result.addAll(parseEntry(entry));
		} catch (IOException e) {
			throw new ParsingErrorException("failed to parse the entry!",e);
		}
		
		return result;
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
				case "organism":
					organism = values[1];
					break;
				}
			}
		}

		log.info("filtering for organism = " + organism);
		
	}

	@Override
	protected boolean hasHeader() {
		return false;
	}

	@Override
	protected void parseHeader(String header) {
	}

	private String advanceEntry() throws ParsingErrorException {
		StringBuffer entry = new StringBuffer();
		while(ready()){
			String line = readLine();
			if(line.equals("//")){
				break;
			} else {
				entry.append(line);
				entry.append("\n");
			}
		}
		return entry.toString();
	}
	
	private List<IdData> parseEntry(String entry) throws IOException {

		List<IdData> result = new ArrayList<IdData>();
		
		String[] lines = entry.split("\n");

		String mi_id = null;
		String mi_acc = null;
		
		
		for(int i = 0; i < lines.length; ++i){
			String line = lines[i];

			if(line.substring(0, 2).equals("ID")){
				mi_id = line.split("\\s+",4)[1];
			} else if(line.substring(0, 2).equals("AC")){
				mi_acc = (line.split("\\s+")[1].replace(";", ""));
				
			} else if(line.contains("FT   miRNA")){
				String mimatacc = lines[i+1].split("\"")[1];
				String product = lines[i+2].split("\"")[1];

				if(product.substring(0,3).equalsIgnoreCase(organism)){
					IdData mimat_mimat = new IdData(mimatacc, checkDictionary("mimat"), mimatacc);
					IdData mimat_name = new IdData(mimatacc, checkDictionary("product"), product);
					
					result.add(mimat_mimat);
					result.add(mimat_name);
					
					if(mi_id != null){
						IdData mimat_mi_id = new IdData(mimatacc, checkDictionary("mi_id"), mi_id);
						result.add(mimat_mi_id);
					}
					
					if(mi_acc != null) {
						IdData mimat_mi_acc = new IdData(mimatacc, checkDictionary("mi_acc"), mi_acc);
						result.add(mimat_mi_acc);
					}
				}
				
			}
		}
		return result;
	}
}
