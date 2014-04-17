package org.networklibrary.primer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.networklibrary.core.config.ConfigManager;
import org.networklibrary.core.parsing.Parser;
import org.networklibrary.core.storage.StorageEngine;
import org.networklibrary.core.types.IdData;
import org.networklibrary.primer.parsing.TabFileParser;
import org.networklibrary.primer.storage.IdBundleStorageEngine;

public class Primer {
	protected static final Logger log = Logger.getLogger(Primer.class.getName());
	private String db;
	private List<String> inputFiles;
	private ConfigManager confMgr;
	
	public Primer(String db, ConfigManager confMgr, List<String> inputFiles) {
		setDb(db);
		this.confMgr = confMgr;
		this.inputFiles = inputFiles;
	}

	public void prime() throws IOException {

		GraphDatabaseService g = new RestGraphDatabase(db);

		StorageEngine<IdData> se = new IdBundleStorageEngine(g,confMgr);
		
		log.info("connecting to db: " + getDb());
		
		// files can be handled multithreaded?
		for(String inputFile : inputFiles){
			long start = System.nanoTime();
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			Parser p = new TabFileParser();
			
			p.parseHeader(reader.readLine());
			
			while(reader.ready()){
				String line = reader.readLine();
				se.storeAll(p.parse(line));
			}
			
			reader.close();
			
			long end = System.nanoTime();
			long elapsed = end - start;
			log.info("finished " + inputFile + " in " + (elapsed/1000000000));
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
