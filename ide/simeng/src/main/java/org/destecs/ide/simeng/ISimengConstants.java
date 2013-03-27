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
package org.destecs.ide.simeng;

import org.destecs.ide.core.IDestecsCoreConstants;

public interface ISimengConstants
{

	public static final String PLUGIN_ID = "org.destecs.ide.simeng";
	public static final String VDM_ENGINE_BUNDLE_IDS[] = {PLUGIN_ID,IDestecsCoreConstants.PLUGIN_ID};// "org.destecs.ide.generated.vdm";
	public static final String VDM_ENGINE_CLASS = "org.destecs.vdm.CoSim";
	
	
	public static final String VDM_ENGINE_SIMULATION_DT_PATH = "org.destecs.ide.simeng.preferences.dtpath";
	public static final String VDM_ENGINE_SIMULATION_CT_PATH = "org.destecs.ide.simeng.preferences.ctpath";
	public static final String VDM_ENGINE_SIMULATION_CONTRACT_PATH = "org.destecs.ide.simeng.preferences.contractpath";
	public static final String VDM_ENGINE_SIMULATION_SDP_PATH = "org.destecs.ide.simeng.preferences.shareddesignparameterspath";
	public static final String VDM_ENGINE_SIMULATION_SCENATIO_PATH = "org.destecs.ide.simeng.preferences.scenariopath";
	public static final String VDM_ENGINE_SIMULATION_TOTAL_SIMULATION_TIME = "org.destecs.ide.simeng.preferences.totalsimulationtime";
	public static final String CLP_20_SIM_REGKEY =  "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\20-sim 4.3";
	public static final String CLP_20_SIM_REGKEY_x64 =  "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\20-sim 4.3";
	public static final String CLP_20_SIM_PATH_REGKEY = "DisplayIcon";

}
