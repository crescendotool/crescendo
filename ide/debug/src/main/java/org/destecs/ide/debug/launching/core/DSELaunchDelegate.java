package org.destecs.ide.debug.launching.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.AcaGenerator;
import org.destecs.ide.debug.aca.plugin.IncludeBaseConfigAcaPlugin;
import org.destecs.ide.debug.core.model.internal.DestecsDebugTarget;
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

		AcaGenerator generator = new AcaGenerator(configuration, baseConfig, monitor, 10);
		generator.addGenerator(new IncludeBaseConfigAcaPlugin());

		final Set<ILaunchConfiguration> configurations = generator.generate();

		int step = 90 / configurations.size();
		monitor.subTask("Evaluating ACA generated launch configurations (total count "
				+ configurations.size() + ")");

		for (final ILaunchConfiguration config : configurations)
		{
			monitor.worked(step);
			System.out.println("Running ACA with: " + config.getName());
			ILaunch acaLaunch = launch(config, mode);
			while (!acaLaunch.isTerminated() && !monitor.isCanceled())
			{
				sleep(1000);
			}
			IDebugTarget target = acaLaunch.getDebugTarget();
			File outputFolder = ((DestecsDebugTarget) target).getOutputFolder();
			@SuppressWarnings("unchecked")
			Map<Object, Object> attributes = config.getAttributes();
			String data = "** launch summery for ACA: "+config.getName();
			for (Map.Entry<Object, Object> entry : attributes.entrySet())
			{
				data += entry.getKey() + " = " + entry.getValue() + "\n";
			}
			
			data+="\n\n----------------------- MEMENTO -------------------------------\n\n";
			data+=config.getMemento();

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
		monitor.done();
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
