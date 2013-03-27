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
package org.destecs.ide.debug;

public interface IDebugConstants {
	
	public static final String PLUGIN_ID = "org.destecs.ide.debug";
	
	public static final String DESTECS_LAUNCH_CONFIG_PROJECT_NAME = "DESTECS_LAUNCH_CONFIG_PROJECT_NAME".toLowerCase();
	
	public static final String DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH = "DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH_DEFAULT = "__DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH_DEFAULT__";
	
	public static final String DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH = "DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_SCENARIO_PATH = "DESTECS_LAUNCH_CONFIG_SCENARIO_PATH".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_CONTRACT_PATH = "DESTECS_LAUNCH_CONFIG_CONTRACT_PATH".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_SIMULATION_TIME = "DESTECS_LAUNCH_CONFIG_SIMULATION_TIME".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM = "DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_DE_ENDPOINT = "DESTECS_LAUNCH_CONFIG_DE_ENDPOINT".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_CT_ENDPOINT = "DESTECS_LAUNCH_CONFIG_CT_ENDPOINT".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_CT_LEAVE_DIRTY_FOR_INSPECTION = "DESTECS_LAUNCH_CONFIG_CT_LEAVE_DIRTY_FOR_INSPECTION".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG = "DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_DE_REPLACE = "DESTECS_LAUNCH_CONFIG_DE_REPLACE".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE = "DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION = "DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION".toLowerCase();
	
	public static final String DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS = "DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS".toLowerCase();
	
	public static final String ATTR_DESTECS_PROGRAM = "org.destecs.ide.debug.launchConfigurationType";
	

	public static final String MESSAGE_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationMessagesView";
	public static final String ENGINE_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationEngineView";
	public static final String SIMULATION_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationView";

	public static final String DEFAULT_DE_ENDPOINT = "http://127.0.0.1:PORT/xmlrpc";
	public static final String DEFAULT_CT_ENDPOINT = "http://localhost:1580";

	public static final String VDMRT_CONTENT_TYPE_ID = "org.overture.ide.vdmrt.core.content-type";

	public static final String VDM_LAUNCH_CONFIG_DTC_CHECKS = "VDM_LAUNCH_CONFIG_DTC_CHECKS".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_INV_CHECKS = "VDM_LAUNCH_CONFIG_INV_CHECKS".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_POST_CHECKS = "VDM_LAUNCH_CONFIG_POST_CHECKS".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_MEASURE_CHECKS = "VDM_LAUNCH_CONFIG_MEASURE_CHECKS".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_PRE_CHECKS = "VDM_LAUNCH_CONFIG_PRE_CHECKS".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_GENERATE_COVERAGE = "VDM_LAUNCH_CONFIG_GENERATE_COVERAGE".toLowerCase();

	public static final String VDM_LAUNCH_CONFIG_LOG_RT = "VDM_LAUNCH_CONFIG_LOG_RT".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING = "DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO = "DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO".toLowerCase();
	
	public static final String DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES = "DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES".toLowerCase();
	public static final String DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES = "DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES".toLowerCase();

	//ACA
	public static final String DESTECS_ACA_BASE_CONFIG = "DESTECS_ACA_BASE_CONFIG".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX = "DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX".toLowerCase();

	public static final String DESTECS_ACA_INCREMENTAL_SDPS = "DESTECS_ACA_INCREMENTAL_SDPS".toLowerCase();
	public static final String DESTECS_ACA_VALUESET_SDPS = "DESTECS_ACA_VALUESET_SDPS".toLowerCase();
	
	public static final String DESTECS_LAUNCH_CONFIG_DEBUG = "DESTECS_LAUNCH_CONFIG_DEBUG".toLowerCase();

	public static final String DESTECS_ACA_ARCHITECTURES = "DESTECS_ACA_ARCHITECTURES".toLowerCase();

	public static final String DESTECS_ACA_SCENARIOS = "DESTECS_ACA_SCENARIOS".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_USE_REMOTE_CT_SIMULATOR = "DESTECS_LAUNCH_CONFIG_USE_REMOTE_CT_SIMULATOR".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_REMOTE_PROJECT_BASE = "DESTECS_LAUNCH_CONFIG_REMOTE_PROJECT_BASE1".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS = "DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS".toLowerCase();

	public static final String DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS = "DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS".toLowerCase();

	public static final String DESTECS_ACA_20SIM_IMPLEMENTATIONS = "DESTECS_ACA_20SIM_IMPLEMENTATIONS".toLowerCase();
	
	public static final String IMPLEMENTATION_PREFIX = "model.implementations.";

	public static final String DESTECS_ACA_20SIM_SETTINGS = "DESTECS_ACA_20SIM_SETTINGS".toString();

	public static final String OCTAVE_PATH = "org.destecs.ide.debug.octavepath";

	public static final String DEFAULT_OCTAVE_PATH = "c:\\Octave\\3.2.4_gcc-4.4.0\\bin\\octave.exe";

	public static final String OCTAVE_PLOT_FILE = "results.m";

	public static final String DESTECS_DIRECTORY_LAUNCH_FOLDER = "DESTECS_DIRECTORY_LAUNCH_FOLDER".toLowerCase();

	public static final int DEFAULT_DEBUG_PORT = 8080;
}
