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
package org.destecs.ide.debug.octave;

import java.io.File;
import java.util.List;

import org.destecs.ide.debug.core.model.internal.DestecsDebugTarget;

public class OctaveFactory
{
	static int destecsPlotResolution = 100;

	public static String createResultScript(String name, File deCsvFile,
			File ctCsvFile, boolean autoShow)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("## Author: Kenneth Lausdahl\n");
		sb.append("1;\n\n");

		sb.append("global destecsPlotResolution = " + destecsPlotResolution
				+ ";\n\n\n");

		sb.append(getPlotDataFunction() + "\n\n\n");
		sb.append(getCsvParseHeaderFunction() + "\n\n\n");
		sb.append(getPlotLastFunction() + "\n\n\n");
		sb.append(getReduceCsv() + "\n\n\n");

		sb.append("\n\n");
		sb.append("function result = readResult ()\n");

		sb.append(createResultStruct("result", name, deCsvFile == null ? null
				: deCsvFile.getName(), ctCsvFile == null ? null
				: ctCsvFile.getName(), 0));

		sb.append("endfunction\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("## Load last result automatically\n");
		sb.append("lastrun = readResult();\n");
		sb.append("\n");
		sb.append("\n");
		if (autoShow)
		{
			sb.append("#Comment the lines below to disable auto plotting\n");
			sb.append("plotrun(lastrun);\n");
			sb.append("pause;\n");
		}
		return sb.toString();
	}

	private static String createResultStruct(String variableName, String name,
			String deFile, String ctFile, Integer index)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		sb.append("if exist(\"" + deFile + "\",\"file\") == 0\n");
		sb.append("\tde_header" + index + " = [];\n");
		sb.append("\tde_data" + index + " = [];\n");
		sb.append("else\n");
		sb.append("de_header" + index + " = "
				+ (deFile == null ? "\"\"" : "parseheader(\"" + deFile + "\")")
				+ ";\n");
		sb.append("de_data" + index + " = "
				+ (deFile == null ? "\"\"" : "csvread(\"" + deFile + "\")")
				+ ";\n");
		sb.append("endif;\n");

		sb.append("if exist(\"" + ctFile + "\",\"file\") == 0\n");
		sb.append("\tct_header" + index + " = [];\n");
		sb.append("\tct_data" + index + " = [];\n");
		sb.append("else\n");
		sb.append("ct_header" + index + " = "
				+ (ctFile == null ? "\"\"" : "parseheader(\"" + ctFile + "\")")
				+ ";\n");
		sb.append("ct_data" + index + " = "
				+ (ctFile == null ? "\"\"" : "csvread(\"" + ctFile + "\")")
				+ ";\n");
		sb.append("endif;\n");

		sb.append("\n");
		sb.append(variableName + " = struct(\"name\", \""
				+ name.replace("_", "-") + "\",\n");
		sb.append("\"de\",struct(\"header\",{de_header" + index
				+ "},\"data\",{reduceCsv(de_data" + index + ")}),\n");
		sb.append("\"ct\",struct(\"header\",{ct_header" + index
				+ "},\"data\",{reduceCsv(ct_data" + index + ")})\n");
		sb.append(");\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("#Clean up\n");
		sb.append("clear de_data" + index + ";\n");
		sb.append("clear ct_data" + index + ";\n");
		return sb.toString();
	}

	public static String createAcaResultScript(String name,
			List<DestecsDebugTarget> completedTargets, boolean autoShow)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("## Author: Kenneth Lausdahl\n");
		sb.append("1;\n\n");

		sb.append("global destecsPlotResolution = " + destecsPlotResolution
				+ ";\n\n\n");

		sb.append(getCsvParseHeaderFunction());
		sb.append("\n");
		sb.append("\n");
		sb.append(getAddnumtolegend());
		sb.append("\n");
		sb.append("\n");
		sb.append(getPlotMultipleRuns());
		sb.append("\n");
		sb.append("\n");
		sb.append(getReduceCsv() + "\n\n\n");
		sb.append("function result = readAcaResult ()\n");

		sb.append("\n");

		Integer i = 0;
		for (DestecsDebugTarget target : completedTargets)
		{
			String targetName = target.getOutputFolder().getName();
			String targetDeCsv = target.getDeCsvFile() == null ? null
					: target.getOutputFolder().getName() + "\\\\"
							+ target.getDeCsvFile().getName();
			String targetCtCsv = target.getCtCsvFile() == null ? null
					: target.getOutputFolder().getName() + "\\\\"
							+ target.getCtCsvFile().getName();

			sb.append(createResultStruct("result" + i, targetName, targetDeCsv, targetCtCsv, i));
			i++;
		}

