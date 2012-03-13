package org.destecs.ide.debug.launching.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem;
import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem.ValueType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


public class SettingTreeNode implements Comparable<SettingTreeNode> {

	public static String[] unwantedKeys = {"model.simulator.finishtime", "model.simulator.starttime", "model.simulator.modelname", "model.simulator.outputaftereach",
		 "model.simulator.modelexperimentname", "model.simulator.modelfilename"};
	
	private String name;
	private String key;
	private boolean isVirtual;
	private List<SettingTreeNode> children = new Vector<SettingTreeNode>();
	
	private ValueType type = null;
	private List<String> possibleValues = null;
	private String value = null;
	
	private boolean recovered = false;
	
	public SettingTreeNode(String name, String key,boolean recovered) {
		this.name = name;
		this.key = key;
		this.isVirtual = true;
		this.recovered = recovered;
	}
	
	public SettingTreeNode(String name, String key, boolean recovered, String value)
	{
		this.name = name;
		this.key = key;
		this.recovered = recovered;
		this.value = value;
	}
	
	public SettingTreeNode(String name, String key, ValueType type, List<String> possibleValues, String value) {
		this.name = name;
		this.key = key;
		this.isVirtual = false;
		this.type = type;
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
		SettingTreeNode root = new SettingTreeNode("root", "root", false);
		
		
		for (SettingItem settingItem : settingItems) {
			
			if(checkIfWanted(settingItem))
			{
				in.add(convertToSettingTreeNode(settingItem));
			}
		}
		
		Collections.sort(in);
		
		for (SettingTreeNode settingTreeNode : in) {
			root.insertNodeInTree(settingTreeNode);
		}
		
		return root;
	}
	
	public static SettingTreeNode createSettingsTreeFromConfiguration(Set<String[]> settingItems) 
	{
		List<SettingTreeNode> in = new Vector<SettingTreeNode>();
		SettingTreeNode root = new SettingTreeNode("root", "root",true);
		
		
		for (String[] settingItem : settingItems) {
			in.add(new SettingTreeNode(settingItem[0], settingItem[0], true, settingItem[1]));
		}
		
		Collections.sort(in);
		
		for (SettingTreeNode settingTreeNode : in) {
			root.insertNodeInTree(settingTreeNode);
		}
		
		return root;
	}
	
	
	private static boolean checkIfWanted(SettingItem settingItem) {
		for (String key : unwantedKeys) {
			if(settingItem.key.equals(key))
				return false;
		}
		return true;
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
				child = new SettingTreeNode(key, key,false);
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
		sb.append(" Type: "); sb.append(this.type != null ? this.type.toString() : "no type");
		sb.append(" Value: "); sb.append(this.value != null ? this.value : "no value"); 
		sb.append("\n");
		return sb.toString();
	}

	public void drawIn(Group optionsGroup) {
		Control[] children = optionsGroup.getChildren();
		
		for (Control control : children) {
			control.dispose();
		}
		
		if(isVirtual)
		{
			Label label = new Label(optionsGroup,SWT.NONE);
			label.setText("No Options");
		}
		else
		if(recovered)
		{
			Label label = new Label(optionsGroup,SWT.NONE);
			label.setText("Value: " + value);
			label = new Label(optionsGroup, SWT.WRAP);
			Color red = new Color(optionsGroup.getDisplay(), 255, 0, 0);
		    label.setForeground(red);
		    label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			label.setText("To edit, press the populate button." );
		}
		else
		{
			createOptionGUI(optionsGroup);
		}
		
		optionsGroup.layout();
	}

	private void createOptionGUI(Group optionsGroup) {

		switch (type) {
		case Bool:
			System.out.println("Creating Bool GUI");
			createBoolOptionGUI(optionsGroup);			
			break;
		case Enum:
		case Real:
		case RealPositive:
		case Double:
			System.out.println("Creating Double GUI");
			break;
		case String:
			System.out.println("Creating String GUI");
			createStringGUI(optionsGroup);
			break;
		case Unknown:
		default:
			break;
		}
		
	}

	private void createStringGUI(Group optionsGroup) {
		// TODO Auto-generated method stub
		
	}

	private void createBoolOptionGUI(Group optionsGroup) {
		
		String[] items = {"Yes","No"};
		final Combo combo = new Combo(optionsGroup, SWT.DROP_DOWN);
		combo.setItems(items);
		boolean b = Boolean.parseBoolean(value);
		if(b)
		{
			combo.select(0);
		}
		else
		{
			combo.select(1);
		}
		combo.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				if(combo.getSelectionIndex() == 0)
				{
					value = "true";
				}
				else
				{
					value = "false";
				}
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	
	public String toSettingsString()
	{
		StringBuffer sb = new StringBuffer();
		
		if(!this.isVirtual)
		{
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append(";");
		}
		
		
		for (SettingTreeNode child : children) {
			sb.append(child.toSettingsString());
		}
		
		return sb.toString();
	}

	public String getValueForKey(String key)
	{
		String result = null;
		
		//TODO:NOT Finished
		
		return result;
	}
	
}
