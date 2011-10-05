package org.destecs.ide.debug.launching.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.types.ClassType;

public class VdmTab extends AbstractLaunchConfigurationTab
{

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	public static class LogVariableSelectionManager
	{
		final List<List<String>> variables = new Vector<List<String>>();

		public boolean isChecked(TreeItem item)
		{
			for (List<String> variable : variables)
			{
				if (!isRootItem(item) && checkName(item, variable))
				{
					return true;
				}
			}
			return false;
		}

		public void selectionChanged(TreeItem item)
		{
			boolean found = false;
			List<String> mustBeRemoved = null;
			for (List<String> variable : variables)
			{
				if (checkName(item, variable))
				{
					// ok this is the variable for this item
					found = true;
					if (!item.getChecked())
					{
						// mark for removal
						mustBeRemoved = variable;
					}
				}
			}

			if (found && mustBeRemoved != null)
			{
				variables.remove(mustBeRemoved);
			}
			if (!found && item.getChecked())
			{
				List<String> newVariable = createVariable(item);
				if (!newVariable.isEmpty())
				{
					variables.add(newVariable);
				}
			}
		}

		private List<String> createVariable(TreeItem item)
		{
			List<TreeItem> roots = Arrays.asList(item.getParent().getItems());
			List<String> variable = new Vector<String>();
			TreeItem tmp = item;
			while (!roots.contains(tmp))
			{
				variable.add(tmp.getText());
				tmp = tmp.getParentItem();
			}
			Collections.reverse(variable);
			return variable;
		}

		private boolean isRootItem(TreeItem item)
		{
			
			return Arrays.asList(item.getParent().getItems()).contains(item);
		}

		private boolean checkName(TreeItem item, List<String> names)
		{			
			
			if (isRootItem(item) && names.isEmpty())
			{
				return true;
			}

			return item.getText().endsWith(names.get(names.size() - 1))
					&& checkName(item.getParentItem(), names.subList(0, names.size() - 1));
		}

		@Override
		public String toString()
		{
			return toString("\n");
		};

		public String getConfigValue()
		{
			String tmp = toString(",");
			if (tmp.endsWith(","))
			{
				tmp = tmp.substring(0, tmp.length() - 1);
			}
			return tmp;
		}

		public void parseConfigValue(String config)
		{
			this.variables.clear();
			if (config.isEmpty())
			{
				return;
			}

			String[] tmps = config.split(",");
			for (String string : tmps)
			{
				String[] names = string.split("\\.");
				if (names.length > 0)
				{
					boolean okToAdd = true;
					for (String string2 : names)
					{
						okToAdd = okToAdd && !string2.trim().isEmpty();
					}

					if (okToAdd)
					{
						this.variables.add(Arrays.asList(names));
					}
				}
			}
		}

		public String toString(String splitter)
		{
			StringBuffer sb = new StringBuffer();
			for (List<String> var : variables)
			{
				Iterator<String> itr = var.iterator();
				while (itr.hasNext())
				{
					sb.append(itr.next());
					if (itr.hasNext())
					{
						sb.append(".");
					}
				}
				sb.append(splitter);
			}
			return sb.toString();
		}
	}

	protected WidgetListener fListener = new WidgetListener();
	private Button checkBoxUsePostChecks = null;
	private Button checkBoxUsePreChecks = null;
	private Button checkBoxInvChecks = null;
	private Button checkBoxDynamicTypeChecks = null;
	private Button checkBoxUseMeasure = null;
	private Button checkBoxUseCoverage = null;
	private Button checkBoxUseLogRt = null;

