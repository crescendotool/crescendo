package org.destecs.ide.debug.launching.ui.aca;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ArchitectureSelectionTab extends AbstractAcaTab
		implements ILaunchConfigurationTab {

	
	

	private Table architecturesTable;
	private Label warningLabel;
	private Button createFolderButton;

	public void createControl(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createArchitectureFolderGroup(comp);
		createArchitectureSelectionGroup(comp);
	}

	private void createArchitectureSelectionGroup(Composite comp) {
		Group group = new Group(comp, comp.getStyle());
		group.setText("Architectures selection");
		GridData gd = new GridData(GridData.FILL_BOTH);
		group.setLayoutData(gd);
		
		group.setLayout(new GridLayout(1, true));
		
		architecturesTable = new Table(group, SWT.CHECK);
	    architecturesTable.setSize(100, 100);
	    architecturesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	    architecturesTable.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
	         updateLaunchConfigurationDialog();
	        }
	      });
		
	}

	private void createArchitectureFolderGroup(Composite comp) {
		Group group = new Group(comp, comp.getStyle());
		group.setText("Architectures folder (\"model_de/architectures\")");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(2, false));
		
		
		createFolderButton = createPushButton(group, "Create Architectures Folder", null);
		createFolderButton.setEnabled(false);
		createFolderButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				IProject project = getActiveProject();
				if(project == null)
				{
					//this should not happen
					System.out.println("this should not happen");
				}
				else
				{
					boolean folderCreated = false;
					IPath architecturesPath = project.getLocation().append("model_de/architectures");
					File architecturesFile = architecturesPath.toFile();
					if(true)
					{
						folderCreated = architecturesFile.mkdirs();
						try {
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (CoreException e1) {
							DestecsDebugPlugin.logError("Failed to refresh project", e1);
						}
					}
					
					if(folderCreated)
					{
						createFolderButton.setEnabled(false);
						warningLabel.setText("Folder present");	
					}
					
				}
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("widgetDefaultSelected");
				
			}
		});
		
		warningLabel = new Label(group, SWT.MIN);
		
		
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_ARCHITECTURES, "");

	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		String architectureString = "";
		try {
			 architectureString = configuration.getAttribute(IDebugConstants.DESTECS_ACA_ARCHITECTURES,"");
		} catch (CoreException e1) {
			DestecsDebugPlugin.logError("Failed get aca architectures attribute", e1);
		}
		
		String[] architectures = architectureString.split(";");
		List<String> architecturesList = Arrays.asList(architectures);
		
		
		IProject project = getActiveProject();
		
		if(project == null)
		{
			//setErrorMessage("No project is selected");
			//System.out.println("[Architectures] No base config is selected");
		}
		else
		{
			architecturesTable.removeAll(); 
			
			//setErrorMessage(null);
			System.out.println("[Architectures] Base config is selected");
			IResource architecturesDirectory = project.findMember("model_de/architectures");
			if(architecturesDirectory == null)
			{
				warningLabel.setText("Folder not found");
				createFolderButton.setEnabled(true);
			}
			else
			{
				warningLabel.setText("Folder present");
				if(architecturesDirectory instanceof IFolder)
				{
					IFolder architectureFolder = (IFolder) architecturesDirectory;
					try {
						for (IResource element : architectureFolder.members()) {
							if(element instanceof IFile)
							{
								IFile iFile = (IFile) element;
								if(iFile.getFileExtension().equals("arch"))
								{
									TableItem item = new TableItem(architecturesTable, SWT.NONE);
								     item.setText(iFile.getName());
								     item.setData(iFile);
								     if(architecturesList.contains(iFile.getName()))
								     {
								    	 item.setChecked(true);
								     }
								}
							}
						}
					} catch (CoreException e) {
						DestecsDebugPlugin.logError("Failed to get members from "+architectureFolder, e);
					} 
				}
				
			}
			
		}
			
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		StringBuffer sb = new StringBuffer();
		
		for (TableItem tItem : architecturesTable.getItems()) {
			if(tItem.getChecked())
			{
				sb.append(tItem.getText() + ";");
			}
		}
		
		if(sb.length() > 0)
		{
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_ARCHITECTURES, sb.substring(0, sb.length()-1));
		}
		else
		{
			configuration.setAttribute(IDebugConstants.DESTECS_ACA_ARCHITECTURES, "");
		}

	}

	public String getName() {
		return "Architectures";
	}

	
	
}
