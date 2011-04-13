package org.destecs.vdm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.destecs.vdmj.VDMCO;
import org.overturetool.vdmj.Settings;

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
				outputFolder = new File(Settings.DGBPbaseDir, "output");
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