		sb.append("\n");
		sb.append("result = struct(\"name\", \"" + name.replace("_", "-")
				+ "\",\n");
		sb.append("\"runs\",{{");
		for (int j = 0; j < i; j++)
		{
			sb.append("result" + j);
			if (j + 1 < i)
			{
				sb.append(", ");
			}
		}
		sb.append("}}\n");
		sb.append(");\n");
		sb.append("\n");
		sb.append("endfunction\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("## Load last result automatically\n");
		sb.append("lastaca = readAcaResult();\n");

		if (autoShow)
		{
			sb.append("#Comment the lines below to disable auto plotting\n");
			sb.append("plotmultipleruns(lastaca);\n");
			sb.append("whos -all;\n");
			sb.append("pause;\n");
		}

		return sb.toString();
	}

	private static String getPlotLastFunction()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function plotrun (runx) \n");
		sb.append("figure(1);\n");
		sb.append("if (length(runx.de.data) != 0 && length(runx.ct.data)!=0)\n");
		sb.append("subplot(2,1,1);\n");
		sb.append("endif;");
		sb.append("if (length(runx.ct.data) != 0) \n");
		sb.append("plotdata(strcat(\"20-sim-\",runx.name), runx.ct.header, runx.ct.data);\n");
		sb.append("endif;");
		sb.append("\n");
		// sb.append("figure(2);\n");
		sb.append("if (length(runx.de.data) != 0 && length(runx.ct.data)!=0)\n");
		sb.append("subplot(2,1,2);\n");
		sb.append("endif;");
		sb.append("if (length(runx.de.data) != 0) \n");
		sb.append("plotdata(strcat(\"VDM-\",runx.name), runx.de.header, runx.de.data);\n");
		sb.append("endif;");
		sb.append("endfunction\n");
		return sb.toString();
	}

	private static String getCsvParseHeaderFunction()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function header = parseheader (filename) \n");
		sb.append("#open the file\n");
		sb.append("[vfid, vmsg] = fopen (filename, \"r\");\n");
		sb.append("if (isempty (vmsg) == false), error (vmsg); endif\n");
		sb.append("\n");
		sb.append("#Get the first line in the file (the header)\n");
		sb.append("vstr = fgetl (vfid);\n");
		sb.append("\n");
		sb.append("if(vstr == -1) header = cellstr([\"time\";]);\n");
		sb.append("else\n");
		sb.append("vstr = strrep(vstr,\"\\\"\",\"\");\n");
		sb.append("#split the string and remove all the spaces\n");
		sb.append("header = strtrim( strsplit(vstr,\",\",true) );\n");
		sb.append("endif;\n");
		sb.append("fclose(vfid);\n");
		sb.append("endfunction\n");
		return sb.toString();
	}

	private static String getPlotDataFunction()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function plotdata (titleStr, header, data)\n");
		sb.append("#number of curves in the file\n");
		sb.append("curveCount = length(header);\n");
		sb.append("\n");
		sb.append("l = header;\n");
		sb.append("\n");
		sb.append("#plot the data\n");

		sb.append("	    timeCol = 1;\n");
		sb.append("	    for i=1:curveCount\n");
		sb.append("	    	    if(strcmpi(header{1,i},\"time\")==1)\n");
		sb.append("	    	    	    timeCol = i;\n");
		sb.append("	    	    endif;\n");
		sb.append("	    end;\n");
		sb.append("	    tmpData = data;\n");
		sb.append("	    tmpData(:,timeCol)=[];#Remove TimeCol\n");
		sb.append("	    l(timeCol)=[];#Remove TimeCol\n");
		sb.append("plot( data(:,timeCol), tmpData(:, 1:curveCount-1 ) );\n");
		sb.append("title(titleStr);\n");
		sb.append("xlabel( header{timeCol} );\n");
		// sb.append("ylabel( (strvcat(header))(2:curveCount) );\n");
		sb.append("legend(strvcat(l));\n");
		sb.append("\n");
		sb.append("endfunction\n");
		return sb.toString();

	}

	private static String getAddnumtolegend()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function runlegend = addnumtolegend (count)\n");
		sb.append("runlegend = {};\n");
		sb.append("	    \n");
		sb.append("for i=1:count\n");
		sb.append("		runlegend{i} = [\"run\" int2str(i)];\n");
		sb.append("end\n");
		sb.append("   \n");
		sb.append("endfunction\n");
		return sb.toString();
	}

	private static String getPlotMultipleRuns()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("function plotmultipleruns (data)\n");
		sb.append("	    #data must be a struct with name and runs field.\n");
		sb.append("	    #name field will be used as title for the plot\n");
		sb.append("	    #runs field contains each run. each run has the DE & CT Data\n");
		sb.append("\n");
		sb.append("	    #number of runs\n");
		sb.append("	    runs = data.runs;\n");
		sb.append("	    runCount = length(runs);\n");
		sb.append("\tfigNum = 1;\n");
		sb.append("\tplotPrFig = 3;\n");
		sb.append("\n");
		sb.append("	    #\n");
		sb.append("	    #plot the ct data\n");
		sb.append("	    #\n");
		sb.append("	    ct_header = runs{1}.ct.header;\n");
		sb.append("	    ct_curveCount = length(ct_header) - 1;\n");
		sb.append("\n");
		sb.append("\tif (ct_curveCount>0)\n");
		sb.append("	    #create new figure\n");
		sb.append("	    figure(figNum, \"name\", [\"ct-\" data.name] );\n");
		sb.append("\n");
		sb.append("	    plotindex = 1;\n");
		sb.append("	    timeCol = 1;\n");
		sb.append("	    for i=1:ct_curveCount\n");
		sb.append("	    	    if(strcmpi(ct_header{1+i},\"time\")==1)\n");
		sb.append("	    	    	    timeCol = i;\n");
		sb.append("	    	    endif;\n");
		sb.append("	    end;\n");
		sb.append("	    for i=1:ct_curveCount\n");
		sb.append("\n");

		sb.append("	    if(strcmpi(ct_header{1+i},\"time\")==1)\n");
		sb.append("	    	    continue;\n");
		sb.append("	    endif;\n");

		sb.append("	        subplot( plotPrFig , 1, plotindex);\n");
		sb.append("	        hold on;\n");
		sb.append("\n");
		sb.append("	        for j=1:runCount\n");
		sb.append("	            ct_time = runs{j}.ct.data(:,timeCol+1);\n");
		sb.append("	            ct_data = runs{j}.ct.data(:,1+i);\n");
		sb.append("\n");
		sb.append("	            plot( ct_time, ct_data, int2str(j));\n");
		sb.append("	        end\n");
		sb.append("\n");
		sb.append("	        ylabel( ct_header{1+i} );\n");
		sb.append("			legend ('right');legend('boxon');\n");
		sb.append("	        legend( addnumtolegend(runCount), \"location\", 'eastoutside' );\n");
		sb.append("	        plotindex = plotindex + 1;\n");

		sb.append("\tif(plotindex ==plotPrFig+1 && i<ct_curveCount)\n");
		sb.append("		xlabel(\"time {s}\");\n");
		sb.append("\tfigNum = figNum+1;\n");
		sb.append("\tfigure(figNum, \"name\", [\"ct-\" data.name] );\n");
		sb.append("\tplotindex=1;\n");
		sb.append("\tendif;");

		sb.append("	    end\n");
		sb.append("	    xlabel(\"time {s}\");\n");
		sb.append("\tendif;\n");
		sb.append("\n");
		sb.append("	    #\n");
		sb.append("\tfigNum = figNum+1;\n");
		sb.append("	    #plot the de data\n");
		sb.append("	    #\n");
		sb.append("	    de_header = runs{1}.de.header;\n");
		sb.append("	    de_curveCount = length(de_header) - 1;\n");
		sb.append("\n");
		sb.append("\tif (de_curveCount>0)\n");
		sb.append("	    #create new figure\n");
		sb.append("	    figure(figNum, \"name\", [\"de-\" data.name] );\n");
		sb.append("\n");
		sb.append("	    plotindex = 1;\n");
		sb.append("	    timeCol = 1;\n");
		sb.append("	    for i=1:ct_curveCount\n");
		sb.append("	    	    if(strcmpi(de_header{1+i},\"time\")==1)\n");
		sb.append("	    	    	    timeCol = i\n");
		sb.append("	    	    endif;\n");
		sb.append("	    end;\n");
		sb.append("	    for i=1:de_curveCount\n");
		sb.append("\n");
		sb.append("	    if(strcmpi(de_header{1+i},\"time\")==1)\n");
		sb.append("	    	    continue;\n");
		sb.append("	    endif;\n");
		sb.append("	        subplot( plotPrFig , 1, plotindex);\n");
		sb.append("	        hold on;\n");
		sb.append("\n");
		sb.append("	        for j=1:runCount\n");
		sb.append("				de_time = runs{j}.de.data(:,timeCol+1);\n");
		sb.append("         	de_data = runs{j}.de.data(:,1+i);\n");
		sb.append("\n");
		sb.append("        		stairs( de_time, de_data, int2str(j));\n");
		sb.append("     	end\n");
		sb.append("\n");
		sb.append("     	ylabel( de_header{1+i} );\n");
		sb.append("			legend ('right');legend('boxon');\n");
		sb.append("     	legend( addnumtolegend(runCount) , \"location\", 'eastoutside');\n");
		sb.append("    		plotindex = plotindex + 1;\n");

		sb.append("\tif(plotindex ==plotPrFig+1 && i<de_curveCount)\n");
		sb.append("\tfigNum = figNum+1;\n");
		sb.append("		xlabel(\"time {s}\");\n");
		sb.append("\tfigure(figNum, \"name\", [\"de-\" data.name] );\n");
		sb.append("\tplotindex=1;\n");
		sb.append("\tendif;");

		sb.append("		end\n");
		sb.append("		xlabel(\"time {s}\");\n");
		sb.append("\tendif;\n");
		sb.append("   \n");
		sb.append("endfunction\n");
		return sb.toString();
	}

	private static String getReduceCsv()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function res = reduceCsv (data)\n");
		sb.append("\tglobal destecsPlotResolution;\n");
		sb.append("\tres= data(1:ceil (rows(data)/10):end,:) ;\n");
		sb.append("endfunction\n");
		return sb.toString();
	}
}
