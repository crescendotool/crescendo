package org.destecs.ide.debug.launching.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.core.model.internal.AcaSimulationManager;
import org.destecs.ide.debug.core.model.internal.DestecsAcaDebugTarget;
import org.destecs.ide.simeng.actions.ISimulationControlProxy;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.progress.UIJob;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DestecsDirectoryLaunchDelegate implements ILaunchConfigurationDelegate
{

	private static final Object LAUNCH = "launch";

	public void launch(ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		//String baseLaunchName = launch.getLaunchConfiguration().getAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, "");
		
	
		
		String projectName = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		String folderPath = configuration.getAttribute(IDebugConstants.DESTECS_DIRECTORY_LAUNCH_FOLDER, "");
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String outputPreFix = dateFormat.format(new Date()) + "_"
				+ configuration.getName();
		
				
		final Set<ILaunchConfiguration> configurations = getConfingsFromFolder(project,folderPath,outputPreFix);
		if(configurations.size() == 0)
		{
			return;
		}
		
		IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		File base = dProject.getOutputFolder().getLocation().toFile();

		DestecsAcaDebugTarget acaTarget = new DestecsAcaDebugTarget(launch, project, new File(base, outputPreFix), configurations);
		launch.addDebugTarget(acaTarget);

		AcaSimulationManager manager = new AcaSimulationManager(acaTarget,monitor);
//		manager.start();
		acaTarget.setAcaSimulationManager(manager);

		UIJob listeners = new UIJob("Set Listeners")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final String engineViewId = IDebugConstants.ENGINE_VIEW_ID;
				final InfoTableView engineView = CoSimLaunchConfigurationDelegate.getInfoTableView(engineViewId);

				ISimulationControlProxy simulationControl = new ISimulationControlProxy()
				{

					public void terminate()
					{
						try
						{
							launch.terminate();
						} catch (DebugException e)
						{
							DestecsDebugPlugin.logError("Failed to terminate launch", e);
						}
					}

					public void pause()
					{
						// not supported
					}

					public void resume()
					{
						// not supported
					}
				};

				engineView.getTerminationAction().addSimulationControlProxy(simulationControl);
				engineView.getPauseAction().addSimulationControlProxy(simulationControl);
				engineView.getResumeAction().addSimulationControlProxy(simulationControl);

				return new Status(IStatus.OK, IDebugConstants.PLUGIN_ID, "Listeners OK");
			}
		};
		listeners.schedule();
manager.run();
		monitor.done();
	}

	private Set<ILaunchConfiguration> getConfingsFromFolder(IProject project,
			String folderPath, String outputPreFix) throws CoreException 
	{
		Set<ILaunchConfiguration> result = new HashSet<ILaunchConfiguration>();
		
		IFolder folder = project.getFolder(folderPath);
		
		for (IResource resource : folder.members())
		{
			if(resource instanceof IFile)
			{
				IFile file = (IFile) resource;
				if(file.getFileExtension().equals(LAUNCH))
				{
					ILaunchConfiguration launchConfig;
					try
					{
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();						
			            DocumentBuilder db = dbf.newDocumentBuilder();			            
						Document doc = db.parse(file.getContents());						
		                Element docEle = doc.getDocumentElement();		               
						launchConfig = getConfigFromProps(file.getName(),docEle,outputPreFix);
						if(launchConfig != null)
						{
							result.add(launchConfig);
						}
					} catch (Exception e)
					{
						DestecsDebugPlugin.log(e);
					}
					
				}
			}
		}
		return result;
				
	}

	private ILaunchConfiguration getConfigFromProps(String fileName, Element docEle, String outputPreFix)
	{		
		String type = docEle.getAttribute("type");
		NodeList nodes = docEle.getChildNodes();
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(type);
		try
		{
			wc = configType.newInstance(null, fileName);
			
			for(int index = 0; index < nodes.getLength(); index++){
				Node node = nodes.item(index);
                if(node.getNodeName().equals("#text"))
                	continue;
				
                insertNodeInConfig(node,wc);             
            }
			wc.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
			config = wc;
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to recreate LaunchConfig", e);			
		}
		return config;
	}

		private void insertNodeInConfig(Node node,
			ILaunchConfigurationWorkingCopy wc)
	{
		String nodeType = node.getNodeName();
		NamedNodeMap attributes = node.getAttributes();
		Node keyNode = attributes.getNamedItem("key");
		String key = keyNode.getNodeValue();
		
		Node valueNode = attributes.getNamedItem("value");
		String value = valueNode.getNodeValue();
		
		if(nodeType.startsWith("string"))
		{
			wc.setAttribute(key, value);
		}
		else if(nodeType.startsWith("boolean"))
		{
			wc.setAttribute(key, Boolean.parseBoolean(value));
		}
		
	}

		protected static ILaunchConfigurationType getConfigurationType(String id)
		{
			return getLaunchManager().getLaunchConfigurationType(id);
		}
		
		protected static ILaunchManager getLaunchManager()
		{
			return DebugPlugin.getDefault().getLaunchManager();
		}

}
