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
package org.destecs.ide.vdmmetadatabuilder.internal.builder.vdmmetadatabuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.vdmmetadatabuilder.VdmMetadataBuilderPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.PType;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;

public class VdmMetadataBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder
{

	private static final String BUS_TYPE_NAME = "#BUS";
	private static final String CPU_TYPE_NAME = "CPU";
	private static final String SYSTEM_TYPE_NAME = "_system";
	private static final String UNKNOWN = "unknown";

	protected IProject[] build(int kind,
			@SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException
	{
		try
		{
			expandedDefinitions.clear();
			IVdmProject project = (IVdmProject) getProject().getAdapter(IVdmProject.class);
			if (project != null)
			{
				Properties props = new Properties();

				IVdmModel model = project.getModel();

				if (!model.isTypeChecked())
				{
					if(!project.typeCheck(new NullProgressMonitor()))
					{
						props.put("TYPE_CHECK_STATUS", "false");
						storeProperties(monitor, props);
						return null;
					}
				}

				props.put("TYPE_CHECK_STATUS", "true");
				for (INode node : model.getRootElementList())
				{
					if (node instanceof ASystemClassDefinition)
					{
						ASystemClassDefinition sd = (ASystemClassDefinition) node;
						expandAndSave(props, "", sd, model);
						List<String> values = new Vector<String>();
						for (PDefinition def : sd.getDefinitions())
						{
							if (def instanceof AValueDefinition
									|| def instanceof ALocalDefinition)
							{
								values.add(def.getName());
							}
						}

						save(props, sd.getName(), toCsvString(values));
						expandAndSave(props, "", sd, model);
					} else
					{
						List<String> values = new Vector<String>();
						for (PDefinition def : getDefinitions(node))
						{
							if (def instanceof AValueDefinition
									|| def instanceof ALocalDefinition)
							{
								values.add(def.getName());

								String typeName = getTypeName(getVdmTypeName(def));
								if (!typeName.equals(UNKNOWN))
								{
									save(props, node.getName() + "."
											+ def.getName(), typeName + "," + "const");
								}
							}
							else
							if(def instanceof AInstanceVariableDefinition)
							{
								values.add(def.getName());

								String typeName = getTypeName(getVdmTypeName(def));
								if (!typeName.equals(UNKNOWN))
								{
									save(props, node.getName() + "."
											+ def.getName(), getNameTypeString(getVdmTypeName(def)));
								}
							}
							else
							if(def instanceof AExplicitOperationDefinition)
							{
								AExplicitOperationDefinition op = (AExplicitOperationDefinition) def;
								values.add(def.getName());
								
								save(props, node.getName() + "."
										+ def.getName(), "_operation"+ "," + (op.getAccess().getAsync()!=null  ? "async" : "sync") );
							}
							
						}
					}
				}

				storeProperties(monitor, props);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	protected void storeProperties(IProgressMonitor monitor, Properties props)
			throws CoreException
	{
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
				VdmMetadataBuilderPlugin.log("Failed to store metadatafile for project: "+getProject(), e);
			}

			if (file.exists())
			{
				file.setContents(new ByteArrayInputStream(out.toByteArray()), IFile.FORCE, monitor);
			} else if( dp.getVdmModelFolder().isAccessible() &&  dp.getVdmModelFolder().exists())
			{
				file.create(new ByteArrayInputStream(out.toByteArray()), IFile.FORCE, monitor);
			}

		}
	}

	Set<PDefinition> expandedDefinitions = new HashSet<PDefinition>();

	private void expandAndSave(Properties props, String prefix, PDefinition def,
			IVdmModel model)
	{
		expandAndSave(props, prefix, def, model, false);
	}

	private void expandAndSave(Properties props, String prefix, PDefinition def,
			IVdmModel model, boolean defIsField)
	{

		if (!prefix.isEmpty())
		{
			expandedDefinitions.add(def);
		}

		String name = prefix + (prefix == "" ? "" : ".") + def.getName();
		if (def instanceof ASystemClassDefinition)
		{
			save(props, name, SYSTEM_TYPE_NAME + ",systemclass");
		} else
		{
			
			
			
			save(props, name, getNameTypeString(getVdmTypeName(def)));
		}

		if (defIsField)
		{
			return;
		}

		for (PDefinition field : getFieldDefinitions(getTypeName(def), model))
		{
			PDefinition child = field;// getDefinition(getTypeName(field),
										// model);
			if (child != null)
			{
				if (!expandedDefinitions.contains(child))
				{

					expandAndSave(props, name, child, model, true);
				}
			}
		}
	}

	private String getNameTypeString(PType type) {
		if(type instanceof AClassType)//FIXMEif( type.isClass())
		{
			return getTypeName(type) + ",class";
		}
		else
		{
			return getTypeName(type) + ",variable";
		}
		
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

	private PType getVdmTypeName(INode node)
	{
		PType t = null;

		if (node instanceof AInstanceVariableDefinition)
		{
			t = ((AInstanceVariableDefinition) node).getType();
		} else if (node instanceof AValueDefinition)
		{
			t = ((AValueDefinition) node).getType();
		} else if (node instanceof ALocalDefinition)
		{
			t = ((ALocalDefinition) node).getType();
		}

		return t;

	}

	private String getTypeName(PType t)
	{
		if (t instanceof ARealNumericBasicType || t.isType("real") != null)
		{
			return "real";
		} else if (t instanceof AIntNumericBasicType || t.isType("int") != null)
		{
			return "int";
		} else if (t instanceof ANatNumericBasicType || t.isType("nat") != null)
		{
			return "nat";
		}else if (t instanceof ANatOneNumericBasicType || t.isType("nat1") != null)
		{
			return "nat1";
		}  else if (t instanceof ABooleanBasicType || t.isType("bool") != null)
		{
			return "bool";
		}else if (t instanceof ACharBasicType || t.isType("char") != null)
		{
			return "char";
		}
		 else if (t instanceof ASeqSeqType)
		 {
			 ASeqSeqType t1 = (ASeqSeqType) t;
		 return "seq of (" + getTypeName(t1.getSeqof()) + ")";
		 }
		else if (t instanceof AClassType)
		{
			AClassType ct = (AClassType) t;
			if(ct.getClassdef() instanceof ACpuClassDefinition)
			{
				return CPU_TYPE_NAME;
			}else if(ct.getClassdef() instanceof ABusClassDefinition)
			{
				return BUS_TYPE_NAME;
			}
			return ct.getName().name;// "Class";
		} else if (t instanceof AOptionalType)
		{
			return getTypeName(((AOptionalType) t).getType());
		}
		
//		if(t.isNumeric()) //this is trying to fit the value in a real (if it is compatible with real it is ok)
//		{
//			return "real";
//		}
		
		return UNKNOWN;
	}

	private String getTypeName(PDefinition def)
	{
		PType t = null;

		if (def instanceof AInstanceVariableDefinition)
		{
			t = ((AInstanceVariableDefinition) def).type;
		} else if (def instanceof AValueDefinition)
		{
			t = ((AValueDefinition) def).type;
		} else if (def instanceof ALocalDefinition)
		{
			t = ((ALocalDefinition) def).type;
		}

		String typeName = "";
		if (t instanceof AOptionalType)
		{
			AOptionalType opType = (AOptionalType) t;
			typeName = opType.type.getName();

		}
		if (t instanceof AClassType)
		{
			typeName = t.getName();
		} else if (t instanceof AOptionalType
				&& ((AOptionalType) t).type instanceof AClassType)
		{
			typeName = t.getName();

		} else
		{
			typeName = def.getName();
		}
		return typeName;
	}

	private List<PDefinition> getFieldDefinitions(String type, IVdmModel model)
	{
		List<PDefinition> variable = new Vector<PDefinition>();
		for (INode node : model.getRootElementList())
		{
			if (!node.getName().equals(type))
			{
				continue;
			}
			for (PDefinition def : getDefinitions(node))
			{
				if (def instanceof AInstanceVariableDefinition)
				{
					variable.add(def);
				}
			}
		}
		return variable;
	}

	private List<PDefinition> getDefinitions(INode node)
	{
		if (node instanceof AClassClassDefinition)
		{
			return ((AClassClassDefinition) node).getDefinitions();
		} else if (node instanceof AModuleModules)
		{
			return ((AModuleModules) node).getDefs();
		}
		return new Vector<PDefinition>();

	}
}
