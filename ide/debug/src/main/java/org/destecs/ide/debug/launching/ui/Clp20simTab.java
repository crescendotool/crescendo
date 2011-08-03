package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.launching.ui.internal._20simHelper;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.QueryToolSettingsStruct;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

public class Clp20simTab extends AbstractLaunchConfigurationTab
{

	private String ctPath = null;
	private String ctEndpoint = null;
	private String projectName;

	public void createControl(Composite parent)
	{

		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		final Button b = createPushButton(comp, "Populate...", null);

		b.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{

				b.setEnabled(false);

				Job populator = new Job("20-sim table Populator")
				{

					private File getFileFromPath(IProject project, String path)
					{

						IResource r = project.findMember(new Path(path));

						if (r != null && !r.equals(project))
						{
							return r.getLocation().toFile();
						}
						return null;
					}

					@Override
					protected IStatus run(IProgressMonitor monitor)
					{

						try
						{
							if (projectName.length() == 0)
							{
								return new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Project is not set");

							}

							File ctFile = getFileFromPath(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName), ctPath);
							Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(ctFile);
							Process p = clp20sim.launch();

							ProxyICoSimProtocol protocol = _20simHelper.connect(new URL(ctEndpoint));
							QueryToolSettingsStruct getst = protocol.queryToolSettings();
							System.out.println(getst);
						} catch (MalformedURLException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SimulationException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						UIJob changeButton = new UIJob("Enable populate button")
						{

							@Override
							public IStatus runInUIThread(
									IProgressMonitor monitor)
							{
								b.setEnabled(true);
								return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "Enabled populate button");
							}
						};
						changeButton.schedule();
						return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "Populated ok");
					}
				};
				populator.schedule();

			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO Auto-generated method stub

	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// TODO Auto-generated method stub

	}

	public String getName()
	{

		return "20-sim options";
	}

}
