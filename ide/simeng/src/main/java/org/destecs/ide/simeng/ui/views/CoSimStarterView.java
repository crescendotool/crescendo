package org.destecs.ide.simeng.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.IEngineListener;
import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.launcher.Clp20SimLauncher;
import org.destecs.ide.simeng.internal.core.VdmRtBundleLauncher;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;



public class CoSimStarterView extends ViewPart
{

	static public String VIEW_ID = "org.destecs.ide.simeng.ui.cosimstarter";
	
	private Label warningLabel = null;
	private Text ctPath = null;
	private Text dtPath = null;
	private Text contractPath = null;
	private Text fProjectText;
	private WidgetListener fListener = new WidgetListener();
	private Shell fShell;

	public CoSimStarterView()
	{
		super();

	}

	public void createPartControl(Composite parent)
	{
		fShell = parent.getShell();
		
		
		createProjectSelection(parent);
		
		parent.setLayout(new GridLayout(1, false));
		
		
		// DT Line
		Label dtLabel = new Label(parent, SWT.NONE);
		dtLabel.setText("DT Path:");
		dtPath= new Text(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		dtPath.setLayoutData(gridData);
		dtPath.setText("Insert DT model path here");
		
		// CT Line
		Label ctLabel = new Label(parent, SWT.NONE);
		ctLabel.setText("CT Path:");
		ctPath = new Text(parent, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		ctPath.setLayoutData(gridData);
		ctPath.setText("Insert CT model path here");
		
		// Contract Line
		Label contractLabel = new Label(parent, SWT.NONE);
		contractLabel.setText("Contract Path:");
		contractPath = new Text(parent, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		contractPath.setLayoutData(gridData);
		contractPath.setText("Insert Contract path here");
		
		Button runButton = new Button(parent,SWT.PUSH);
		runButton.setText("Run CoSim");
		gridData = new GridData();
		//gridData.horizontalSpan = 2;
		runButton.setLayoutData(gridData);
		
		warningLabel = new Label(parent, SWT.NONE);
		
		runButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				startSimulation();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// TODO Auto-generated method stub

			}
		});

		
	}

	

