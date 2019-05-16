package jaicore.graphvisualizer.plugin.nodeinfo;

import java.util.List;

public class NodeInfo {

	private String mainNodeId;
	private List<String> parentNodeIds;
	private List<String> childrenNodeIds;
	private String nodeType;

	@SuppressWarnings("unused")
	private NodeInfo() {
		// for serialization purposes
	}

	public NodeInfo(String mainNodeId, List<String> parentNodeIds, List<String> childrenNodeIds, String nodeType) {
		super();
		this.mainNodeId = mainNodeId;
		this.parentNodeIds = parentNodeIds;
		this.childrenNodeIds = childrenNodeIds;
		this.nodeType = nodeType;
	}

	public String getMainNodeId() {
		return mainNodeId;
	}

	public List<String> getParentNodeIds() {
		return parentNodeIds;
	}

	public List<String> getChildrenNodeIds() {
		return childrenNodeIds;
	}

	public String getNodeType() {
		return nodeType;
	}

}