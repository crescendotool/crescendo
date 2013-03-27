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
package org.destecs.vdm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.destecs.vdmj.VDMCO;
import org.overture.config.Settings;

public class ErrorLog
{
	public static boolean show = true;
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static void log(Exception exception)
	{
		try
		{
			if (show)
			{
				exception.printStackTrace();
			}
			File outputFolder = null;
			if (VDMCO.outputDir != null)
			{
				outputFolder = VDMCO.outputDir;
			} else
			{
				outputFolder = new File(Settings.baseDir, "output");
			}
			PrintWriter out = new PrintWriter(new FileWriter(new File(outputFolder, "vdm_error.txt"), true));
			out.println();
			out.println("===============================================================================================");
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			out.println("Error time: " + sdf.format(cal.getTime()));
			exception.printStackTrace(out);
			out.println();
			out.println();
			out.close();
		} catch (IOException e)
		{
		}
	}

}
