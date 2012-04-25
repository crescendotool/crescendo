package org.destecs.ide.debug.launching.ui.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.launching.ui.IUpdatableTab;

public class LogItemTree implements Comparable<LogItemTree>
{
	public static String[] unwantedKeys = {"time"};
	
	private String name = null;
	private String key = null;
	private boolean isVirtual = false;
	private List<LogItemTree> children = null; 
	private boolean isChecked = false;

	public LogItemTree(String name, String key, boolean isVirtual, boolean isChecked)
	{
		this.name = name;
		this.key = key;
		this.isVirtual = isVirtual;
		this.isChecked = isChecked;
		children = new Vector<LogItemTree>();
	}
	
	
	public static LogItemTree buildTreeFromItems(Set<LogItem> itemsSet, LogItemTree oldLogItemTree, IUpdatableTab tab, boolean recovering)
	{
		List<LogItemTree> in = new Vector<LogItemTree>(); 
		
		
		for (LogItem item : itemsSet) {
			
			if(checkIfWanted(item))
			{
				LogItemTree newNode = new LogItemTree(item.name, item.name, false,recovering);
				if(oldLogItemTree != null)
				{
					LogItemTree oldTreeNode = oldLogItemTree.getValueForKey(newNode.getKey());
					if(oldTreeNode != null)
					{
						newNode.isChecked = oldTreeNode.isChecked;
					}
				}
				
//				newNode.setTab(tab);
				in.add(newNode);
			}
		}
		Collections.sort(in);
		LogItemTree root = new LogItemTree("root", "root", true,false);
		
		for (LogItemTree logItem : in)
		{
			root.insertItem(logItem);
		}
		
		return root;
	}


	public String getName() 
	{
		return name;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public boolean isVirtual()
	{
		return isVirtual;
	}
	
	public Object[] getChildren()
	{
		return children.toArray();
	}
	
	private static  boolean checkIfWanted(LogItem item)
	{
		for (String key : unwantedKeys) {
			if(item.name.equals(key))
				return false;
		}
		return true;
	}


	private void insertItem(LogItemTree logItem)
	{
		String[] key = logItem.name.split("\\.");
		
		List<String> keyList =  Arrays.asList(key);
		
		LogItemTree leaf = this.createPathToOption(keyList.subList(0, keyList.size()-1) );
		logItem.name = keyList.get(keyList.size()-1);
		leaf.children.add(logItem);
	}
	
	private LogItemTree createPathToOption(List<String> keyList) {
		
		LogItemTree searched = this;
		
		for (String key : keyList) {
			LogItemTree child = searched.getChildByName(key);
			if(child == null)
			{
				child = new LogItemTree(key, key,true,false);
				searched.children.add(child);
				
			}
			searched = child;
				
		}
		
		return searched;
		
	}

	private LogItemTree getChildByName(String string) {
		
		for (LogItemTree child : this.children) {
			if(child.name.equals(string))
				return child;
		}
		
		return null;
	}


	@Override
	public int compareTo(LogItemTree o)
	{
		return this.key.compareTo(o.key);
	}

	@Override
	public String toString()
	{
		return key;
	}


	public boolean isChecked()
	{
		return isChecked;
	}
	
	public LogItemTree getValueForKey(String key)
	{
		
		if(this.key.equals(key))
		{
			return this;
		}
		
		for (LogItemTree node : this.children) {
			LogItemTree result = node.getValueForKey(key);
			if(result != null)
			{
				return result;
			}
		}
		
		return null;
	}
}
