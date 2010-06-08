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
			String packageFolder = getProjectJavaSrcDirectory().getAbsolutePath()+File.separatorChar+packagename.replace('.', File.separatorChar);
			XmlRpcJavaInterfaceGenerator.main(new String[]{new File(getResourcesDir(),xmldefinition).getAbsolutePath(),name,packagename,packageFolder});
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new MojoExecutionException("Error generating ", e);
		}
		
	}
	
	

}