	private Tree logTree = null;
	private LogVariableSelectionManager logManager = new LogVariableSelectionManager();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		createInterperterGroupCheckGroup(comp);
		createLogGroup(comp);
		createExtendableContent(comp);
	}

	private void createLogGroup(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		GridData gd = new GridData(GridData.FILL_BOTH);

		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		logTree = new Tree(group, SWT.CHECK | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data = new GridData(GridData.FILL_BOTH);
		logTree.setLayoutData(data);
		logTree.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.detail == SWT.CHECK)
				{
					if(!Arrays.asList(logTree.getChildren()).contains((TreeItem)event.item))
					{
						checkAndExpand((TreeItem) event.item);
					}
					
					logManager.selectionChanged(((TreeItem) event.item));
					updateLaunchConfigurationDialog();
				}
			}

			
		});
		logTree.addListener(SWT.Expand, new Listener()
		{
			public void handleEvent(final Event event)
			{
				final TreeItem root = (TreeItem) event.item;
				TreeItem[] items = root.getItems();
				for (int i = 0; i < items.length; i++)
				{
					if (items[i].getData() != null)
					{
						return;
					} else
					{
						items[i].dispose();
					}
				}
				IAstNode node = (IAstNode) root.getData();

				if (node instanceof ClassDefinition)
				{
					ClassDefinition c = (ClassDefinition) node;
					expandClassDefinition(c, root);

				} else if (node instanceof InstanceVariableDefinition)
				{
					InstanceVariableDefinition instance = (InstanceVariableDefinition) node;
					if (instance.type instanceof ClassType)
					{
						expandClassDefinition(((ClassType) instance.type).classdef, root);
					}
				}
			}
		});
	}

	private void checkAndExpand(TreeItem root) {
		
		boolean isChecked = root.getChecked();
		
		TreeItem[] items = root.getItems();
		for (TreeItem treeItem : items) {
			treeItem.setChecked(isChecked);			
			logManager.selectionChanged(treeItem);			
			checkAndExpand(treeItem);
		}
		
	}
	
	private void expandClassDefinition(ClassDefinition c, TreeItem root)
	{
		for (Definition def : c.getDefinitions())
		{
			if (def instanceof InstanceVariableDefinition)
			{
				InstanceVariableDefinition instance = (InstanceVariableDefinition) def;
				if (instance.type instanceof ClassType)
				{
					ClassDefinition internalType = ((ClassType) instance.type).classdef;
					if (internalType instanceof CPUClassDefinition
							|| internalType instanceof BUSClassDefinition)
					{
						continue;
					}
				}

				TreeItem item = new TreeItem(root, 0);
				item.setText(def.getName());
				item.setData(def);

				if (logManager.isChecked(item))
				{
					item.setChecked(true);
				}

				if (instance.type instanceof ClassType)
				{
					new TreeItem(item, 0);
				}
			}
		}
	}

	/**
	 * Enables sub classes to add groups to the existing view
	 * 
	 * @param comp
	 */
	protected void createExtendableContent(Composite comp)
	{

	}

	void createInterperterGroupCheckGroup(Composite controlGroup)
	{
		Group interperterGroup = new Group(controlGroup, SWT.NONE);
		interperterGroup.setText("Interpreting");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		interperterGroup.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		interperterGroup.setLayout(layout);

		checkBoxDynamicTypeChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxDynamicTypeChecks.setText("Dynamic type checks");
		checkBoxDynamicTypeChecks.addSelectionListener(fListener);

		checkBoxInvChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxInvChecks.setText("Invariants checks");
		checkBoxInvChecks.addSelectionListener(fListener);

		checkBoxUsePreChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePreChecks.setText("Pre condition checks");
		checkBoxUsePreChecks.addSelectionListener(fListener);

		checkBoxUsePostChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePostChecks.setText("Post condition checks");
		checkBoxUsePostChecks.addSelectionListener(fListener);

		checkBoxUseMeasure = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseMeasure.setText("Measure Run-Time checks");
		checkBoxUseMeasure.addSelectionListener(fListener);

		checkBoxUseCoverage = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseCoverage.setText("Generate Coverage");
		checkBoxUseCoverage.addSelectionListener(fListener);

		checkBoxUseLogRt = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseLogRt.setText("Log Real-Time Events");
		checkBoxUseLogRt.addSelectionListener(fListener);

	}

	public String getName()
	{
		return "VDM Options";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			checkBoxDynamicTypeChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true));
			checkBoxInvChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true));
			checkBoxUsePostChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true));
			checkBoxUsePreChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true));
			checkBoxUseMeasure.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true));
			checkBoxUseCoverage.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true));
			checkBoxUseCoverage.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true));
			logManager.parseConfigValue(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_LOG_VARIABLES, ""));

		} catch (CoreException e)
		{
			if (DestecsDebugPlugin.DEBUG)
			{
				DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Error in vdmruntimechecks launch configuration tab", e));
			}
		}

		IProject project = getProject();
		if (project == null)
		{
			return;
		}
		IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);
		for (TreeItem c : logTree.getItems())
		{
			c.dispose();

		}
		logTree.removeAll();

		if (p != null)
		{
			IVdmModel model = p.getModel();
			for (IAstNode elem : model.getRootElementList())
			{
				if (elem instanceof SystemDefinition)
				{
					TreeItem root = new TreeItem(logTree, 0);
					root.setText(elem.toString());
					root.setData(elem);
					new TreeItem(root, 0);
				}
			}
		}
	}

	public IProject getProject()
	{
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				return ((CoSimLaunchConfigurationTab) tab).getProject();
			}
		}
		return null;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, checkBoxDynamicTypeChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, checkBoxInvChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, checkBoxUsePostChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, checkBoxUsePreChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, checkBoxUseMeasure.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, checkBoxUseCoverage.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, checkBoxUseLogRt.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_LOG_VARIABLES, logManager.getConfigValue());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true);
	}

}
