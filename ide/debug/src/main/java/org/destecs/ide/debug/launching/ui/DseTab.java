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
package org.destecs.ide.debug.launching.ui;

import java.io.File;

import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.SubsParserWrapper;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class DseTab extends AbstractLaunchConfigurationTab
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

		createFaultField(comp);
		createArchitectureGroup(comp);
		

	}

	private void createArchitectureGroup(Composite comp)
	{
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
				dialog.addFilter(new FileExtensionFilter(true,"arch"));
				dialog.setValidator(new ISelectionStatusValidator()
				{
					
					public IStatus validate(Object[] selection)
					{
						if (selection.length == 1 && selection[0] instanceof IFile)
						{
							return Status.OK_STATUS;
						}
						else return new Status(IStatus.ERROR,DestecsDebugPlugin.PLUGIN_ID,"Invalid selection. Selection must be a file.");
							
					}
				});

				for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
				{
					if (tab instanceof CoSimLaunchConfigurationTab)
					{
						CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
						IProject project = cosimLaunchTab.getProject();
						if (project != null)
						{
							dialog.setInput(project.getFolder("dse"));
						}
					}
				}
				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						fArchitecturePathText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());

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
		return "DSE";
	}

	@Override
	public String getId()
	{
		return "org.destecs.ide.debug.launching.ui.DseTab";
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
		try
		{
			String url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");

			replacePattern.setText(url);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error fetching faults from launch configuration", e);
		}
		removeArchitectureButton.setEnabled(!fArchitecturePathText.getText().isEmpty());
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, fArchitecturePathText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, replacePattern.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		if (replacePattern.getText().trim().length() > 0)
		{
			try
			{
				SubsParserWrapper parser = new SubsParserWrapper();
				parser.parse(new File("argument"), replacePattern.getText());
				for (IError errorMessage : parser.getErrors())
				{
					setErrorMessage(errorMessage.toString());
					break;
				}
			} catch (Exception e)
			{
				DestecsDebugPlugin.logError("Error while running subs parser to validate replace pattern", e);
			}
		}

		return super.isValid(launchConfig);
	}

	private Text replacePattern = null;

	public void createFaultField(Composite comp)
	{
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(comp.getFont());

		Group group = new Group(comp, comp.getStyle());
		group.setText("Faults");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("DE Replace pattern (A/B):");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		replacePattern = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		replacePattern.setLayoutData(gd);
		replacePattern.addModifyListener(fListener);

	}

}
