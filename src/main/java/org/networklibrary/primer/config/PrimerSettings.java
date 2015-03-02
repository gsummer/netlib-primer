package org.networklibrary.primer.config;

public interface PrimerSettings {

	public void setDoIndex(boolean doIndex);
	
	public boolean doIndex();
	
	public void setDoArrays(boolean doArray);
	
	public boolean doArrays();
	
	public void setIsLabel(boolean isLabel);
	
	public boolean isLabel();
	
	public void setIsProperty(boolean isProp);
	
	public boolean isProperty();
	
	public void setNoNewNodes(boolean NewNodes);
	
	public boolean newNodes();
}
