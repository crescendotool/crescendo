package org.destecs.ide.simeng.internal.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class ProcessConsolePrinter extends Thread
{
	
	InputStream stream = null;

	public ProcessConsolePrinter( InputStream inputStream) {
		setDaemon(true);
		this.stream = inputStream;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null)
			{
				System.out.println(line);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
