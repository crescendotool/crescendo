package org.destecs.ide.debug.launching.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.simulationengine.ScenarioSimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.launcher.VdmRtLauncher;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.ide.simeng.internal.core.EngineListener;
import org.destecs.ide.simeng.internal.core.ListenerToLog;
import org.destecs.ide.simeng.internal.core.MessageListener;
import org.destecs.ide.simeng.internal.core.SimulationListener;
import org.destecs.ide.simeng.internal.core.VdmRtBundleLauncher;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class CoSimLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate
{

	private File dtFile = null;
	private File ctFile = null;
	private File contractFile = null;
	private File scenarioFile = null;
	private String sharedDesignParam = null;
	private double totalSimulationTime = 0.0;
	private IProject project = null;
	private URL deUrl = null;
	private URL ctUrl = null;
	private boolean remoteDebug = false;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		loadSettings(configuration);

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
			dtFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, ""));
			ctFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, ""));
			scenarioFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, ""));
			sharedDesignParam = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
			totalSimulationTime = Double.parseDouble(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "0"));

			deUrl = new URL(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT));
			ctUrl = new URL(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT));

			remoteDebug = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Faild to load launch configuration attributes", e);
		} catch (MalformedURLException e)
		{
			DestecsDebugPlugin.logError("Faild to load launch configuration attributes (URL's)", e);
		}

	}

	private void startSimulation()
	{
		final List<InfoTableView> views = new Vector<InfoTableView>();
		final ListenerToLog log = getLog();
		Job runSimulation = null;
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

			if (!remoteDebug)
			{
				engine.setDtSimulationLauncher(new VdmRtBundleLauncher(dtFile));// new
			} else
			{
				engine.setDtSimulationLauncher(new VdmRtLauncher(5000));
			}

			engine.setDtModel(dtFile);
			engine.setDtEndpoint(deUrl);

			engine.setCtSimulationLauncher(new Clp20SimProgramLauncher(ctFile));
			engine.setCtModel(ctFile);
			engine.setCtEndpoint(ctUrl);

			final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = loadSharedDesignParameters(sharedDesignParam);

			runSimulation = new Job("Simulation")
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					final List<Throwable> exceptions = new Vector<Throwable>();
					class SimulationRunner extends Thread
					{
						public SimulationRunner()
						{
							setDaemon(true);
							setName("Simulation Engine");
						}

						public void run()
						{
							try
							{
								Job vdmlaunch = new Job("VDM launch")
								{

									@Override
									protected IStatus run(
											IProgressMonitor monitor)
									{
										ILaunchConfiguration vdmLaunchConfig = getVdmLaunchConfig();
										try
										{
//											final IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
//											final Shell shell = Display.getDefault().getActiveShell();
//											shell.getDisplay().syncExec(new Runnable()
//											{
//												public void run()
//												{
//													VdmTypeCheckerUi.typeCheck(shell, vdmProject);
//												}
//											});
											vdmLaunchConfig.launch("debug", null);
										} catch (CoreException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										return Status.OK_STATUS;
									}

								};
								vdmlaunch.schedule();

								engine.simulate(shareadDesignParameters, totalSimulationTime);
							} catch (Throwable e)
							{
								exceptions.add(e);
								log.flush();
							}
						}

					}

					Thread simulationEngineThread = new SimulationRunner();

					simulationEngineThread.start();

					while (!simulationEngineThread.isInterrupted()
							&& simulationEngineThread.isAlive())
					{
						sleep(2000);

						if (monitor.isCanceled())
						{
							engine.forceSimulationStop();
							log.flush();
						}
					}

					for (InfoTableView view : views)
					{
						view.refreshPackTable();
					}

					log.close();

					try
					{
						project.refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException e)
					{
						// Ignore it
					}

					if (exceptions.size() == 0)
					{
						return Status.OK_STATUS;
					} else
					{
						for (Throwable throwable : exceptions)
						{
							throwable.printStackTrace();
						}
						return new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, "Simulation failed", exceptions.get(0));
					}

				}

				private void sleep(long i)
				{
					try
					{
						Thread.sleep(i);
					} catch (InterruptedException e)
					{
						// Ignore it
					}

				}
			};
			runSimulation.schedule();
		} catch (Exception ex)
		{
			ex.printStackTrace();
			for (InfoTableView view : views)
			{
				view.refreshPackTable();
			}
		}
	}

	private ListenerToLog getLog()
	{
		try
		{
			IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
			return new ListenerToLog(dProject.getOutputFolder().getLocation().toFile());
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
			}else if (result.get(name) instanceof Boolean)
			{
				boolean r = ((Boolean) result.get(name)).booleanValue();
				Double val = Double.valueOf(0);
				if(r)
				{
					val =  Double.valueOf(1);
				}
				shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, val));
			}else
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

	private ILaunchConfiguration getVdmLaunchConfig()
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

			final String VDM_LAUNCH_CONFIG_IS_TRACE = "vdm_launch_config_is_trace";

			final String VDM_LAUNCH_CONFIG_REMOTE_CONTROL = "vdm_launch_config_remote_control_class";
			final String VDM_LAUNCH_CONFIG_CREATE_COVERAGE = "vdm_launch_config_create_coverage";
			final String VDM_LAUNCH_CONFIG_REMOTE_DEBUG = "vdm_launch_config_remote_debug";
			final String VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION = "vdm_launch_config_memory_option";
			final String VDM_LAUNCH_CONFIG_ENABLE_LOGGING = "vdm_launch_config_enable_logging";

			wc.setAttribute(VDM_LAUNCH_CONFIG_PROJECT, project.getName());
			wc.setAttribute(VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_DEFAULT, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_OPERATION, "AAA" + "()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_MODULE, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_EXPRESSION, "new AAA().AAA()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_STATIC_OPERATION, false);

			wc.setAttribute(VDM_LAUNCH_CONFIG_ENABLE_LOGGING, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_REMOTE_DEBUG, true);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wc;
	}

}
