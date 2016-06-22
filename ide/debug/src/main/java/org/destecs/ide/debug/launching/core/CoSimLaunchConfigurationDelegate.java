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
package org.destecs.ide.debug.launching.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.parsers.ScriptParserWrapper;
import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.core.scenario.Action;
import org.destecs.core.simulationengine.ScenarioSimulationEngine;
import org.destecs.core.simulationengine.ScriptSimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.launcher.DummyLauncher;
import org.destecs.core.simulationengine.launcher.ISimulatorLauncher;
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
import org.destecs.ide.debug.launching.internal.LaunchStore;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.ide.simeng.internal.core.Clp20SimStatelessProgramLauncher;
import org.destecs.ide.simeng.internal.core.VdmRtBundleLauncher;
import org.destecs.ide.simeng.listener.EngineListener;
import org.destecs.ide.simeng.listener.ListenerToLog;
import org.destecs.ide.simeng.listener.MessageListener;
import org.destecs.ide.simeng.listener.SimulationListener;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.ide.ui.DestecsUIPlugin;
import org.destecs.ide.ui.IDestecsPreferenceConstants;
import org.destecs.ide.ui.utility.DestecsTypeCheckerUi;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.script.ast.EDomain;
import org.destecs.script.ast.analysis.DepthFirstAnalysisAdaptor;
import org.destecs.script.ast.node.INode;
import org.destecs.script.ast.preprocessing.AScriptInclude;
import org.destecs.script.ast.statement.AAssignStm;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

