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

public class DefaultStorageEngine implements StorageEngine<IdData> {

	private final static String MATCH = "matchid";
	
	private GraphDatabaseService graph = null;

	private Map<String,Node> nodeCache = new HashMap<String,Node>();

	public DefaultStorageEngine(String db) {
		graph = new RestGraphDatabase(db);
	}

	@Override
	public void store(IdData curr) {
		try(Transaction tx = graph.beginTx()){

			Node currNode = nodeCache.get(curr.getMatchID());
			if(currNode == null){
				currNode = graph.createNode();
				addProperty(currNode, MATCH, curr.getMatchID());
				nodeCache.put(curr.getMatchID(), currNode);
			}

			addProperty(currNode,MATCH,curr.getValue());
			addProperty(currNode,curr.getPropertyName(),curr.getValue());
			tx.success();
		}
	}
	
	@Override
	public void finishUp() {

	}

	@Override
	public void storeAll(Collection<IdData> bundles) {
		for(IdData b : bundles){
			store(b);
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
