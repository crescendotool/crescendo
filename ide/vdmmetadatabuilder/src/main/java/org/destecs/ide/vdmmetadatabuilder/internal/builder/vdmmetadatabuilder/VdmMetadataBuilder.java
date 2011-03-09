package org.destecs.ide.vdmmetadatabuilder.internal.builder.vdmmetadatabuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.LocalDefinition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.Type;

public class VdmMetadataBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder
{

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException
	{
		IVdmProject project = (IVdmProject) getProject().getAdapter(IVdmProject.class);
		if (project != null)
		{
			Properties props = new Properties();

			IVdmModel model = project.getModel();

			for (IAstNode node : model.getRootElementList())
			{
				if (node instanceof SystemDefinition)
				{
					SystemDefinition sd = (SystemDefinition) node;

					List<String> values = new Vector<String>();
					for (Definition def : sd.getDefinitions())
					{
						if (def instanceof InstanceVariableDefinition)
						{
							save(props, def.getName(), toCsvString(getFields(getTypeName(def), model)));

						} else if (def instanceof ValueDefinition
								|| def instanceof LocalDefinition)
						{
							values.add(def.getName());
						}
					}

					save(props, sd.getName(), toCsvString(values));
				} else
				{
					List<String> values = new Vector<String>();
					for (Definition def : getDefinitions(node))
					{
						if (def instanceof ValueDefinition
								|| def instanceof LocalDefinition)
						{
							values.add(def.getName());
						}
					}
					save(props, node.getName(), toCsvString(values));
				}
			}

			IDestecsProject dp = (IDestecsProject) getProject().getAdapter(IDestecsProject.class);
			if (dp != null)
			{
				IFile file = dp.getVdmModelFolder().getFile(".metadata");

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try
				{
					props.store(out, "");
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (file.exists())
				{
					file.setContents(new ByteArrayInputStream(out.toByteArray()), IFile.FORCE, monitor);
				} else
				{
					file.create(new ByteArrayInputStream(out.toByteArray()), IFile.FORCE, monitor);
				}

			}
		}

		return null;
	}

	private void save(Properties props, String name, String list)
	{
		if (name.trim().length() > 0 && list.trim().length() > 0)
		{
			props.put(name, list);
		}
	}

	private String toCsvString(List<String> list)
	{
		StringBuffer sb = new StringBuffer();
		for (String string : list)
		{
			sb.append(",");
			sb.append(string);
		}
		sb.append(" ");
		return sb.substring(1).trim();
	}

	private String getTypeName(Definition def)
	{
		Type t = null;

		if (def instanceof InstanceVariableDefinition)
		{
			t = ((InstanceVariableDefinition) def).type;
		} else if (def instanceof ValueDefinition)
		{
			t = ((ValueDefinition) def).type;
		} else if (def instanceof LocalDefinition)
		{
			t = ((LocalDefinition) def).type;
		}

		String typeName = "";
		if (t instanceof OptionalType)
		{
			OptionalType opType = (OptionalType) t;
			typeName = opType.type.getName();

		} else
		{
			typeName = t.getName();
		}
		return typeName;
	}

	private List<String> getFields(String type, IVdmModel model)
	{
		List<String> variable = new Vector<String>();
		for (IAstNode node : model.getRootElementList())
		{
			if (!node.getName().equals(type))
			{
				continue;
			}
			for (Definition def : getDefinitions(node))
			{
				if (def instanceof InstanceVariableDefinition)
				// || def instanceof ValueDefinition || def instanceof LocalDefinition)
				{
					variable.add(def.getName());
				}
			}
		}
		return variable;
	}

	private List<Definition> getDefinitions(IAstNode node)
	{
		if (node instanceof ClassDefinition)
		{
			return ((ClassDefinition) node).getDefinitions();
		} else if (node instanceof Module)
		{
			return ((Module) node).defs;
		}
		return new Vector<Definition>();

	}
}