public class CoSimLaunchConfigurationDelegate extends
		LaunchConfigurationDelegate
{

	private static final String VDM_LAUNCH_CONFIG_TYPE = "org.overture.ide.vdmrt.debug.launchConfigurationType";
	private File deFile = null;
	private File ctFile = null;
	private String ctFilePathRelative = null;
	private String remoteRelativeProjectPath = null;
	private boolean useRemoteCtSimulator = false;
	private String resultFolderRelativePath = null;
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
	private ILaunchConfiguration configuration;
	private boolean enableLogging = false;
	private boolean showDebugInfo = false;
	private List<String> logVariablesVdm = new Vector<String>();;
	private List<String> logVariables20Sim = new Vector<String>();;
	private String ourputFolderPrefix = "";
	private boolean debug = false;
	private String clp20simToolSettings = null;
	private String clp20simImplementationSettings = null;
	private boolean leaveCtDirtyRunning = true;
	private boolean acaMode = false;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		IPreferenceStore store = DestecsUIPlugin.getDefault().getPreferenceStore();
		Boolean typeCheck = store.getBoolean(IDestecsPreferenceConstants.ACTIVATE_DESTECSCHECK_PREFERENCE);

		this.configuration = configuration;
		try
		{
			loadSettings(configuration);

			IDestecsProject destecsProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);

			Assert.isNotNull(destecsProject, " Project not found: "
					+ configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, ""));

			IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);
			if (vdmProject == null
					|| (typeCheck && !VdmTypeCheckerUi.typeCheck(vdmProject, monitor)))
			{
				abort("Cannot launch a project (" + vdmProject.getName()
						+ ") with type errors, please check the problems view", null);
			}

			if (destecsProject == null
					|| (typeCheck && !DestecsTypeCheckerUi.typeCheck(destecsProject, monitor)))
			{
				abort("Cannot launch a project (" + destecsProject.getName()
						+ ") with errors, please check the problems view", null);
			}

			this.launch = launch;
			target = new DestecsDebugTarget(launch, project, outputFolder);
			this.launch.addDebugTarget(target);
			startSimulation();
		} catch (Exception e)
		{
			abort("Aborting launch because of config error", e);
			launch.terminate();
			// target.terminate();
		}
	}

	private File getFileFromPath(IProject project, String path)
			throws IOException
	{
		if (path == null || path.isEmpty())
		{
			return null;
		}

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project))
		{
			return r.getLocation().toFile();
		}
		throw new IOException("Faild to find file: " + path);
	}

	private void loadSettings(ILaunchConfiguration configuration)
			throws IOException
	{
		boolean filterOutput = false;
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, ""));

			contractFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CONTRACT_PATH, ""));
			ourputFolderPrefix = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, "");
			deFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, ""));
			ctFilePathRelative = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, "");
			ctFile = getFileFromPath(project, ctFilePathRelative);
			useRemoteCtSimulator = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_USE_REMOTE_CT_SIMULATOR, false);
			remoteRelativeProjectPath = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_PROJECT_BASE, "");
			scenarioFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, ""));
			deArchitectureFile = getFileFromPath(project, configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, ""));
			deReplacePattern = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");
			sharedDesignParam = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
			totalSimulationTime = Double.parseDouble(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "0"));
			enableLogging = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, false);
			showDebugInfo = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, false);
			logVariablesVdm.clear();
			clp20simToolSettings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");
			clp20simImplementationSettings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, "");
			leaveCtDirtyRunning = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_LEAVE_DIRTY_FOR_INSPECTION, true);
			acaMode = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_MODE, "").equals(IDebugConstants.DESTECS_LAUNCH_MODE_ACA);
			filterOutput = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_FILTER_OUTPUT, false);

			String tmpVdm = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES, "");
			for (String var : tmpVdm.split(","))
			{
				if (var.trim().length() > 0)
				{
					logVariablesVdm.add(var);
				}
			}
			logVariables20Sim.clear();
			String tmp20Sim = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, "");
			for (String var : tmp20Sim.split(","))
			{
				if (var.trim().length() > 0)
				{
					logVariables20Sim.add(var);
				}
			}

			debug = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DEBUG, false);

			String deUrlString = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
			if (deUrlString.length() == 0)
			{
				Integer freePort = VdmRtBundleLauncher.getFreePort();
				if (freePort == -1)
				{
					throw new Exception("No free port found for DE launch");
				}
				deUrl = new URL(IDebugConstants.DEFAULT_DE_ENDPOINT.replace("PORT", freePort.toString()));
			} else
			{
				deUrl = new URL(deUrlString);
			}
			ctUrl = new URL(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT));

			remoteDebug = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to load launch configuration attributes", e);
		} catch (MalformedURLException e)
		{
			DestecsDebugPlugin.logError("Failed to load launch configuration attributes (URL's)", e);
		} catch (IOException e)
		{
			DestecsDebugPlugin.logError("Failed to find file", e);
			throw e;
		} catch (Exception e)
		{
			DestecsDebugPlugin.logError("Failed to load launch configuration attributes (URL's)", e);
		}

		IFolder outputTmp = createOutputFolder(configuration);

		if (filterOutput)
		{
			try
			{

				int type = IResourceFilterDescription.EXCLUDE_ALL
						| IResourceFilterDescription.FOLDERS/* |IResourceFilterDescription.FILES */;
				FileInfoMatcherDescription matchter = new FileInfoMatcherDescription("org.eclipse.ui.ide.multiFilter", "1.0-name-matches-false-false-"
						+ outputTmp.getName());

				boolean hasFilter = false;
				for (IResourceFilterDescription filter : outputTmp.getParent().getFilters())
				{
					if (filter.getType() == type
							&& filter.getFileInfoMatcherDescription().equals(matchter)
							&& filter.getResource().equals(outputTmp.getParent()))
					{
						hasFilter = true;
						break;
					}
				}
				// outputTmp.setHidden(true);
				if (!hasFilter)
				{
					outputTmp.getParent().createFilter(type, matchter, IResource.BACKGROUND_REFRESH, new NullProgressMonitor());
				}

			} catch (CoreException e)
			{
				DestecsDebugPlugin.logError("Unable to create exclude filter for output folder"
						+ outputTmp.getName(), e);
			}
		}

		outputFolder = outputTmp.getLocation().toFile();// new File(base, resultFolderRelativePath);

		try
		{
			LaunchStore.store(configuration, outputFolder);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to store launch configuration to"
					+ outputFolder, e);
		}

	}

	private IFolder createOutputFolder(ILaunchConfiguration configuration)
	{
		IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		IFolder base = dProject.getOutputFolder();// .getLocation().toFile();
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String tmp = ourputFolderPrefix;
		tmp = tmp.replace('\\', '/');
		if (!tmp.endsWith("/"))
		{
			tmp += "/";
		}
		resultFolderRelativePath = tmp + dateFormat.format(new Date()) + "_"
				+ configuration.getName();

		try
		{
			String postfix = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_NAME_POSTFIX, "");
			if (postfix != null && !postfix.isEmpty())
			{
				resultFolderRelativePath += " {" + postfix + "}";
			}
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to load launch configuration attributes", e);
		}

		IFolder outputTmp = base;
		try
		{
			outputTmp.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
			if (!outputTmp.exists())
			{
				outputTmp.create(true, true, new NullProgressMonitor());
			}
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to create output folder"
					+ outputTmp, e);
		}

		for (String part : resultFolderRelativePath.split("/"))
		{
			outputTmp = outputTmp.getFolder(part);
			try
			{
				if (!outputTmp.exists())
				{
					outputTmp.create(true, true, new NullProgressMonitor());
				}
			} catch (CoreException e)
			{
				DestecsDebugPlugin.logError("Failed to create output folder"
						+ outputTmp + " part:" + part, e);
			}
		}
		return outputTmp;
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
					final String engineViewId = IDebugConstants.ENGINE_VIEW_ID;
					final InfoTableView engineView = getInfoTableView(engineViewId);

					views.add(engineView);
					engine.engineListeners.add(new EngineListener(engineView));

					if (showDebugInfo)
					{
						final String messageViewId = IDebugConstants.MESSAGE_VIEW_ID;
						final String simulationViewId = IDebugConstants.SIMULATION_VIEW_ID;

						final InfoTableView messageView = getInfoTableView(messageViewId);
						final InfoTableView simulationView = getInfoTableView(simulationViewId);

						views.add(messageView);
						views.add(simulationView);

						engine.messageListeners.add(new MessageListener(messageView));
						engine.simulationListeners.add(new SimulationListener(simulationView));

						for (InfoTableView view : views)
						{
							view.refreshPackTable();
						}
					}
					return new Status(IStatus.OK, IDebugConstants.PLUGIN_ID, "Listeners OK");
				}
			};

			listeners.schedule();
			if (enableLogging)
			{
				engine.engineListeners.add(log);
				engine.messageListeners.add(log);
				engine.simulationListeners.add(log);
				engine.variablesSyncListeners.add(log);
			}
			if (!remoteDebug)
			{
				File libSearchRoot = new File(project.getLocation().toFile(), "lib");
				engine.setDeSimulationLauncher(new VdmRtBundleLauncher(deFile, deUrl.getPort(), libSearchRoot));// new
			} else
			{
				deUrl = new URL(IDebugConstants.DEFAULT_DE_ENDPOINT.replaceAll("PORT", Integer.valueOf(IDebugConstants.DEFAULT_DEBUG_PORT).toString()));
				engine.setDeSimulationLauncher(new VdmRtLauncher(5000));
			}

			final int deDebugPort = findFreePort();
			ModelConfig deModel = getDeModelConfig(project, deDebugPort);
			engine.setDeModel(deModel);
			engine.setDeEndpoint(deUrl);

			if (!useRemoteCtSimulator)
			{
				// engine.setCtSimulationLauncher(new
				// Clp20SimStatelessProgramLauncher(ctFile));
				// engine.setCtSimulationLauncher(new
				// Clp20SimProgramLauncher(ctFile));
				engine.setCtSimulationLauncher(selectClp20SimLauncher(engine, ctFile));
				// engine.setCtEndpoint(new URL(IDebugConstants.DEFAULT_CT_ENDPOINT));
				engine.setCtEndpoint(ctUrl);
			} else
			{
				engine.setCtSimulationLauncher(new DummyLauncher("CT simulator"));
				engine.setCtEndpoint(ctUrl);
			}
			ModelConfig ctModel = getCtModelConfig(ctFile);
			engine.setCtModel(ctModel);

			setCtSettings(engine);
			setCtImplementations(engine);

			engine.setOutputFolder(outputFolder);
			engine.debug(debug);

			engine.addProcessCreationListener(new IProcessCreationListener()
			{
				public void processCreated(String name, Process p)
				{
					launch.addProcess(DebugPlugin.newProcess(launch, p, name));
				}
			});

			if (deModel.logFile != null)
			{
				target.setDeCsvFile(new File(deModel.logFile));
			}
			if (!useRemoteCtSimulator)
			{
				if (ctModel.logFile != null)
				{
					target.setCtCsvFile(new File(ctModel.logFile));
				}
			}
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
					if (simulator == Simulator.DE)
					{
						vdm.schedule();
					}
				}
			});

			CoSimulationThread simThread = new CoSimulationThread(engine, log, shareadDesignParameters, totalSimulationTime, target, views, acaMode);
			target.setCoSimulationThread(simThread);
			simThread.start();

		} catch (Exception ex)
		{
			ex.printStackTrace();
			DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Failed to launch: "
					+ ex.getMessage(), ex));
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

	private static class ScriptClp20simStateAffectSearcher extends
			DepthFirstAnalysisAdaptor
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7732646898630654830L;
		public boolean ctStateAffected = false;

		@Override
		public void caseAAssignStm(AAssignStm node) throws Throwable
		{
			if (node != null && node.getDomain().kindPDomain() == EDomain.CT)
			{
				ctStateAffected = true;
				throw new Exception("Finished search");
			}
		}
	}

	private ISimulatorLauncher selectClp20SimLauncher(SimulationEngine engine,
			File model)
	{

		if (engine instanceof ScriptSimulationEngine)
		{
			for (INode n : ((ScriptSimulationEngine) engine).script)
			{
				ScriptClp20simStateAffectSearcher searcher = new ScriptClp20simStateAffectSearcher();
				try
				{
					n.apply(searcher);
				} catch (Throwable e)
				{
					// Ignore
				}
				if (searcher.ctStateAffected)
				{
					return new Clp20SimStatelessProgramLauncher(model, leaveCtDirtyRunning);
				}
			}

		} else if (engine instanceof ScenarioSimulationEngine)
		{
			boolean stateAffected = false;
			for (Action a : ((ScenarioSimulationEngine) engine).actions)
			{
				if (a != null && a.targetSimulator == Action.Simulator.CT)
				{
					stateAffected = true;
					break;
				}
			}
			if (stateAffected)
			{
				return new Clp20SimStatelessProgramLauncher(model, leaveCtDirtyRunning);
			}
		}

		return new Clp20SimProgramLauncher(model);

	}

	private void setCtImplementations(SimulationEngine engine)
	{

		Properties properties = new Properties();

		String[] settings = clp20simImplementationSettings.split(";");

		for (String setting : settings)
		{

			String[] splitSetting = setting.split("=");
			if (splitSetting.length == 2)
			{
				properties.put(splitSetting[0], splitSetting[1]);
			}
		}

		StringWriter sw = new StringWriter();
		try
		{
			properties.store(sw, "");
			engine.setCtImplementations(sw.toString());
		} catch (IOException e)
		{
			DestecsDebugPlugin.logWarning("Failed record CT implementation settings for use in simulation", e);
		}

	}

	private void setCtSettings(SimulationEngine engine)
	{
		Properties properties = new Properties();

		String[] settings = clp20simToolSettings.split(";");

		for (String setting : settings)
		{

			String[] splitSetting = setting.split("=");
			if (splitSetting.length == 2)
			{
				properties.put(splitSetting[0], splitSetting[1]);
			}
		}

		StringWriter sw = new StringWriter();
		try
		{
			properties.store(sw, "");
			engine.setCtSettings(sw.toString());
		} catch (IOException e)
		{
			DestecsDebugPlugin.logWarning("Failed record CT Tool settings for use in simulation", e);
		}

	}

	private ModelConfig getCtModelConfig(File ctFile)
	{
		String logfile = null;
		CtModelConfig model = null;
		if (!useRemoteCtSimulator)
		{
			model = new CtModelConfig(ctFile.getAbsolutePath());
			logfile = new File(outputFolder, "20simVariables.csv").getAbsolutePath();
		} else
		{
			model = new CtModelConfig((remoteRelativeProjectPath + "\\" + ctFilePathRelative).replace("/", "\\"));
			logfile = (remoteRelativeProjectPath + "\\output\\"
					+ resultFolderRelativePath + "\\" + "20simVariables.csv").replace("/", "\\");
		}

		if (logVariables20Sim.size() > 1)
		{
			model.logVariables.addAll(logVariables20Sim);
			model.logFile = logfile;
		}
		return model;
	}

	private ModelConfig getDeModelConfig(IProject project2, int port)
	{
		final IDestecsProject p = (IDestecsProject) project2.getAdapter(IDestecsProject.class);
		final DeModelConfig model = new DeModelConfig();
		model.logVariables.addAll(logVariablesVdm);
		if (!model.logVariables.isEmpty())
		{
			model.logFile = new File(outputFolder, "VdmVariables.csv").getAbsolutePath();
		}
		model.arguments.put(DeModelConfig.LOAD_OUTPUT_DIR, outputFolder.getAbsolutePath());
		model.arguments.put(DeModelConfig.LOAD_REPLACE, deReplacePattern);
		model.arguments.put(DeModelConfig.LOAD_DEBUG_PORT, String.valueOf(port));
		model.arguments.put(DeModelConfig.LOAD_BASE_DIR, p.getVdmModelFolder().getLocation().toFile().getAbsolutePath());

		try
		{
			if (!configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION, false))
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_RT_VALIDATOR, "true");
			}
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Could not get realtime check option from launch configuration", e);
		}

		if (deArchitectureFile != null && deArchitectureFile.exists())
		{
			StringBuffer architecture = new StringBuffer();
			StringBuffer deploy = new StringBuffer();
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(deArchitectureFile));
				String str;
				boolean inArch = false;
				boolean inDeploy = false;
				while ((str = in.readLine()) != null)
				{
					str = str.trim();
					if (str.startsWith("-- ## Architecture ## --"))
					{
						inArch = true;
						inDeploy = false;
					}
					if (str.startsWith("-- ## Deployment ## --"))
					{
						inDeploy = true;
						inArch = false;
					}

					if (inArch)
					{
						architecture.append(str);
						architecture.append("\n");
					}
					if (inDeploy)
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
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true))// vdmProject.hasPrechecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_PRE, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true))// vdmProject.hasPostchecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_POST, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true))// vdmProject.hasInvchecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_INV, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true))// vdmProject.hasDynamictypechecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_DYNAMIC_TC, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true))// vdmProject.hasMeasurechecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_MEASURE, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true))// vdmProject.hasMeasurechecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_RT_LOG, "true");
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true))// vdmProject.hasMeasurechecks())
			{
				model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_COVERAGE, "true");
			}

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
			DestecsDebugPlugin.logError("Failed to create VDM model config", e);
			return null;
		}

	}

	private ListenerToLog getLog()
	{
		if (!enableLogging)
		{
			return null;
		}
		try
		{
			return new ListenerToLog(outputFolder);
		} catch (FileNotFoundException e)
		{
			DestecsDebugPlugin.logError("Failed to make log listener", e);
		}
		return null;
	}

	private SimulationEngine getEngine() throws Exception
	{

		if (scenarioFile != null)
		{
			// if (scenarioFile.getName().endsWith("script2"))
			// {
			ScriptParserWrapper parser = new ScriptParserWrapper();
			List<INode> script = parser.parse(scenarioFile);

			if (script.contains(null))
			{
				throw new Exception("Failed to parse script file");
			}

			script = expandScript(script, scenarioFile, null);
			return new ScriptSimulationEngine(contractFile, script);
			// }

			// Scenario scenario = new ScenarioParserWrapper().parse(scenarioFile);
			// if (scenario == null)
			// {
			// throw new Exception("Scenario not parse correct: "
			// + scenarioFile);
			// }
			// return new ScenarioSimulationEngine(contractFile, scenario);

		} else
		{
			return new SimulationEngine(contractFile);
		}
	}

	public List<INode> expandScript(List<INode> script, File scriptFile,
			Set<File> used) throws IOException
	{
		if (used == null)
		{
			used = new HashSet<File>();
		}

		// Check for infinite loop inclusion
		if (used.contains(scriptFile))
		{
			return new Vector<INode>();// Do not include again
			// List<INode> notExpandedScript = new Vector<INode>();
			// for (INode node : script)
			// {
			// if (!(node instanceof AScriptInclude))
			// {
			// notExpandedScript.add(node);
			// }
			// }
			// return notExpandedScript;
		}

		used.add(scriptFile);

		List<INode> expandedScript = new Vector<INode>();
		for (INode node : script)
		{
			if (node instanceof AScriptInclude)
			{
				ScriptParserWrapper parser = new ScriptParserWrapper();
				File file = new File(scriptFile.getParentFile(), ((AScriptInclude) node).getFilename().replace('\"', ' ').trim());
				List<INode> subScript = parser.parse(file);
				expandedScript.addAll(expandScript(subScript, file, used));
			} else
			{
				expandedScript.add(node);
			}
		}

		return expandedScript;
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
			List<Integer> size = new Vector<Integer>();
			List<Double> value = new Vector<Double>();

			if (!(result.get(name) instanceof List))
			{
				size.add(1);
				value.add(convertToDouble(result.get(name)));
			} else if (result.get(name) instanceof List)
			{
				@SuppressWarnings("rawtypes")
				List list = (List) result.get(name);
				for (Object object : list)
				{
					value.add(convertToDouble(object));
				}

				String dimentions = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
				String[] splitDimentions = dimentions.split(",");

				for (String string : splitDimentions)
				{
					size.add(Integer.parseInt(string));
				}

				name = name.substring(0, name.indexOf("["));
			} else
			{
				throw new Exception("Design parameter type not supported by protocol: "
						+ name);
			}

			shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam(name, size,value));

		}

		return shareadDesignParameters;
	}

	private static Double convertToDouble(Object object)
	{
		if (object instanceof Double)
		{
			return ((Double) object);
		} else if (object instanceof Integer)
		{
			return (((Integer) object).doubleValue());
		} else if (object instanceof Boolean)
		{
			boolean r = ((Boolean) object).booleanValue();
			Double val = Double.valueOf(0);
			if (r)
			{
				val = Double.valueOf(1);
			}
			return val;
		}
		return null;
	}

	public static InfoTableView getInfoTableView(String id)
	{
		IViewPart v;
		try
		{
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window!=null)
			{
				v = window.getActivePage().showView(id);
				return (InfoTableView) v;
			}
			return null;

		} catch (PartInitException e)
		{
			DestecsDebugPlugin.logError("Failed to create info table view", e);
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

	@SuppressWarnings("deprecation")
	private ILaunchConfiguration getVdmLaunchConfig(int port)
	{
		// ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(VDM_LAUNCH_CONFIG_TYPE);// IVdmRtDebugConstants.ATTR_VDM_PROGRAM);
		try
		{
			wc = configType.newInstance(null, launchManager.generateUniqueLaunchConfigurationNameFrom(project.getName()));

			final String VDM_LAUNCH_CONFIG_PROJECT = "vdm_launch_config_project";
			final String VDM_LAUNCH_CONFIG_MODULE = "vdm_launch_config_module";
			final String VDM_LAUNCH_CONFIG_OPERATION = "vdm_launch_config_method";
			final String VDM_LAUNCH_CONFIG_STATIC_OPERATION = "vdm_launch_config_static_method";
			// final static String VDM_LAUNCH_CONFIG_EXPRESSION_SEPERATOR =
			// "vdm_launch_config_expression_seperator";
			final String VDM_LAUNCH_CONFIG_EXPRESSION = "vdm_launch_config_expression";

			final String VDM_LAUNCH_CONFIG_DEFAULT = "vdm_launch_config_default";

			// final String VDM_LAUNCH_CONFIG_IS_TRACE =
			// "vdm_launch_config_is_trace";

			// final String VDM_LAUNCH_CONFIG_REMOTE_CONTROL =
			// "vdm_launch_config_remote_control_class";

			// VDM RT LOG
			final String VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING = "vdm_launch_config_enable_realtime_logging";

			final String VDM_LAUNCH_CONFIG_CREATE_COVERAGE = "vdm_launch_config_create_coverage";
			final String VDM_LAUNCH_CONFIG_REMOTE_DEBUG = "vdm_launch_config_remote_debug";
			// final String VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION =
			// "vdm_launch_config_memory_option";
			final String VDM_LAUNCH_CONFIG_ENABLE_LOGGING = "vdm_launch_config_enable_logging";

			final String VDM_LAUNCH_CONFIG_OVERRIDE_PORT = "vdm_launch_config_override_port";

			wc.setAttribute(VDM_LAUNCH_CONFIG_PROJECT, project.getName());
			// wc.setAttribute(VDM_LAUNCH_CONFIG_CREATE_COVERAGE, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_DEFAULT, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_OPERATION, "AAA" + "()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_MODULE, "AAA");
			wc.setAttribute(VDM_LAUNCH_CONFIG_EXPRESSION, "new AAA().AAA()");

			wc.setAttribute(VDM_LAUNCH_CONFIG_STATIC_OPERATION, false);

			wc.setAttribute(VDM_LAUNCH_CONFIG_ENABLE_LOGGING, showDebugInfo);

			wc.setAttribute(VDM_LAUNCH_CONFIG_REMOTE_DEBUG, true);

			wc.setAttribute(VDM_LAUNCH_CONFIG_OVERRIDE_PORT, port);

			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true))// vdmProject.hasPrechecks())
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true))// vdmProject.hasPostchecks())
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true))// vdmProject.hasInvchecks())
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true))// vdmProject.hasDynamictypechecks())
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true))// vdmProject.hasMeasurechecks())
			{
				wc.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true))// vdmProject.hasMeasurechecks())
			{
				wc.setAttribute(VDM_LAUNCH_CONFIG_CREATE_COVERAGE, false);
			}
			if (!configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true))// vdmProject.hasMeasurechecks())
			{
				wc.setAttribute(VDM_LAUNCH_CONFIG_ENABLE_REALTIME_LOGGING, false);
			}

		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to create vdm launch config", e);
		}

		return wc;
	}

	/**
	 * Throws an exception with a new status containing the given message and optional exception.
	 * 
	 * @param message
	 *            error message
	 * @param e
	 *            underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException
	{
		throw new CoreException((IStatus) new Status(IStatus.ERROR, IDebugConstants.PLUGIN_ID, 0, message, e));
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
