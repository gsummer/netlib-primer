package org.networklibrary.primer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.networklibrary.core.parsing.Parser;
import org.networklibrary.core.storage.StorageEngine;
import org.networklibrary.primer.parsing.TabFileParser;
import org.networklibrary.primer.storage.TxStorageEngine;

public class Primer {

	private String db;
	private List<String> inputFiles;
	
	public Primer(String db, List<String> inputFiles) {
		setDb(db);
		this.inputFiles = inputFiles;
	}

	public void prime() throws IOException {

//		StorageEngine se = new DefaultStorageEngine(getDb());
		StorageEngine se = new TxStorageEngine(db);
		
		System.out.println("connecting to db: " + getDb());
		
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
			
			long end = System.nanoTime();
			long elapsed = end - start;
			System.out.println("finished " + inputFile + " in " + (elapsed/1000000000));
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
