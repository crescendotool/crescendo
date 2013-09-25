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
package org.destecs.ide.debug.launching.ui.aca;

import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExternalDseMainTab extends DseMainTab
{

	private Text hostText;
	private Button filterAnimationsButton ;

	@Override
	protected void createExtendableContent(Composite comp)
	{
		super.createExtendableContent(comp);

		Group group = new Group(comp, SWT.NONE);
		group.setText("External Server Configuration");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);
		
		
		
		Label hostLabel = new Label(group, SWT.NONE);
		hostLabel.setText("Host:");
		gd = new GridData(GridData.BEGINNING);
		hostLabel.setLayoutData(gd);
		
		
		hostText = new Text(group, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		hostText.setLayoutData(gd);
		hostText.addModifyListener(fListener);
		
		
		filterAnimationsButton= createCheckButton(group, "Filter Annimations");
		filterAnimationsButton.addSelectionListener(fListener);

	}
	
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		super.performApply(configuration);
		configuration.setAttribute(IDebugConstants.DESTECS_EXTERNAL_ACA_HOST ,hostText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_EXTERNAL_ACA_FILTER_ANNIMATIONS ,filterAnimationsButton.getSelection());
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		super.initializeFrom(configuration);
		try
		{
			filterAnimationsButton.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_EXTERNAL_ACA_FILTER_ANNIMATIONS ,false));
		} catch (CoreException e)
		{
		}
	}
}
