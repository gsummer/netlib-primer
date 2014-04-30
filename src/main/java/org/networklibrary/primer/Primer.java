package org.networklibrary.primer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.networklibrary.core.config.ConfigManager;
import org.networklibrary.core.parsing.Parser;
import org.networklibrary.core.parsing.ParsingErrorException;
import org.networklibrary.core.storage.StorageEngine;
import org.networklibrary.core.types.IdData;
import org.networklibrary.primer.parsing.TabFileParser;
import org.networklibrary.primer.storage.IdBundleStorageEngine;

public class Primer {
	protected static final Logger log = Logger.getLogger(Primer.class.getName());
	private String db;
	private List<String> inputFiles;
	private ConfigManager confMgr;
	private List<String> extras;
	private boolean index;
	private boolean array;
	private boolean noNew;
	private boolean label;

	public Primer(String db, ConfigManager confMgr, List<String> inputFiles, List<String> extras, boolean index, boolean array, boolean noNew, boolean label) {
		setDb(db);
		this.confMgr = confMgr;
		this.inputFiles = inputFiles;
		this.extras = extras;
		this.index = index;
		this.array = array;
		this.noNew = noNew;
		this.label = label;
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

	public void prime() throws IOException {

//		GraphDatabaseService g = new RestGraphDatabase(db);
		GraphDatabaseService g = new GraphDatabaseFactory().newEmbeddedDatabase(db);

		StorageEngine<IdData> se = new IdBundleStorageEngine(g,confMgr,isIndex(),isArray(),isNoNew(),isLabel());

		log.info("connecting to db: " + getDb());

		// files can be handled multithreaded?
		for(String inputFile : inputFiles){
			long start = System.nanoTime();
			try {
				Parser<IdData> p = new TabFileParser();

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

	}

	protected String getDb() {
		return db;
	}

	protected List<String> getInputFiles() {
		return inputFiles;
	}

	protected void setDb(String db) {
		// TODO validate url
		this.db = db;
	}

	protected void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}

}
