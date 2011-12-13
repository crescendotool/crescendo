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

	public static String createResultScript(String name, File deCsvFile,
			File ctCsvFile)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("## Author: Kenneth Lausdahl\n");
		sb.append("1;\n");

		sb.append(getPlotDataFunction() + "\n\n\n");
		sb.append(getCsvParseHeaderFunction() + "\n\n\n");
		sb.append(getPlotLastFunction() + "\n\n\n");

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
		sb.append("#Comment the lines below to disable auto plotting\n");
		sb.append("plotrun(lastrun);\n");
		sb.append("pause;\n");

		return sb.toString();
	}

	private static String createResultStruct(String variableName, String name,
			String deFile, String ctFile, Integer index)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		sb.append("de_header" + index + " = "
				+ (deFile == null ? "\"\"" : "parseheader(\"" + deFile + "\")")
				+ ";\n");
		sb.append("de_data" + index + " = "
				+ (deFile == null ? "\"\"" : "csvread(\"" + deFile + "\")")
				+ ";\n");

		sb.append("ct_header" + index + " = "
				+ (ctFile == null ? "\"\"" : "parseheader(\"" + ctFile + "\")")
				+ ";\n");
		sb.append("ct_data" + index + " = "
				+ (ctFile == null ? "\"\"" : "csvread(\"" + ctFile + "\")")
				+ ";\n");

		sb.append("\n");
		sb.append(variableName + " = struct(\"name\", \"" + name.replace("_", "-") + "\",\n");
		sb.append("\"de\",struct(\"header\",de_header" + index
				+ ",\"data\",de_data" + index + "),\n");
		sb.append("\"ct\",struct(\"header\",ct_header" + index
				+ ",\"data\",ct_data" + index + ")\n");
		sb.append(");\n");
		sb.append("\n");
		return sb.toString();
	}

	public static String createAcaResultScript(String name,
			List<DestecsDebugTarget> completedTargets)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("## Author: Kenneth Lausdahl\n");
		sb.append("1;\n");
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
		sb.append("result = struct(\"name\", \"" + name.replace("_", "-") + "\",\n");
		sb.append("\"runs\",{");
		for (int j = 0; j < i; j++)
		{
			sb.append("result" + j);
			if (j + 1 < i)
			{
				sb.append(", ");
			}
		}
		sb.append("}\n");
		sb.append(");\n");
		sb.append("\n");
		sb.append("endfunction\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("## Load last result automatically\n");
		sb.append("lastaca = readAcaResult();\n");

		return sb.toString();
	}

	private static String getPlotLastFunction()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("function plotrun (runx) \n");
		sb.append("figure(1);\n");
		sb.append("subplot(2,1,1);\n");
		sb.append("plotdata(strcat(\"20-sim-\",runx.name), {runx.ct.header}, runx.ct.data);\n");
		sb.append("\n");
		//sb.append("figure(2);\n");
		sb.append("subplot(2,1,2);\n");
		sb.append("plotdata(strcat(\"VDM-\",runx.name), {runx.de.header}, runx.de.data);\n");
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
		sb.append("#split the string and remove all the spaces\n");
		sb.append("header = strtrim( strsplit(vstr,\",\",true) );\n");
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
		sb.append("l(1)=[];\n");
		sb.append("\n");
		sb.append("#plot the data\n");
		sb.append("plot( data(:,1), data(:, 2:curveCount ) );\n");
		sb.append("title(titleStr);\n");
		sb.append("xlabel( header{1} );\n");
		// sb.append("ylabel( (strvcat(header))(2:curveCount) );\n");
		sb.append("legend(strvcat(l));\n");
		sb.append("\n");
		sb.append("endfunction\n");
		return sb.toString();

	}
}
