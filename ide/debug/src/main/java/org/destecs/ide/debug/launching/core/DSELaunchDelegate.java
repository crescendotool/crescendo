package org.destecs.ide.debug.launching.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.AcaGenerator;
import org.destecs.ide.debug.aca.plugin.ArchitectureAcaPlugin;
import org.destecs.ide.debug.aca.plugin.IncludeBaseConfigAcaPlugin;
import org.destecs.ide.debug.aca.plugin.SharedDesignParameterAcaPlugin;
import org.destecs.ide.debug.core.model.internal.DestecsDebugTarget;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class DSELaunchDelegate implements ILaunchConfigurationDelegate
{

	public void launch(ILaunchConfiguration configuration, final String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask("ACA execution", 100);
		String baseLaunchName = launch.getLaunchConfiguration().getAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, "");
		System.out.println("ACA launch with base: " + baseLaunchName);
		ILaunchConfiguration baseConfig = null;

		for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
		{
			if (tmp.getName().equals(baseLaunchName))
			{
				baseConfig = tmp;
			}
		}

		IProject project = getProject(baseConfig);

		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String outputPreFix = dateFormat.format(new Date()) + "_"
				+ configuration.getName();

		AcaGenerator generator = new AcaGenerator(configuration, baseConfig, monitor, 10, project, outputPreFix);
		generator.addGenerator(new IncludeBaseConfigAcaPlugin());
		generator.addGenerator(new ArchitectureAcaPlugin());
		generator.addGenerator(new SharedDesignParameterAcaPlugin());

		monitor.worked(10);
		final Set<ILaunchConfiguration> configurations = generator.generate();

		int step = 90 / configurations.size();
		monitor.setTaskName("Evaluating ACA generated launch configurations (total count "
				+ configurations.size() + ")");

		for (final ILaunchConfiguration config : configurations)
		{
			if (monitor.isCanceled())
			{
				break;
			}
			monitor.worked(step);
			System.out.println("Running ACA with: " + config.getName());
			ILaunch acaLaunch = launch(config, mode);
			IDebugTarget target = acaLaunch.getDebugTarget();
			File outputFolder = target == null ? null
					: ((DestecsDebugTarget) target).getOutputFolder();

			while (!acaLaunch.isTerminated() && !monitor.isCanceled())
			{
				sleep(100);
			}

			if (acaLaunch != null && !acaLaunch.isTerminated())
			{
				acaLaunch.terminate();
			}

			@SuppressWarnings("unchecked")
			Map<Object, Object> attributes = config.getAttributes();
			String data = "** launch summery for ACA: " + config.getName();
			for (Map.Entry<Object, Object> entry : attributes.entrySet())
			{
				data += entry.getKey() + " = " + entry.getValue() + "\n";
			}

			data += "\n\n----------------------- MEMENTO -------------------------------\n\n";
			data += config.getMemento();

			if (outputFolder != null)
			{
				try
				{
					FileWriter outFile = new FileWriter(new File(outputFolder, "launch"));
					PrintWriter out = new PrintWriter(outFile);
					out.println(data);
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			sleep(1000);// just let the tools calm down.
		}
		monitor.done();
	}

	private IProject getProject(ILaunchConfiguration configuration)
	{
		try
		{
			if (configuration != null)
			{
				String projectName = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
		}
	}

	public abstract class UILaunchJob extends UIJob
	{
		public boolean isFinished = false;

		public UILaunchJob(Display jobDisplay, String name)
		{
			super(jobDisplay, name);
		}

	}

	private ILaunch launch(ILaunchConfiguration config, String mode)
	{
		if (config != null)
		{
			// DebugUITools.launch(config, mode);
			try
			{
				ILaunch launch = DebugUITools.buildAndLaunch(config, mode, new NullProgressMonitor());
				return launch;
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// DebugUIPlugin.launchInForeground(config,mode);

		}
		return null;
	}

}
