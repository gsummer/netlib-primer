package org.networklibrary.primer.storage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.networklibrary.core.config.ConfigManager;
import org.networklibrary.core.storage.MultiTxStrategy;
import org.networklibrary.core.types.IdData;

public class IdBundleStorageEngine extends MultiTxStrategy<IdData> {

	protected static final Logger log = Logger.getLogger(IdBundleStorageEngine.class.getName());
	private final static String MATCH = "matchid";

	private Map<String,Node> nodeCache = new HashMap<String,Node>();

	private Index<Node> matchableIndex = null;
	private boolean index;
	private boolean array;
	private boolean noNew;
	private boolean label;


	public IdBundleStorageEngine(GraphDatabaseService graph,
			ConfigManager confMgr, boolean index, boolean array, boolean noNew, boolean label) {
		super(graph, confMgr);
		try ( Transaction tx = graph.beginTx() ){
			matchableIndex = graph.index().forNodes("matchable");
			tx.success();
		}

		this.index = index;
		this.array = array;
		this.noNew = noNew;
		this.label = label;
	}

	@Override
	protected void doStore(IdData curr) {
		Node currNode = getNode(curr.getMatchID(), getGraph());

		if(!noNew && currNode == null){
			currNode = getGraph().createNode();
			//			addProperty(currNode, MATCH, curr.getMatchID());
			nodeCache.put(curr.getMatchID(), currNode);
			matchableIndex.add(currNode, MATCH, curr.getMatchID());
		}

		if(currNode != null){
			if(label){
				currNode.addLabel(DynamicLabel.label(curr.getValue()));
			} 

			//		addProperty(currNode,MATCH,curr.getValue());
			addProperty(currNode,curr.getPropertyName(),curr.getValue());
			if(index){
				matchableIndex.add(currNode, MATCH, curr.getValue());
			}
		}


	}

	private void addProperty(Node currNode, String propertyName, String value) {

		if(currNode.hasProperty(propertyName)){
			if(array){
				Set<String> values = new HashSet<String>();
				Object prop = currNode.getProperty(propertyName);
				if(prop instanceof String){
					values.add((String)prop);
				} else {
					String[] objs = (String[])currNode.getProperty(propertyName);
					values.addAll(Arrays.asList(objs));
				}
				values.add(value);

				String[] newvalues = new String[values.size()];
				newvalues = values.toArray(newvalues);

				currNode.setProperty(propertyName, newvalues);
			} else {
				currNode.setProperty(propertyName, value);
			}
		} else {
			currNode.setProperty(propertyName, value);
		}
	}

	protected Node getNode(String name, GraphDatabaseService g){
		Node result = nodeCache.get(name);

		if(result == null){
			IndexHits<Node> hits = g.index().forNodes("matchable").get(MATCH, name);

			if(hits.size() > 1){
				log.warning("query for name = " + name + " returned more than one hit. Defaulting to first.");
			}

			result = hits.getSingle();
			nodeCache.put(name,result);
		}

		return result;
	}

}
