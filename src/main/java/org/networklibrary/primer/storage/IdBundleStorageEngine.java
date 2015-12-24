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
import org.networklibrary.core.config.Indexing;
import org.networklibrary.core.storage.MultiTxStrategy;
import org.networklibrary.core.types.IdData;
import org.networklibrary.primer.config.PrimerConfigManager;

public class IdBundleStorageEngine extends MultiTxStrategy<IdData> {

	protected static final Logger log = Logger.getLogger(IdBundleStorageEngine.class.getName());

	private Map<String,Set<Node>> nodeCache = new HashMap<String,Set<Node>>();

	private Index<Node> matchableIndex = null;
	
	public IdBundleStorageEngine(GraphDatabaseService graph,
			ConfigManager confMgr) {
		super(graph, confMgr);
		try ( Transaction tx = graph.beginTx() ){
			matchableIndex = graph.index().forNodes(getIndexing().getPrimaryIndex());
			tx.success();
		}
	}

	@Override
	protected void doStore(IdData curr) {
		Set<Node> currNodes = getNode(curr.getMatchID(), getGraph());

		if(currNodes == null) // multi hits; we are ignorning
			return;
		
		if(currNodes.isEmpty() && getConfig().newNodes()){
			Node currNode = getGraph().createNode();
			currNodes.add(currNode);
			
			if(!nodeCache.containsKey(curr.getMatchID())){
				nodeCache.put(curr.getMatchID(), new HashSet<Node>());
			}
			nodeCache.get(curr.getMatchID()).add(currNode);
			
			matchableIndex.add(currNode, getIndexing().getPrimaryKey(), curr.getMatchID());
		}
		
		for(Node currNode : currNodes){
			if(currNode != null){
				if(getConfig().isLabel()){
					currNode.addLabel(DynamicLabel.label(curr.getValue()));
				} 

				addProperty(currNode,curr.getPropertyName(),curr.getValue());
				if(getConfig().doIndex()){
					matchableIndex.add(currNode, getIndexing().getPrimaryKey(), curr.getValue());
				}
			}
		}
	}

	private void addProperty(Node currNode, String propertyName, String value) {

		if(currNode.hasProperty(propertyName)){
			if(getConfig().doArrays()){
				Set<String> values = new HashSet<String>();
				Object prop = currNode.getProperty(propertyName);
				if(prop instanceof String){
					values.add((String)prop);
				} else {
					String[] objs = (String[])currNode.getProperty(propertyName);
					values.addAll(Arrays.asList(objs));
				}
				values.add(value);

				if(values.size() == 1){
					currNode.setProperty(propertyName, value);
				} else {
					String[] newvalues = new String[values.size()];
					newvalues = values.toArray(newvalues);

					currNode.setProperty(propertyName, newvalues);
				}
				
			} else {
				currNode.setProperty(propertyName, value);
			}
		} else {
			currNode.setProperty(propertyName, value);
		}
	}

	protected Set<Node> getNode(String name, GraphDatabaseService g){
		Set<Node> result = nodeCache.get(name);

		if(result == null){
			IndexHits<Node> hits = matchableIndex.get(getIndexing().getPrimaryKey(), name);

			if(hits.size() > 1 && !getConfig().allowMultiNodes()){
				log.warning("query for name = " + name + " returned more than one hit. Ignoring.");
				return null;
			}

			result = new HashSet<Node>();
			while(hits.hasNext()){
				result.add(hits.next());
			}
			nodeCache.put(name,result);
		}

		return result;
	}

	protected PrimerConfigManager getConfig(){
		return (PrimerConfigManager)getConfMgr();
	}
	
	protected Indexing getIndexing(){
		return (Indexing)getConfMgr();
	}
	
}
