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
package org.destecs.tools.plugins;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.destecs.tools.jprotocolgenerator.XmlRpcJavaInterfaceGenerator;

/**
 * Generate Java source form an AST file
 * 
 * @goal java

 */
public class GenerateJava extends AstGenBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Generating Java Interface");
		generate();
		
	}

	

	private void generate() throws MojoExecutionException,
			MojoFailureException
	{
		
		try{
			String packageFolder = outputDirectory.getAbsolutePath();//+File.separatorChar+packagename.replace('.', File.separatorChar);
			XmlRpcJavaInterfaceGenerator.main(new String[]{new File(getResourcesDir(),xmldefinition).getAbsolutePath(),name,packagename,packageFolder});
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new MojoExecutionException("Error generating ", e);
		}
		
	}

}
