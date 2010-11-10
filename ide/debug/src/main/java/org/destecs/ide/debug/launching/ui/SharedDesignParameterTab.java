package org.destecs.ide.debug.launching.ui;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.core.utility.FileUtility;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SharedDesignParameterTab extends AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{

		public void modifyText(ModifyEvent e)
		{
			// if(!suspended)
			{
				// validatePage();
				updateLaunchConfigurationDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				/* do nothing */
			}
		}

		public void widgetSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				// fOperationText.setEnabled(!fdebugInConsole.getSelection());

				updateLaunchConfigurationDialog();
			}
		}
	}

	private IProject project;
	private IFile sdpFile;
	private Table table;
	private HashMap<String, Object> shareadDesignParameters = new HashMap<String, Object>();
	private WidgetListener fListener = new WidgetListener();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		// parent.setLayout(new GridLayout());
		table = new Table(comp, SWT.FULL_SELECTION | SWT.VIRTUAL);// SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		// table.setItemCount(10);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);

		String[] titles = { "Name", "Value" };
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(100);
		}

		final TableEditor editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		table.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent me)
					{
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(EDITABLECOLUMN, text.getText());
						setDirty(true);
						updateLaunchConfigurationDialog();
					}
				});
				newEditor.addModifyListener(fListener);

				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);

			}
		});

		populate();
	}

	private void populate()
	{
		table.removeAll();
		if (shareadDesignParameters != null)
		{
			for (String p : shareadDesignParameters.keySet())
			{
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, p);
				item.setText(1, shareadDesignParameters.get(p).toString());
			}
		}
		// table.redraw();

	}

	public String getName()
	{
		return "Shared Design Parameters";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			String projectName = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
			if (projectName != null && projectName.trim().length() > 0)
			{
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			} else
			{
				return;
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		if (project != null)
		{
			IResource[] projectMembers;
			try
			{
				projectMembers = project.members();

				for (IResource iResource : projectMembers)
				{
					if (iResource instanceof IFile)
					{
						IFile file = (IFile) iResource;

						if (file.getFileExtension().equals("sdp"))
						{
							sdpFile = file;
							populateTable(sdpFile);
						}
					}

				}
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void populateTable(IFile sdpFile2)
	{
		shareadDesignParameters = new HashMap<String, Object>();

		try
		{
			sdpFile2.refreshLocal(IResource.DEPTH_ONE, null);
			// props.load(sdpFile2.getContents());
			shareadDesignParameters = new SdpParserWrapper().parse(sdpFile2.getLocation().toFile(), new String(FileUtility.getCharContent(FileUtility.getContent(sdpFile2))));

			// for (Object key : result.keySet())
			// {
			// // String name = key.toString();
			// // Double value = Double.parseDouble(props.getProperty(name));
			// shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, value));
			// }

			if (table != null)
			{
				populate();
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getId()
	{

		return "org.destecs.ide.debug.launching.ui.SharedDesignParameterTab";
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		Writer out;
		try
		{
			out = new OutputStreamWriter(new FileOutputStream(sdpFile.getLocation().toFile()));

			try
			{
				for (TableItem item : table.getItems())
				{
					if (item.getText().trim().length() > 0)
					{
						out.write(item.getText(0) + "=" + item.getText(1)
								+ "\n");
					}
				}
			} finally
			{
				out.close();
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		// TODO Auto-generated method stub
		return super.isValid(launchConfig);
	}

}
