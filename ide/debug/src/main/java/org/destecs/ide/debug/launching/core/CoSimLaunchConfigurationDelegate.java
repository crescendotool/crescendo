package org.destecs.ide.debug.launching.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.simulationengine.ScenarioSimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.launcher.VdmRtLauncher;
import org.destecs.core.simulationengine.listener.IProcessCreationListener;
import org.destecs.core.simulationengine.listener.ISimulationStartListener;
import org.destecs.core.simulationengine.model.CtModelConfig;
import org.destecs.core.simulationengine.model.DeModelConfig;
import org.destecs.core.simulationengine.model.ModelConfig;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.core.model.internal.CoSimulationThread;
import org.destecs.ide.debug.core.model.internal.DestecsDebugTarget;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.ide.simeng.internal.core.VdmRtBundleLauncher;
import org.destecs.ide.simeng.listener.EngineListener;
import org.destecs.ide.simeng.listener.ListenerToLog;
import org.destecs.ide.simeng.listener.MessageListener;
import org.destecs.ide.simeng.listener.SimulationListener;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

public class CoSimLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate
{

	private File deFile = null;
	private File ctFile = null;
	private File contractFile = null;
	private File scenarioFile = null;
	private String sharedDesignParam = null;
	private double totalSimulationTime = 0.0;
	private IProject project = null;
	private URL deUrl = null;
	private URL ctUrl = null;
	private boolean remoteDebug = false;
	private DestecsDebugTarget target;
	private ILaunch launch;
	private File outputFolder = null;
	private File deArchitectureFile;
	private String deReplacePattern;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		loadSettings(configuration);
		this.launch = launch;
		target = new DestecsDebugTarget(launch, project);
		this.launch.addDebugTarget(target);
		startSimulation();
	}

	private File getFileFromPath(IProject project, String path)
	{

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project))
		{
			return r.getLocation().toFile();
		}
		return null;
	}

	private void loadSettings(ILaunchConfiguration configuration)
	{
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, ""));

			contractFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CONTRACT_PATH, ""));
			deFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, ""));
			ctFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, ""));
			scenarioFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, ""));
			deArchitectureFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, ""));
			deReplacePattern = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");
			sharedDesignParam = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
			totalSimulationTime = Double.parseDouble(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "0"));

			String deUrlString = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
			if (deUrlString.length() == 0)
			{
				Integer freePort = VdmRtBundleLauncher.getFreePort();
				if (freePort == -1)
				{
					throw new Exception("No free port found for DE launch");
				}
				deUrl = new URL(IDebugConstants.DEFAULT_DE_ENDPOINT.replaceAll("PORT", freePort.toString()));
			} else
			{
				deUrl = new URL(deUrlString);
			}
			ctUrl = new URL(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT));

			remoteDebug = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Faild to load launch configuration attributes", e);
		} catch (MalformedURLException e)
		{
			DestecsDebugPlugin.logError("Faild to load launch configuration attributes (URL's)", e);
		} catch (Exception e)
		{
			DestecsDebugPlugin.logError("Faild to load launch configuration attributes (URL's)", e);
		}

		IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		File base = dProject.getOutputFolder().getLocation().toFile();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		outputFolder = new File(base, dateFormat.format(new Date()));

		if (!outputFolder.mkdirs())
		{
			outputFolder = null;
		}

	}

	private void startSimulation()
	{
		final List<InfoTableView> views = new Vector<InfoTableView>();
		final ListenerToLog log = getLog();

		try
		{

			SimulationEngine.eclipseEnvironment = true;
			final SimulationEngine engine = getEngine();

			UIJob listeners = new UIJob("Set Listeners")
			{

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					final String messageViewId = IDebugConstants.MESSAGE_VIEW_ID;
					final String engineViewId = IDebugConstants.ENGINE_VIEW_ID;
					final String simulationViewId = IDebugConstants.SIMULATION_VIEW_ID;

					final InfoTableView messageView = getInfoTableView(messageViewId);
					final InfoTableView engineView = getInfoTableView(engineViewId);
					final InfoTableView simulationView = getInfoTableView(simulationViewId);

					views.add(messageView);
					views.add(engineView);
					views.add(simulationView);

					engine.engineListeners.add(new EngineListener(engineView));
					engine.messageListeners.add(new MessageListener(messageView));
					engine.simulationListeners.add(new SimulationListener(simulationView));

					for (InfoTableView view : views)
					{
						view.refreshPackTable();
					}

					return new Status(IStatus.OK, IDebugConstants.PLUGIN_ID, "Listeners OK");
				}
			};

			listeners.schedule();
			engine.engineListeners.add(log);
			engine.messageListeners.add(log);
			engine.simulationListeners.add(log);
			engine.variablesSyncListeners.add(log);

			if (!remoteDebug)
			{
				File libSearchRoot = new File(project.getLocation().toFile(), "lib");
				engine.setDeSimulationLauncher(new VdmRtBundleLauncher(deFile, deUrl.getPort(), libSearchRoot));// new
			} else
			{
				deUrl = new URL(IDebugConstants.DEFAULT_DE_ENDPOINT.replaceAll("PORT", Integer.valueOf(8080).toString()));
				engine.setDeSimulationLauncher(new VdmRtLauncher(5000));
			}

			final int deDebugPort = findFreePort();
			engine.setDeModel(getDeModelConfig(project,deDebugPort));
			engine.setDeEndpoint(deUrl);

			engine.setCtSimulationLauncher(new Clp20SimProgramLauncher(ctFile));
			engine.setCtModel(new CtModelConfig(ctFile));
			engine.setCtEndpoint(ctUrl);

			engine.setOutputFolder(outputFolder);

			engine.addProcessCreationListener(new IProcessCreationListener()
			{
				public void processCreated(String name, Process p)
				{
					launch.addProcess(DebugPlugin.newProcess(launch, p, name));
				}
			});

			final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = loadSharedDesignParameters(sharedDesignParam);

			final Job vdm = new Job("launch vdm")
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						ILaunchConfiguration vdmLaunchConfig = getVdmLaunchConfig(deDebugPort);
						ILaunch vdmLaunch = vdmLaunchConfig.launch("debug", null);
						launch.addDebugTarget(vdmLaunch.getDebugTarget());
					} catch (CoreException e)
					{
						return new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, "Faild to launch VDM-RT debug", e);
					}
					return Status.OK_STATUS;
				}
			};
			
			
			engine.simulationStartListeners.add(new ISimulationStartListener()
			{
				
				public void simulationStarting(Simulator simulator)
				{
					if(simulator==Simulator.DE)
					{
						vdm.schedule();
					}
				}
			});

			CoSimulationThread simThread = new CoSimulationThread(engine, log, shareadDesignParameters, totalSimulationTime, target, views);
			target.setCoSimulationThread(simThread);
			simThread.start();

		} catch (Exception ex)
		{
			ex.printStackTrace();
			for (InfoTableView view : views)
			{
				view.refreshPackTable();
			}
			try
			{
				if (target != null)
				{
					target.terminate();
				} else
				{
					launch.terminate();
				}
			} catch (DebugException e)
			{
				DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Error launching", e));
			}
		}
	}

	private ModelConfig getDeModelConfig(IProject project2,int port)
	{
		final DeModelConfig model = new DeModelConfig();
		model.arguments.put(DeModelConfig.LOAD_REPLACE, deReplacePattern);
		model.arguments.put(DeModelConfig.LOAD_DEBUG_PORT, String.valueOf(port));

		if (deArchitectureFile!= null && deArchitectureFile.exists())
		{
			StringBuffer architecture =new StringBuffer();
			StringBuffer deploy=new StringBuffer();
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(deArchitectureFile));
				String str;
				boolean inArch = false;
				boolean inDeploy = false;
				while ((str = in.readLine()) != null)
				{
					str = str.trim();
					if(str.startsWith("-- ## Architecture ## --"))
					{
						inArch = true;
						inDeploy = false;
					}
					if(str.startsWith("-- ## Deployment ## --"))
					{
						inDeploy = true;
						inArch = false;
					}
					
					if(inArch)
					{
						architecture.append(str);
						architecture.append("\n");
					}
					if(inDeploy)
					{
						deploy.append(str);
						deploy.append("\n");
					}
				}
				in.close();
			} catch (IOException e)
			{
			}
			model.arguments.put(DeModelConfig.LOAD_ARCHITECTURE, architecture.toString());
			model.arguments.put(DeModelConfig.LOAD_DEPLOY, deploy.toString());
		}

		final IContentType vdmrtFileContentType = Platform.getContentTypeManager().getContentType(IDebugConstants.VDMRT_CONTENT_TYPE_ID);

		try
		{
			project2.accept(new IResourceVisitor()
			{

				public boolean visit(IResource resource) throws CoreException
				{
					if (resource instanceof IFile
							&& resource.getFileExtension() != null
							&& resource.getFileExtension().equals(DeModelConfig.LOAD_LINK))
					{
						model.arguments.put(DeModelConfig.LOAD_LINK, resource.getLocation().toFile().getAbsolutePath());
					}
					return true;
				}
			});
			
			
			IDestecsProject p = (IDestecsProject) project2.getAdapter(IDestecsProject.class);
			p.getVdmModelFolder().accept(new IResourceVisitor()
			{

				public boolean visit(IResource resource) throws CoreException
				{
					if (vdmrtFileContentType.isAssociatedWith(resource.getName()))
					{
						model.addSpecFile(resource.getLocation().toFile());
					}
					return true;
				}
			});
			return model;
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private ListenerToLog getLog()
	{
		try
		{
			return new ListenerToLog(outputFolder);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	private SimulationEngine getEngine() throws IOException
	{

		if (scenarioFile != null)
		{
			Scenario scenario = new ScenarioParserWrapper().parse(scenarioFile);
			return new ScenarioSimulationEngine(contractFile, scenario);
		} else
		{
			return new SimulationEngine(contractFile);
		}
	}

	private static List<SetDesignParametersdesignParametersStructParam> loadSharedDesignParameters(
			String sharedDesignParamData) throws Exception
	{
		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();

		SdpParserWrapper parser = new SdpParserWrapper();
		HashMap<String, Object> result = parser.parse(new File("memory"), sharedDesignParamData);

		for (Object key : result.keySet())
		{
			String name = key.toString();
			if (result.get(name) instanceof Double)
			{
				shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, (Double) result.get(name)));
			} else if (result.get(name) instanceof Integer)
			{
				shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, ((Integer) result.get(name)).doubleValue()));
			} else if (result.get(name) instanceof Boolean)
			{
				boolean r = ((Boolean) result.get(name)).booleanValue();
				Double val = Double.valueOf(0);
				if (r)
				{
					val = Double.valueOf(1);
				}
				shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, val));
			} else
			{
				throw new Exception("Design parameter type not supported by protocol: "
						+ name);
			}

		}

		return shareadDesignParameters;
	}

	private static InfoTableView getInfoTableView(String id)
	{
		IViewPart v;
		try
		{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0)
			{
				v = windows[0].getActivePage().getActivePart().getSite().getPage().showView(id);
				return (InfoTableView) v;
			}
			return null;

		} catch (PartInitException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	public static void sleep(long i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			// Ignore it
		}

	}

	private ILaunchConfiguration getVdmLaunchConfig(int port)
	{
		// ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType("org.overture.ide.vdmrt.debug.launchConfigurationType");// IVdmRtDebugConstants.ATTR_VDM_PROGRAM);
		try
		{
			wc = configType.newInstance(null, launchManager.generateUniqueLaunchConfigurationNameFrom(project.getName()));

			final String VDM_LAUNCH_CONFIG_PROJECT = "vdm_launch_config_project";
			final String VDM_LAUNCH_CONFIG_MODULE = "vdm_launch_config_module";
			final String VDM_LAUNCH_CONFIG_OPERATION = "vdm_launch_config_method";
			final String VDM_LAUNCH_CONFIG_STATIC_OPERATION = "vdm_launch_config_static_method";
			// final static String VDM_LAUNCH_CONFIG_EXPRESSION_SEPERATOR = "vdm_launch_config_expression_seperator";
			final String VDM_LAUNCH_CONFIG_EXPRESSION = "vdm_launch_config_expression";

			final String VDM_LAUNCH_CONFIG_DEFAULT = "vdm_launch_config_default";

			// final String VDM_LAUNCH_CONFIG_IS_TRACE = "vdm_launch_config_is_trace";

			// final String VDM_LAUNCH_CONFIG_REMOTE_CONTROL = "vdm_launch_config_remote_control_class";
			final String VDM_LAUNCH_CONFIG_CREATE_COVERAGE = "vdm_launch_config_create_coverage";
			final String VDM_LAUNCH_CONFIG_REMOTE_DEBUG = "vdm_launch_config_remote_debug";
			// final String VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION = "vdm_launch_config_memory_option";
			final String VDM_LAUNCH_CONFIG_ENABLE_LOGGING = "vdm_launch_config_enable_logging";
			
			final String VDM_LAUNCH_CONFIG_OVERRIDE_PORT = "vdm_launch_config_override_port";

			wc.setAttribute(VDM_LAUNCH_CONFIG_PROJECT, project.getName());
			wc.setAttribute(VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_DEFAULT, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_OPERATION, "AAA" + "()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_MODULE, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_EXPRESSION, "new AAA().AAA()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_STATIC_OPERATION, false);

			wc.setAttribute(VDM_LAUNCH_CONFIG_ENABLE_LOGGING, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_REMOTE_DEBUG, true);
			
			wc.setAttribute(VDM_LAUNCH_CONFIG_OVERRIDE_PORT, port);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wc;
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e)
		{
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
		return -1;
	}
}
