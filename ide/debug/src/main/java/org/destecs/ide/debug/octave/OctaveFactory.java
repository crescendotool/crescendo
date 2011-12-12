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
		sb.append("function result = readResult ()\n");

		sb.append(createResultStruct("result", name, deCsvFile == null ? null
				: deCsvFile.getName(), ctCsvFile == null ? null
				: ctCsvFile.getName(), 0));

		sb.append("endfunction\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("## Load last result automatically\n");
		sb.append("lastrun = readResult();\n");

		return sb.toString();
	}

	private static String createResultStruct(String variableName, String name,
			String deFile, String ctFile, Integer index)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");

		sb.append("de" + index + " = "
				+ (deFile == null ? "\"\"" : "csvread(\"" + deFile + "\")")
				+ ";\n");
		sb.append("ct" + index + " = "
				+ (ctFile == null ? "\"\"" : "csvread(\"" + ctFile + "\")")
				+ ";\n");
		sb.append("\n");
		sb.append(variableName + " = struct(\"name\", \"" + name + "\",\n");
		sb.append("\"de\",de" + index + ",\n");
		sb.append("\"ct\",ct" + index + "\n");
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
		sb.append("result = struct(\"name\", \"" + name + "\",\n");
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
}
