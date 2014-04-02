package org.networklibrary.primer.storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.networklibrary.core.storage.StorageEngine;
import org.networklibrary.core.types.IdData;

public class TxStorageEngine implements StorageEngine<IdData> {

	private final static String MATCH = "matchid";

	private GraphDatabaseService graph = null;

	private Map<String,Node> nodeCache = new HashMap<String,Node>();

	private Transaction currTx = null;
	private long currOp = 0;

	private long maxOps = 0;

	public TxStorageEngine(String db) {
		graph = new RestGraphDatabase(db);

		maxOps = 250;
	}

	@Override
	public void store(IdData curr) {

		checkTx();
		Node currNode = nodeCache.get(curr.getMatchID());
		if(currNode == null){
			currNode = graph.createNode();
			addProperty(currNode, MATCH, curr.getMatchID());
			nodeCache.put(curr.getMatchID(), currNode);
		}

		addProperty(currNode,MATCH,curr.getValue());
		addProperty(currNode,curr.getPropertyName(),curr.getValue());
	}

	@Override
	public void finishUp() {
		
		if(currTx != null){
			currTx.success();
			currTx.close();
		}

	}

	@Override
	public void storeAll(Collection<IdData> bundles) {
		for(IdData b : bundles){
			store(b);
		}
	}

	protected void checkTx() {
		currOp = currOp + 1;
		if(currTx == null){
			currTx = graph.beginTx();
			currOp = 0;
		}

		if(currOp == maxOps){
			currTx.success();
			currTx.close();
			currOp = 0;
			currTx = null;
		}
	}

	private void addProperty(Node currNode, String propertyName, String value) {

		if(currNode.hasProperty(propertyName)){
			String[] objs = (String[])currNode.getProperty(propertyName);

			Set<String> values = new HashSet<String>(Arrays.asList(objs));
			values.add(value);
			String[] newvalues = new String[values.size()];
			newvalues = values.toArray(newvalues);

			currNode.setProperty(propertyName, newvalues);
		} else {
			String[] values = new String[1];
			values[0] = value;
			currNode.setProperty(propertyName, values);
		}

	}

}
