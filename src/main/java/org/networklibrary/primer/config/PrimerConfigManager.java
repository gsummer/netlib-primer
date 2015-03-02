package org.networklibrary.primer.config;

import org.networklibrary.core.config.ConfigManager;

public class PrimerConfigManager extends ConfigManager implements
		PrimerSettings {

	public PrimerConfigManager() {
		super();
	}
	
	public PrimerConfigManager(String runConfig) {
		super(runConfig);
	}
	
	/*
	 *	Primer settings 
	 */
	public void setDoIndex(boolean doIndex){
		getConfig().addProperty("do_index", doIndex);
	}
	
	public boolean doIndex(){
		return getConfig().getBoolean("do_index");
	}
	
	public void setDoArrays(boolean doArray){
		getConfig().addProperty("do_array", doArray);
	}
	
	public boolean doArrays(){
		return getConfig().getBoolean("do_array");
	}
	
	public void setIsLabel(boolean isLabel){
		getConfig().addProperty("is_label",isLabel);
	}
	
	public boolean isLabel(){
		return getConfig().getBoolean("is_label");
	}
	
	public void setIsProperty(boolean isProp){
		getConfig().addProperty("is_property",isProp);
	}
	
	public boolean isProperty(){
		return getConfig().getBoolean("is_property");
	}
	
	public void setNoNewNodes(boolean NewNodes){
		getConfig().addProperty("new_nodes", NewNodes);
	}
	
	public boolean newNodes(){
		return getConfig().getBoolean("new_nodes");
	}
}
