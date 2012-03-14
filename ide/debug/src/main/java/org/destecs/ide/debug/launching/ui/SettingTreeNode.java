package org.destecs.ide.debug.launching.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem;
import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem.ValueType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class SettingTreeNode implements Comparable<SettingTreeNode> {

	public static String[] unwantedKeys = {"model.simulator.finishtime", "model.simulator.starttime", "model.simulator.modelname", "model.simulator.outputaftereach",
		 "model.simulator.modelexperimentname", "model.simulator.modelfilename"};
	
	private String name;
	private String key;
	private boolean isVirtual;
	private List<SettingTreeNode> children = new Vector<SettingTreeNode>();
	
	private ValueType type = null;
	private List<String> enumerations = null;
	private String value = null;
	
	
	private boolean implementation = false;
	private boolean recovered = false;

	private Clp20simTab tab;

	private HashMap<String, String> properties;
	
	public SettingTreeNode(String name, String key,boolean recovered) {
		this.name = name;
		this.key = key;
		this.isVirtual = true;
		this.recovered = recovered;
		if(this.key.contains("implementation"))
		{
			this.implementation = true;
		}
	}
	
	public SettingTreeNode(String name, String key, boolean recovered, String value)
	{
		this.name = name;
		this.key = key;
		this.recovered = recovered;
		this.value = value;
		if(this.key.contains("implementation"))
		{
			this.implementation = true;
		}
	}
	
	public SettingTreeNode(String name, String key, ValueType type, List<String> enumerations, String value, HashMap<String, String> properties) {
		this.name = name;
		this.key = key;
		this.isVirtual = false;
		this.type = type;
		this.enumerations = enumerations;
		this.value = value;
		this.properties = properties;
		if(this.key.contains("implementation"))
		{
			this.implementation = true;
		}
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
	

	public static SettingTreeNode createSettingsTree(Set<SettingItem> settingItems, SettingTreeNode oldSettingsNodeTree, Clp20simTab tab) 
	{
		List<SettingTreeNode> in = new Vector<SettingTreeNode>();
		SettingTreeNode root = new SettingTreeNode("root", "root", false);
		root.setTab(tab);
		
		for (SettingItem settingItem : settingItems) {
			
			if(checkIfWanted(settingItem))
			{
				SettingTreeNode newNode = convertToSettingTreeNode(settingItem);
				String oldValue = oldSettingsNodeTree.getValueForKey(newNode.getKey());
				if(oldValue != null)
				{
					newNode.value = oldValue;
				}
				newNode.setTab(tab);
				in.add(newNode);
			}
		}
		
		Collections.sort(in);
		
		for (SettingTreeNode settingTreeNode : in) {
			root.insertNodeInTree(settingTreeNode);
		}
		
		return root;
	}
	
	private void setTab(Clp20simTab tab) {
		this.tab = tab;
		
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
		return new SettingTreeNode( new String(item.key), new String(item.key), item.type, item.enumerations, item.value,item.propertiesMap);
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
			label = new Label(optionsGroup, SWT.WRAP);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			label.setText("No Options. This is a virtual node which contains no options.");
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
			createBoolOptionGUI(optionsGroup);			
			break;
		case Enum:
			System.out.println("Creating Enum GUI");
			break;
		case Real:
			System.out.println("Creating Real GUI");
			break;
		case RealPositive:
			System.out.println("Creating RealPositive GUI");
			break;
		case Double:
			createDoubleGUI(optionsGroup);
			break;
		case String:
			createStringGUI(optionsGroup);
			break;
		case Unknown:
		default:
			break;
		}
		
	}

	private void createDoubleGUI(Group optionsGroup) {
		
		final Composite c = new Composite(optionsGroup, SWT.FILL);
		
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,1));
		Label label = new Label(c,SWT.NONE);
		label.setText("Value: " );
		
		final Text textInput = new Text(c, SWT.BORDER | SWT.FILL);
		textInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		textInput.setText(this.value);
		
		final Label warningLabel = new Label(c, SWT.NONE);
		
		Color red = new Color(c.getDisplay(), 255, 0, 0);
	    warningLabel.setForeground(red);
	    warningLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
	    
	    if(properties.size() > 0)
	    {
	    	label = new Label(c,SWT.NONE);
	    	label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
	    	label.setText("Constrains:");
	    	for (String property : properties.keySet()) {
				
	    		if(property.equals("lowerbound"))
	    		{
	    			label = new Label(c,SWT.NONE);
	    	    	label.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
	    	    	label.setText("Value must be larger than " + properties.get(property));
	    		}
			}
	    }
	    
	    
		textInput.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent event) {
				String stringValue = textInput.getText();
				
				try
				{
					double dValue = Double.parseDouble(stringValue);
					warningLabel.setText("");
					if(checkConstrains(dValue))
					{
						value = stringValue;
						tab.updateTab();
					}
					else
					{
						warningLabel.setText("Value does not respect the constrains");
					}
					
					c.layout();
				}
				catch (NumberFormatException ex) {
					warningLabel.setText("Input value is not a real number");
					c.layout();
				}
			}

			
		});
		
	}

	private boolean checkConstrains(double dValue) {
		boolean result = true;
		
		if(properties.size() > 0)
		{
			for (String property : properties.keySet()) {
				if(property.equals("lowerbound"))
				{
					result &= dValue > Double.parseDouble(properties.get(property));   
				}
			}
			
			
		}
		
		return result;
	}
	
	private void createStringGUI(Group optionsGroup) {
		
		if(enumerations.size() > 0)
		{
			String[] items = new String[enumerations.size()];
			final Combo combo = new Combo(optionsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			
			combo.setItems(enumerations.toArray(items));
			combo.select(enumerations.indexOf(value));
			combo.addSelectionListener(new SelectionListener() {
				
				public void widgetSelected(SelectionEvent e) {
					value = combo.getItem(combo.getSelectionIndex());
					tab.updateTab();
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					
				}
			});
		}
		
	}

	private void createBoolOptionGUI(Group optionsGroup) {
		
		String[] items = {"Yes","No"};
		final Combo combo = new Combo(optionsGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
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
				tab.updateTab();
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		
	}
	
	public String toSettingsString()
	{
		StringBuffer sb = new StringBuffer();
		
		if(!(this.isVirtual || this.implementation))
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
	
	public String toImplementationString()
	{
		StringBuffer sb = new StringBuffer();
		
		if(this.implementation && !this.isVirtual)
		{
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append(";");
		}
		

		for (SettingTreeNode child : children) {
			sb.append(child.toImplementationString());
		}
		
		return sb.toString();
	}

	public String getValueForKey(String key)
	{
		
		if(this.key.equals(key))
		{
			return this.value;
		}
		
		for (SettingTreeNode node : this.children) {
			String result = node.getValueForKey(key);
			if(result != null)
			{
				return result;
			}
		}
		
		return null;
	}
	
}
