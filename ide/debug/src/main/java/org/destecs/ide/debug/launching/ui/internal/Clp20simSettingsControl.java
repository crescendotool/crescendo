package org.destecs.ide.debug.launching.ui.internal;

import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.IUpdatableTab;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class Clp20simSettingsControl
{

	private TreeViewer settingsTreeViewer;
	private Group optionsGroup;
	private SettingTreeNode settingsRootNode;
	private boolean isAca = false;

	/**
	 * Constructor for the class
	 * @param isAca indicate if the control is for the ACA tab or not
	 */
	public Clp20simSettingsControl(boolean isAca)
	{
		this.isAca = isAca;
	}
	
	/** 
	 * 	 Method used to create the initial control 
	 */
	public void createSettingsTable(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Settings");
		group.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		group.setLayoutData(gd);

		settingsTreeViewer = new TreeViewer(group, SWT.BORDER);
		settingsTreeViewer.setContentProvider(new SettingsTreeContentProvider());
		settingsTreeViewer.setLabelProvider(new SettingsTreeLabelProvider());
		settingsTreeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		optionsGroup = new Group(group, SWT.NONE);
		optionsGroup.setText("Options");
		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		settingsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{

				if (event.getSelection().isEmpty())
				{
					return;
				}

				if (event.getSelection() instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					Object selected = selection.getFirstElement();
					if (selected instanceof SettingTreeNode)
					{
						SettingTreeNode node = (SettingTreeNode) selected;
						if(isAca)
						{
							node.drawInAca(optionsGroup);
						}
						else
						{
							node.drawIn(optionsGroup);
						}
					}
				}
			}
		});

	}
	
	
	/**
	 *  Method to re-populate the control from a configuration
	 * @param configuration
	 */
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		resetOptionsGroup();
		try {
			/*
			 * getting the settings
			 */
			String settings = null;
			
			if(isAca)
			{
				settings = configuration.getAttribute(IDebugConstants.DESTECS_ACA_20SIM_SETTINGS,"");
			}
			else
			{
				settings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");
			}
			
			
			String[] splitSettings = settings.split(";");
			
			Set<String[]> settingsSet = new HashSet<String[]>();
			
			for (String setting : splitSettings) {
				String[] splitSetting = setting.split("=");
				if(splitSetting.length == 2)
				{
					settingsSet.add(splitSetting);
				}
			}
			
			/*
			 * 	getting the implementations
			 */
			if(isAca)
			{
				settings = configuration.getAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS,"");
			}
			else
			{
				settings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, "");	
			}
			
			splitSettings = settings.split(";");
			
			for (String setting : splitSettings) {
				String[] splitSetting = setting.split("=");
				if(splitSetting.length == 2)
				{
					splitSetting[0] = IDebugConstants.IMPLEMENTATION_PREFIX + splitSetting[0];
					settingsSet.add(splitSetting);
				}
			}
			
			if(isAca)
			{
				settingsRootNode = SettingTreeNode.createAcaSettingsTreeFromConfiguration(settingsSet);
			}
			else
			{
				settingsRootNode = SettingTreeNode.createSettingsTreeFromConfiguration(settingsSet);	
			}
			
			settingsTreeViewer.setInput(settingsRootNode);
			settingsTreeViewer.refresh();
			settingsTreeViewer.expandToLevel(2);
			
		} catch (CoreException e) {
			DestecsDebugPlugin.logWarning("Failed to initialize Clp20SimTab with log variables", e);
		}
	}
	

	/**
	 * 
	 * @param configuration
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{		
		if(isAca)
		{
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS, settingsRootNode.toImplementationAcaString());
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_SETTINGS, settingsRootNode.toSettingsAcaString());
		}
		else
		{
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, getSettingsString());
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, getImplementationsString());	
		}
		
		
	}
	
	private String getImplementationsString() {

		Object rootSettingsNode = settingsTreeViewer.getInput();
		
		if(rootSettingsNode instanceof SettingTreeNode)
		{
			String result = ((SettingTreeNode) rootSettingsNode).toImplementationString();
			return result;
		}
		
		return "";
	}

	private String getSettingsString() {
		
		Object rootSettingsNode = settingsTreeViewer.getInput();
		
		if(rootSettingsNode instanceof SettingTreeNode)
		{
			String result = ((SettingTreeNode) rootSettingsNode).toSettingsString();
			return result;
		}
		
		return "";
		
	}


	public void populateControl(Set<SettingItem> settingItems, IUpdatableTab tab)
	{
		settingsRootNode = SettingTreeNode.createSettingsTree(settingItems,settingsRootNode,tab);		
	}


	public void refreshInputAndExpand()
	{
		if (optionsGroup != null)
		{
			for (Control map : optionsGroup.getChildren())
			{
				map.dispose();
			}
			optionsGroup.layout();
		}
		
		if (settingsTreeViewer != null)
		{
			settingsTreeViewer.setInput(settingsRootNode);
			settingsTreeViewer.refresh();
			settingsTreeViewer.expandToLevel(2);
		}
	}

	private void resetOptionsGroup()
	{
		if(optionsGroup == null)
		{
			return;
		}
		
		for (Control control : optionsGroup.getChildren())
		{
			control.dispose();
		}		
		Label label = new Label(optionsGroup,SWT.NONE);
		label = new Label(optionsGroup, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		label.setText("No element selected");
		optionsGroup.layout();
	}


	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		if(isAca)
		{
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS, "");
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_SETTINGS, "");
		}
		else
		{
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, "");
		}
		resetOptionsGroup();
		
	}
	
}
