//package org.destecs.core.simulationengine.senario;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.StringTokenizer;
//
//import org.destecs.core.simulationengine.SimulationEngine.Simulator;
//
//public class ScenarioParser 
//{
//		public final static String LEX_ASSIGNMENT = ":=";
////		public final static String LEX_SEMICOLON = ";";
//
//		private File file;
//		BufferedReader reader = null;
//		int lineNo = 0;
//
//		public ScenarioParser(File file)
//		{
//			this.file = file;
//		}
//
//		private String nextLine() throws IOException
//		{
//			String text = null;
//			while ((text = reader.readLine()) != null)
//			{
//				lineNo++;
//				text = text.trim();
//				if (text.length() > 0 && !text.startsWith("--"))
////						&& (text.startsWith(LEX_CONTRACT)
////								|| text.startsWith(LEX_EVENT+" ")
////								|| text.startsWith(LEX_CONTROLLED)
////								|| text.startsWith(LEX_MONITORED)
////								|| text.startsWith(LEX_DESIGN_PARAMETER+" ") || text.startsWith(LEX_END)))
//				{
//					return text;
//				}
//			}
//			return text;
//		}
//
//		public Scenario parse()
//		{
//			try
//			{
//				lineNo = 0;
//				reader = new BufferedReader(new FileReader(file));
//				Scenario scenario = new Scenario();
//
//				String line = null;
//				
//				while ((line = nextLine()) != null)
//				{
//					
//					try{
//					Action action = new Action();
//					StringTokenizer st = new StringTokenizer(line);
//					
//					String time =st.nextToken();
//					action.time = Double.parseDouble(time);
//					
//					String actionString ="";
//					while (st.hasMoreElements())
//					{
//						actionString+=st.nextToken();
//					}
//					
//					String[] aSt = actionString.split("\\.");
//					
//					action.targetSimulator = Simulator.valueOf(aSt[0]);
//					
//					String exp ="";
//					for (int i = 1; i < aSt.length; i++)
//					{
//						exp += aSt[i]+".";
//						
//					}
//					exp = exp.substring(0, exp.length()-1);
//										
//					StringTokenizer expSt = new StringTokenizer(exp,LEX_ASSIGNMENT);
//					
//					action.variableName = expSt.nextToken();
//					
//					action.variableValue = Double.parseDouble(expSt.nextToken());
//					
//					
//						scenario.actions.add(action);
//					}catch(Exception e)
//					{
//						System.err.println("Error in line "+ lineNo+": "+line);
//					}
//				}
//				Collections.sort(scenario.actions);
//				return scenario;
//
//			} catch (FileNotFoundException e)
//			{
//				e.printStackTrace();
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			} finally
//			{
//				try
//				{
//					if (reader != null)
//					{
//						reader.close();
//					}
//				} catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			return null;
//		}
//		
//		public static void main(String[] args)
//		{
//			System.out.println(new ScenarioParser(new File(args[0])).parse().toString());
//		}
//	}