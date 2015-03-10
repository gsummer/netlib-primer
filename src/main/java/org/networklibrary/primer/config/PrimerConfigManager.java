package org.networklibrary.primer.config;

import org.networklibrary.core.config.ConfigManager;

public class PrimerConfigManager extends ConfigManager implements
		PrimerSettings {

	public PrimerConfigManager(String runConfig) {
		super(runConfig);
	}
	
	public PrimerConfigManager(String type, boolean label, boolean index, boolean array,
			boolean prop, boolean noNew, boolean allowMulti) {
		
		setType(type);
		setIsLabel(label);
		setDoIndex(index);
		setDoArrays(array);
		setIsProperty(prop);
		setNoNewNodes(noNew);
		setAllowMulti(allowMulti);
		
		load(null); // loads the default config.
	}

	protected void setAllowMulti(boolean allowMulti) {
		getConfig().addProperty("allow_multi", allowMulti);
	}
		
	protected void setDoIndex(boolean doIndex){
		getConfig().addProperty("do_index", doIndex);
	}
	protected void setDoArrays(boolean doArray){
		getConfig().addProperty("do_array", doArray);
	}
	
	protected void setIsProperty(boolean isProp){
		getConfig().addProperty("is_property",isProp);
	}
	
	protected void setIsLabel(boolean isLabel){
		getConfig().addProperty("is_label",isLabel);
	}
	
	protected void setNoNewNodes(boolean NewNodes){
		getConfig().addProperty("new_nodes", NewNodes);
	}
	
	protected void setType(String type){
		getConfig().addProperty("type", type);
	}
	
	/*
	 *	Primer settings 
	 */
	
	public boolean doIndex(){
		return getConfig().getBoolean("do_index");
	}
	
	public boolean doArrays(){
		return getConfig().getBoolean("do_array");
	}

	public boolean isLabel(){
		return getConfig().getBoolean("is_label");
	}
	
	public boolean isProperty(){
		return getConfig().getBoolean("is_property");
	}
	
	public boolean newNodes(){
		return getConfig().getBoolean("new_nodes");
	}
	
	public boolean allowMultiNodes(){
		return getConfig().getBoolean("allow_multi");
	}

	@Override
	public String getType() {
		return getConfig().getString("type");
	}

}
