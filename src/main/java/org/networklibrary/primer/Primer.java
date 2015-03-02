package org.networklibrary.primer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.networklibrary.core.config.ConfigManager;
import org.networklibrary.core.parsing.Parser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.storage.StorageEngine;
import org.networklibrary.core.types.IdData;
import org.networklibrary.primer.parsing.DisgenetDiseaseParser;
import org.networklibrary.primer.parsing.TabFileParser;
import org.networklibrary.primer.storage.IdBundleStorageEngine;

public class Primer {
	protected static final Logger log = Logger.getLogger(Primer.class.getName());
	
	private static Map<String,Class> parsers = new HashMap<String,Class>();
	private static Map<String,String> supported = new HashMap<String,String>();
	static {
		addParser("TAB","Tab file (default)",TabFileParser.class);
		addParser("DGND","Disgenet Disease parser",DisgenetDiseaseParser.class);
	}

	private static void addParser(String cmd, String name, Class parser){
		parsers.put(cmd,parser);
		supported.put(cmd, name);
	}
	
	private String db;
	private String type;
	private List<String> inputFiles;
	private ConfigManager confMgr;
	private List<String> extras;
	private boolean index;
	private boolean array;
	private boolean noNew;
	private boolean label;
	private boolean allowMulti;
	

	public Primer(String db, ConfigManager confMgr, String type, List<String> inputFiles, List<String> extras, boolean index, boolean array, boolean noNew, boolean label,boolean allowMulti) {
		setDb(db);
		this.confMgr = confMgr;
		this.type = type;
		this.inputFiles = inputFiles;
		this.extras = extras;
		this.index = index;
		this.array = array;
		this.noNew = noNew;
		this.label = label;
		this.allowMulti = allowMulti;
		
		if(this.type == null || this.type.isEmpty()){
			this.type = "TAB";
		}
	}

	public boolean isNoNew() {
		return noNew;
	}

	public boolean isIndex() {
		return index;
	}

	public boolean isArray() {
		return array;
	}

	public boolean isLabel() {
		return label;
	}
	
	public boolean isAllowMulti(){
		return allowMulti;
	}

	public void prime() throws IOException {

//		GraphDatabaseService g = new RestGraphDatabase(db);
		GraphDatabaseService g = new GraphDatabaseFactory().newEmbeddedDatabase(db);

		StorageEngine<IdData> se = new IdBundleStorageEngine(g,confMgr,isIndex(),isArray(),isNoNew(),isLabel(),isAllowMulti());

		log.info("connecting to db: " + getDb());

		// files can be handled multithreaded?
		for(String inputFile : inputFiles){
			long start = System.nanoTime();
			try {
//				Parser<IdData> p = new TabFileParser();
				Parser<IdData> p = makeParser();

				if(p.hasExtraParameters())
					p.takeExtraParameters(extras);

				p.setDataSource(inputFile);

				while(p.ready()){
					se.storeAll(p.parse());
				}

				long end = System.nanoTime();
				long elapsed = end - start;
				log.info("finished " + inputFile + " in " + (elapsed/1000000000));
			} catch(ParsingErrorException e){
				log.severe("error during parsing of location="+inputFile+ ": " + e.getMessage());
			}
		}
		se.finishUp();
		g.shutdown();
	}

	protected String getDb() {
		return db;
	}

	protected List<String> getInputFiles() {
		return inputFiles;
	}

	protected void setDb(String db) {
		this.db = db;
	}

	protected void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	protected String getType(){
		return type;
	}
	
	protected Parser<IdData> makeParser(){
		Parser<IdData> p = null;

		try {
			log.info("Have type = " + getType() + " -> parser = " + parsers.get(getType()));		
			p = (Parser<IdData>)getParsers().get(getType()).newInstance();
		} catch (InstantiationException e) {
			log.severe("InstantiationException when creating parser for: " + getType() + ": " + e.getMessage());
		} catch (IllegalAccessException e) {
			log.severe("IllegalAccessException when creating parser for: " + getType() + ": " + e.getMessage());
		}

		return p;
	}
	
	public static String printSupportedTypes() {
		StringBuilder buff = new StringBuilder();

		for(Entry<String,Class> p : parsers.entrySet() ){
			buff.append("\t" + p.getKey() + " = " + supported.get(p.getKey()));
			buff.append("\n");
		}

		return buff.toString();
	}
	
	protected Map<String,Class> getParsers(){
		return parsers;
	}

}
