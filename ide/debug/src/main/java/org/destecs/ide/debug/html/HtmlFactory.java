package org.destecs.ide.debug.html;

import java.util.List;
import java.util.Map;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.core.model.internal.DestecsDebugTarget;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;

public class HtmlFactory
{
	public static String createAcaResultOverview(String name,
			List<DestecsDebugTarget> completedTargets)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html>\n");
		sb.append("<header>" + style + "</header>\n");
		sb.append("<body>\n");

		sb.append("<h1>ACA Simulation - " + name + "</h1>\n");

		for (DestecsDebugTarget target : completedTargets)
		{
			try
			{
				sb.append("<h2><a href=\"" + target.getOutputFolder().getName()
						+ "\">" + target.getOutputFolder().getName()
						+ "</a></h2>\n");

				@SuppressWarnings("unchecked")
				Map<Object, Object> attributes = target.getLaunch().getLaunchConfiguration().getAttributes();

				sb.append("<table id=\"customers\">\n");
				sb.append("<tr>\n");
				sb.append("\t<th>Id</th>\n");
				sb.append("\t<th>Value</th>\n");
				sb.append("</tr>\n");
				boolean isEven = true;
				for (Map.Entry<Object, Object> entry : attributes.entrySet())
				{
					sb.append("<tr " + (isEven ? "class=\"alt\"" : "") + ">\n");
					sb.append("\t<td>" + entry.getKey() + "</td>\n");
					sb.append("\t<td>");
					if (entry.getKey().equals(IDebugConstants.DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES)
							|| entry.getKey().equals(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES)
							|| entry.getKey().equals(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM))
					{
						sb.append("\t\t<ul>\n");

						String splitter = ",";
						if(entry.getKey().equals(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM))
						{
							splitter = ";";
						}
						
						String[] items = entry.getValue().toString().split(splitter);
						for (String string : items)
						{
							sb.append("\t\t\t<li>" + string + "</li>\n");
						}

						sb.append("\t\t</ul>\n");
					} else
					{
						sb.append(entry.getValue());
					}
					sb.append("</td>\n");
					sb.append("</tr>\n");
					isEven = !isEven;
				}
				sb.append("</table>\n");

				if (target.getLaunch().getLaunchConfiguration().getMemento() != null)
				{
					sb.append("<h3>MEMENTO</h3>\n");
					sb.append("<p>"
							+ target.getLaunch().getLaunchConfiguration().getMemento()
							+ "</p>\n");
					;
				}

			} catch (DebugException e)
			{
			} catch (CoreException e)
			{
			}
		}

		sb.append("</body>\n");
		sb.append("</html>");

		return sb.toString();
	}

	private static final String style = "<style type=\"text/css\">#customers{font-family:\"Trebuchet MS\", Arial, Helvetica, sans-serif;width:100%;border-collapse:collapse;}#customers td, #customers th{font-size:1em;border:1px solid #98bf21;padding:3px 7px 2px 7px;}#customers th{font-size:1.1em;text-align:left;padding-top:5px;padding-bottom:4px;background-color:#A7C942;color:#ffffff;}#customers tr.alt td{color:#000000;background-color:#EAF2D3;}</style>";

}
