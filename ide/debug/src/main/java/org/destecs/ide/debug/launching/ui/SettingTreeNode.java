package org.destecs.ide.debug.launching.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem;
import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem.ValueType;


public class SettingTreeNode implements Comparable<SettingTreeNode> {

	private String name;
	private String key;
	private boolean isVirtual;
	private List<SettingTreeNode> children = new Vector<SettingTreeNode>();
	
	private ValueType type = null;
	private List<String> possibleValues = null;
	private String value = null;
	
	public SettingTreeNode(String name, String key) {
		this.name = name;
		this.key = key;
		this.isVirtual = true;
	}
	
	public SettingTreeNode(String name, String key, ValueType type, List<String> possibleValues, String value) {
		this.name = name;
		this.key = key;
		this.isVirtual = false;
		this.possibleValues = possibleValues;
		this.value = value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addChild(SettingTreeNode child)
	{
		this.children.add(child);
	}
	
	
	public Object[] getChildren()
	{
		return children.toArray();
	}
	
	public String getKey() 
	{
		return key;
	}
	
	public String getName() 
	{
		return name;
	}
	

	public static SettingTreeNode createSettingsTree(Set<SettingItem> settingItems) 
	{
		List<SettingTreeNode> in = new Vector<SettingTreeNode>();
		SettingTreeNode root = new SettingTreeNode("root", "root");
		
		
		for (SettingItem settingItem : settingItems) {
			in.add(convertToSettingTreeNode(settingItem));
		}
		
		Collections.sort(in);
		
		for (SettingTreeNode settingTreeNode : in) {
			root.insertNodeInTree(settingTreeNode);
		}
		
		return root;
	}
	
	
	private void insertNodeInTree(SettingTreeNode node)
	{
		
		String[] key = node.getKey().split("\\.");
		
		List<String> keyList =  Arrays.asList(key);
		
		SettingTreeNode leaf = this.createPathToOption(keyList.subList(0, keyList.size()-1) );
		node.setName(keyList.get(keyList.size()-1));
		leaf.addChild(node);
		
	}
	
	private SettingTreeNode createPathToOption(List<String> keyList) {
		
		SettingTreeNode searched = this;
		
		for (String key : keyList) {
			SettingTreeNode child = searched.getChildByName(key);
			if(child == null)
			{
				child = new SettingTreeNode(key, key);
				searched.addChild(child);
				
			}
			searched = child;
				
		}
		
		return searched;
		
	}

	

	private SettingTreeNode getChildByName(String string) {
		
		
		for (SettingTreeNode child : this.children) {
			if(child.getName().equals(string))
				return child;
		}
		
		return null;
	}

	public static SettingTreeNode convertToSettingTreeNode(SettingItem item){
		return new SettingTreeNode( new String(item.key), new String(item.key), item.type, item.values, item.value);
	}

	public int compareTo(SettingTreeNode arg0) {
		return this.key.compareTo(arg0.getKey());
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("key: "); sb.append(this.key);
		sb.append(" Value: "); sb.append(this.value != null ? this.value : "no value"); 
		sb.append("\n");
		return sb.toString();
	}
	
}
