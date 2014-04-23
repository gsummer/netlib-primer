package org.networklibrary.primer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
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

	public Primer(String db, ConfigManager confMgr, List<String> inputFiles, List<String> extras) {
		setDb(db);
		this.confMgr = confMgr;
		this.inputFiles = inputFiles;
		this.extras = extras;
	}

	public void prime() throws IOException {

		GraphDatabaseService g = new RestGraphDatabase(db);

		StorageEngine<IdData> se = new IdBundleStorageEngine(g,confMgr);

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
