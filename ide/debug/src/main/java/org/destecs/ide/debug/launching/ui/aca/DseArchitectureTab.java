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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class DseArchitectureTab extends AbstractLaunchConfigurationTab
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

	private final WidgetListener fListener = new WidgetListener();
	private Text fArchitecturePathText;
	private Button selectArchitecturePathButton;
	private Button removeArchitectureButton;

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		Group group = new Group(comp, comp.getStyle());
		group.setText("Controller Architecture");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("Architecture:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fArchitecturePathText = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fArchitecturePathText.setLayoutData(gd);
		fArchitecturePathText.addModifyListener(fListener);

		selectArchitecturePathButton = createPushButton(group, "Browse...", null);
		selectArchitecturePathButton.setEnabled(true);
		selectArchitecturePathButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				dialog.setTitle("Architecture Selection");
				dialog.setMessage("Select an architecture:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new ViewerFilter()
				{

					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element)
					{
						if (element instanceof IResource)
						{
							IResource res = (IResource) element;
							return (res.getParent() instanceof IProject && res.getName().equalsIgnoreCase("dse"))
									|| ((res instanceof IFolder || res instanceof IFile) && !(res.getParent() instanceof IProject));
						}
						return false;
					}
				});
				dialog.setValidator(new ISelectionStatusValidator()
				{

					public IStatus validate(Object[] selection)
					{
						if (selection.length == 1
								&& selection[0] instanceof IFolder)
						{
							return Status.OK_STATUS;
						} else
							return new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Invalid selection. Selection must be a folder.");

					}
				});

				for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
				{
					if (tab instanceof DseMainTab)
					{
						DseMainTab dseLaunchTab = (DseMainTab) tab;
						IProject project = dseLaunchTab.getProject();
						if (project != null)
						{
							dialog.setInput(project);
						}
					}
				}
				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						fArchitecturePathText.setText(((IResource) dialog.getFirstResult()).getProjectRelativePath().toString());

					}

				}
			}
		});

		removeArchitectureButton = createPushButton(group, "Remove", null);

		removeArchitectureButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fArchitecturePathText.setText("");
			}
		});
	}

	public String getName()
	{
		return "Architecture";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fArchitecturePathText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, ""));
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error fetching dse from launch configuration", e);
		}
		removeArchitectureButton.setEnabled(!fArchitecturePathText.getText().isEmpty());

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, fArchitecturePathText.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, "");
	}

}
