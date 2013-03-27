package org.destecs.ide.debug.launching.ui.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.PType;
import org.overture.ide.core.IVdmModel;

public class VdmLogTreeContentProvider implements ITreeContentProvider
{

	private Set<PDefinition> visitedCount = new HashSet<PDefinition>();
	private Set<PDefinition> visited = new HashSet<PDefinition>();
	
	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		System.out.println("input changed");
		visited.clear();
		visitedCount.clear();
	}

	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IVdmModel)
		{
			IVdmModel model = (IVdmModel) inputElement;
			for (INode elem : model.getRootElementList())
			{
				if (elem instanceof ASystemClassDefinition)
				{
					return new Object[] { new TreeNodeContainer(null, (ASystemClassDefinition)elem,true) };
				}
			}
		}
		return new Object[] {};
	}
	
	private Object[] getChildren(Object parentElement, boolean addToVisited)
	{
		
		if(parentElement instanceof TreeNodeContainer)
		{
			TreeNodeContainer parentNode = ((TreeNodeContainer) parentElement);
			PDefinition def = parentNode.data;
			
			if(addToVisited && visited.contains(def))
			{
				return new Object[] {};
			}
			
			if(addToVisited)
			{
				System.out.println("Visiting def: " + def.getName());
				visited.add(def);
			}
			
			if(!addToVisited && visitedCount.contains(def))
			{
				return new Object[] {};
			}
			
			if(!addToVisited)
			{
				System.out.println("VisitingCount def: " + def.getName());
				visitedCount.add(def);
			}
			
			if (def instanceof AClassClassDefinition)
			{
				List<PDefinition> defs =  getChildrenOf((AClassClassDefinition) def);
				
				return encapsulateDefs(parentNode,defs);
			}
			if (def instanceof AInstanceVariableDefinition)
			{
				List<PDefinition> defs =  getChildrenOf((AInstanceVariableDefinition) def);
				return encapsulateDefs(parentNode,defs);
			}
			
		}
		return new Object[] {};
	}

	public Object[] getChildren(Object parentElement)
	{
		return getChildren(parentElement,true);
	}

	private Object[] encapsulateDefs(TreeNodeContainer parentNode, List<PDefinition> defs)
	{
		List<TreeNodeContainer> result = new Vector<TreeNodeContainer>();
		
		for (PDefinition definition : defs)
		{			
				result.add(new TreeNodeContainer(parentNode, definition,isVirtual(definition)));
			
		}
		
		return result.toArray();
	}

	private List<PDefinition> getChildrenOf(
			AInstanceVariableDefinition instance)
	{
		List<PDefinition> result = new Vector<PDefinition>();

		PType instanceType = instance.getType();

		if (instanceType instanceof AOptionalType)
		{
			instanceType = ((AOptionalType) instanceType).getType();
		}

		if (instanceType instanceof AClassType)
		{
			SClassDefinition internalClass = ((AClassType) instanceType).getClassdef();
			if (!(internalClass instanceof ACpuClassDefinition || internalClass instanceof ABusClassDefinition))
			{
				result.addAll(getChildrenOf(internalClass));
			}

		}

		return result;
	}

	private List<PDefinition> getChildrenOf(SClassDefinition c)
	{
		List<PDefinition> result = new Vector<PDefinition>();

		for (PDefinition def : c.getDefinitions())
		{
			if (def instanceof AInstanceVariableDefinition)
			{
				if (isWorthAdding(def))
				{
					result.add(def);
				}
			}
		}

		return result;
	}

	private boolean isVirtual(PDefinition def)
	{
		PType instanceType = def.getType();
		
		if (instanceType instanceof AOptionalType)
		{
			instanceType = ((AOptionalType) instanceType).getType();
		}

		if (instanceType instanceof AClassType)
		{
			return true;

		}
		
		return false;
		
	}
	
	private boolean isWorthAdding(PDefinition def)
	{
		PType instanceType = def.getType();

		if(def instanceof AInstanceVariableDefinition)
		{
			if (instanceType instanceof AOptionalType)
			{
				instanceType = ((AOptionalType) instanceType).getType();
			}

			if (instanceType instanceof AClassType)
			{
				SClassDefinition internalClass = ((AClassType) instanceType).getClassdef();
				if (internalClass instanceof ACpuClassDefinition
						|| internalClass instanceof ABusClassDefinition)
				{
					return false;
				} else
				{
					return true;
				}

			}
			return true;
		}
		
		

		return false;
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		return getChildren(element,false).length > 0;
	}

	

}
