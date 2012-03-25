/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.debug.launching.ui.aca;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

public class DseMainTab extends AbstractLaunchConfigurationTab
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

	protected WidgetListener fListener = new WidgetListener();
	private Text fBaseLaunchConfigNameText;
	private Button checkBoxShowOctavePlot = null;

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		createInterperterGroupCheckGroup(comp);
		createExtendableContent(comp);
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
		Group group = new Group(controlGroup, SWT.NONE);
		group.setText("Base Configuration");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		fBaseLaunchConfigNameText = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fBaseLaunchConfigNameText.setLayoutData(gd);
		fBaseLaunchConfigNameText.addModifyListener(fListener);

		Button selectProjectButton = createPushButton(group, "Browse...", null);

		selectProjectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				class LaunchConfigContentProvider implements
						ITreeContentProvider
				{

					public void dispose()
					{

					}

					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput)
					{

					}

					public Object[] getElements(Object inputElement)
					{
						if (inputElement instanceof Object[])
						{
							return (Object[]) inputElement;
						}
						return null;
					}

					public Object[] getChildren(Object parentElement)
					{
						return null;
					}

					public Object getParent(Object element)
					{
						return null;
					}

					public boolean hasChildren(Object element)
					{
						return false;
					}
				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), DebugUITools.newDebugModelPresentation(), new LaunchConfigContentProvider());
				dialog.setTitle("Base Launch Configuration Selection");
				dialog.setMessage("Select a launch configuration:");
				dialog.setComparator(new ViewerComparator());

				try
				{
					// see class: LaunchConfigurationFilteredTree and LaunchConfigurationView for launchconfig display
					dialog.setInput(DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(getConfigurationType()));
				} catch (CoreException e1)
				{
					DestecsDebugPlugin.log(e1);
				}

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							&& dialog.getFirstResult() instanceof ILaunchConfiguration

					)
					{
						fBaseLaunchConfigNameText.setText(((ILaunchConfiguration) dialog.getFirstResult()).getName());
					}

				}
			}
		});
		
		
		
		checkBoxShowOctavePlot = new Button(group, SWT.CHECK);
		checkBoxShowOctavePlot.setText("Show plot automaticaly when the script runs");
		checkBoxShowOctavePlot.setSelection(false);
		checkBoxShowOctavePlot.addSelectionListener(fListener);

	}

	protected ILaunchConfigurationType getConfigurationType()
	{
		return getLaunchManager().getLaunchConfigurationType(IDebugConstants.ATTR_DESTECS_PROGRAM);
	}

	public String getName()
	{
		return "Main";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fBaseLaunchConfigNameText.setText(configuration.getAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, ""));
			checkBoxShowOctavePlot.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false));

		} catch (CoreException e)
		{
			if (DestecsDebugPlugin.DEBUG)
			{
				DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Error in aca launch configuration tab", e));
			}
		}

	}

	public IProject getProject()
	{
		if (!fBaseLaunchConfigNameText.getText().isEmpty())
		{
			ILaunchConfiguration baseConfig = null;

			try
			{
				for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
				{
					if (tmp.getName().equals(fBaseLaunchConfigNameText.getText()))
					{
						baseConfig = tmp;
					}
				}

				if (baseConfig != null)
				{
					String projectName = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
					return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				}
			} catch (CoreException e)
			{
				DestecsDebugPlugin.logError("Failed to find project in DseMainTab: "+fBaseLaunchConfigNameText.getText(), e);
			}
		}
		return null;
	}
	
	public ILaunchConfiguration getBaseLaunchConfig()
	{
		if (!fBaseLaunchConfigNameText.getText().isEmpty())
		{
			try
			{
				for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
				{
					if (tmp.getName().equals(fBaseLaunchConfigNameText.getText()))
					{
						return tmp;
					}
				}

			} catch (CoreException e)
			{
				DestecsDebugPlugin.log(e);
			}
		}
		return null;
	}
	
	
	public ILaunchConfiguration getSelectedBaseConfig()
	{
		if (!fBaseLaunchConfigNameText.getText().isEmpty())
		{
			ILaunchConfiguration baseConfig = null;

			try
			{
				for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
				{
					if (tmp.getName().equals(fBaseLaunchConfigNameText.getText()))
					{
						baseConfig = tmp;
					}
				}

				if (baseConfig != null)
				{
					return baseConfig;
				}
			} catch (CoreException e)
			{
				DestecsDebugPlugin.log( e);
			}
		}
		return null;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, fBaseLaunchConfigNameText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, checkBoxShowOctavePlot.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false);
	}

}
