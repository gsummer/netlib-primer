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
	protected String idprefix = ""; // a fix for reactome..
	protected String format = "broad";
	protected String source = "unknown";
	protected String filterOrganism = null;

	@Override
	public Collection<IdData> parse() throws ParsingErrorException {
		
		String line = readLine();
		List<IdData> res = null;
		
		System.out.println("format = " + format);
		
		switch(format.toLowerCase()) {
		case "broad":
			res = parseBroad(line);
			break;

		case "wp":
			res = parseWP(line);
			break;
		}

		return res;
	}

	protected List<IdData> parseBroad(String line){
		List<IdData> res = null;

		if(line != null && !line.isEmpty()){
			res = new LinkedList<IdData>();

			String[] values = line.split("\\t",-1);

			String id = values[0];
			String desc = values[1];

			res.add(new IdData(id, checkDictionary("name"), id));
			res.add(new IdData(id, checkDictionary("id"), id));
			res.add(new IdData(id, checkDictionary("description"), desc));
			res.add(new IdData(id, checkDictionary("source"), source));
		}

		return res;
	}

	protected List<IdData> parseWP(String line) {
		List<IdData> res = null;

		if(line != null && !line.isEmpty()){
			res = new LinkedList<IdData>();

			String[] values = line.split("\\t",-1);

			String[] col0 = values[0].split("%",-1);

			String id = col0[2];
			String name = col0[0];
			String source = col0[1];
			String organism = col0[3];

			String url = values[1];
			
			System.out.println(id + " -> " + name);

			if(filterOrganism == null || organism.equalsIgnoreCase(filterOrganism)){

				res.add(new IdData(id, checkDictionary("id"), id));
				res.add(new IdData(id, checkDictionary("name"), name));
				res.add(new IdData(id, checkDictionary("source"), source));
				res.add(new IdData(id, checkDictionary("organism"), organism));
				res.add(new IdData(id, checkDictionary("url"), url));
			}
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
				case "format":
					format = values[1];
					break;
				case "idcol":
					idcol = Integer.valueOf(values[1]);
					break;

				case "idprefix":
					idprefix = values[1];
					break;

				case "source":
					source = values[1];
					break;

				case "organism":
					filterOrganism = values[1];
					break;
				}
			}

			log.info("using idcol =" + idcol);
			log.info("using format =" + format);
			log.info("using idprefix =" + idprefix);
			log.info("using source =" + source);
			log.info("using organism =" + filterOrganism);
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