	public void setFocus()
	{

	}

	
	private void createProjectSelection(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Project");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText("Project:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectText.setLayoutData(gd);
		fProjectText.addModifyListener(fListener);

		Button selectProjectButton = createPushButton(group, "Browse...", null);

		selectProjectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ListSelectionDialog dlg = new ListSelectionDialog(getShell(),
				// ResourcesPlugin.getWorkspace().getRoot(), new
				// BaseWorkbenchContentProvider(), new
				// WorkbenchLabelProvider(), "Select the Project:");
				// dlg.setTitle("Project Selection");
				// dlg.open();
				class ProjectContentProvider extends
						BaseWorkbenchContentProvider
				{
					@Override
					public boolean hasChildren(Object element)
					{
						if (element instanceof IProject)
						{
							return false;
						} else
						{
							return super.hasChildren(element);
						}
					}

					@SuppressWarnings("unchecked")
					@Override
					public Object[] getElements(Object element)
					{
						List elements = new Vector();
						Object[] arr = super.getElements(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								if (object instanceof IProject)
									
								{
									elements.add(object);
								}
							}
							return elements.toArray();
						}
						return null;
					}

				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
				dialog.setTitle("Project Selection");
				dialog.setMessage("Select a project:");
				dialog.setComparator(new ViewerComparator());

				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							&& dialog.getFirstResult() instanceof IProject)
							//&& ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
					{
						fProjectText.setText(((IProject) dialog.getFirstResult()).getName());
					}

				}
			}

			private Shell getShell() {
				return fShell;
			}
		});
	}
	
	private void startSimulation()
	{
		final String messageViewId = "org.destecs.ide.simeng.ui.views.SimulationMessagesView";
		final String engineViewId = "org.destecs.ide.simeng.ui.views.SimulationEngineView";
		final String simulationViewId = "org.destecs.ide.simeng.ui.views.SimulationView";

		Job runSimulation = null;
		try
		{
			
			
			
			SimulationEngine.eclipseEnvironment = true;
			final SimulationEngine engine = new SimulationEngine(new File(contractPath.getText()));

			engine.engineListeners.add(new EngineListener(getInfoTableView(engineViewId)));
			engine.messageListeners.add(new MessageListener(getInfoTableView(messageViewId)));
			engine.simulationListeners.add(new SimulationListener(getInfoTableView(simulationViewId)));
			
			
			engine.setDtSimulationLauncher(new VdmRtBundleLauncher(new File(dtPath.getText())));//new File("C:\\destecs\\workspace\\watertank_new\\model")));
			engine.setDtModel(new File(dtPath.getText()));
			engine.setDtEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));

			engine.setCtSimulationLauncher(new Clp20SimLauncher());
			engine.setCtModel(new File(ctPath.getText()));
			engine.setCtEndpoint(new URL("http://localhost:1580"));

			
			
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
							ISafeRunnable runnable = new ISafeRunnable()
							{

								public void run() throws Exception
								{
									runSumulation(engine);

								}

								public void handleException(Throwable e)
								{
									exceptions.add(e);
								}
							};

							SafeRunner.run(runnable);
						};

					}
					
					Thread simulationEngineThread = new SimulationRunner();
					
					simulationEngineThread.start();
					
					while (!simulationEngineThread.isInterrupted() && simulationEngineThread.isAlive())
					{
						sleep(2000);

						if (monitor.isCanceled())
						{
							engine.forceSimulationStop();
						}
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
						return new Status(IStatus.ERROR, "", "Simulation faild", exceptions.get(0));
					}

				}

				private void sleep(long i)
				{
					try
					{
						Thread.sleep(i);
					} catch (InterruptedException e)
					{
						//Ignore it
					}
					
				}
			};

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		runSimulation.schedule();
	}

	

	private InfoTableView getInfoTableView(String id)
	{
		IViewPart v;
		try
		{
			v = super.getSite().getPage().showView(id);
			return (InfoTableView) v;
		} catch (PartInitException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
	}

	private void runSumulation(final SimulationEngine engine)
			throws ModelPathNotValidException, MalformedURLException,
			InvalidEndpointsExpection, InvalidSimulationLauncher,
			FileNotFoundException
	{
		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minlevel", 1.0));
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxlevel", 2.0));

		engine.simulate(shareadDesignParameters, 5);
	}

	private static class EngineListener implements IEngineListener
	{
		private InfoTableView view;

		public EngineListener(InfoTableView view)
		{
			this.view = view;
			this.view.resetBuffer();
		}

		public void info(Simulator simulator, String message)
		{
			List<String> l = new Vector<String>();
			l.add(simulator.toString());
			l.add(message.replace('\n', ' '));
			view.setDataList(l);
		}
	}

	/**
	 * Creates and returns a new push button with the given
	 * label and/or image.
	 * 
	 * @param parent parent control
	 * @param label button label or <code>null</code>
	 * @param image image of <code>null</code>
	 * 
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label, Image image) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null) {
			button.setImage(image);
		}
		if (label != null) {
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);	
		setButtonDimensionHint(button);
		return button;	
	}	
	
	/**
	 * Sets width and height hint for the button control.
	 * <b>Note:</b> This is a NOP if the button's layout data is not
	 * an instance of <code>GridData</code>.
	 * 
	 * @param	the button for which to set the dimension hint
	 */		
	public static void setButtonDimensionHint(Button button) {
		Assert.isNotNull(button);
		Object gd= button.getLayoutData();
		if (gd instanceof GridData) {
			((GridData)gd).widthHint= getButtonWidthHint(button);	
			((GridData)gd).horizontalAlignment = GridData.FILL;	 
		}
	}		
	
	
	/**
	 * Returns a width hint for a button control.
	 */
	public static int getButtonWidthHint(Button button) {
		/*button.setFont(JFaceResources.getDialogFont());*/
		PixelConverter converter= new PixelConverter(button);
		int widthHint= converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			// validatePage();
			//updateLaunchConfigurationDialog();
			System.out.println("Selected Project (modify text):" + fProjectText.getText());
			searchModels();
			
		}

		private void searchModels() {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = wsRoot.getProject(fProjectText.getText());
			if(project == null)
			{
				//Show error
				return;
			}
			
			try {
				IResource[] projectMembers = project.members();
				for (IResource iResource : projectMembers) {
					if(iResource instanceof IFolder){
						IFolder folder = (IFolder) iResource;
						String fName = folder.getName();
						if(fName.equals("model")){
							dtPath.setText(folder.getLocationURI().getPath());							
						}
					}
					else if(iResource instanceof IFile){
						IFile file = (IFile) iResource;
						if(file.getFileExtension().equals("csc")){
							contractPath.setText(file.getLocationURI().getPath());
						}
						else if(file.getFileExtension().equals("emx")){
							ctPath.setText(file.getLocationURI().getPath());
						}
					}
					
					
				}
				
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
			System.out.println(fProjectText.getText());
		}

		public void widgetSelected(SelectionEvent e)
		{
			System.out.println("Selected Project (widgetSelected):" + fProjectText.getText());
			// fOperationText.setEnabled(!fdebugInConsole.getSelection());		
			//updateLaunchConfigurationDialog();
		}

		
	}
}
