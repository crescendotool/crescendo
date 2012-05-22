package org.destecs.ide.debug.launching.ui.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.overture.ide.core.IVdmModel;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.Type;

public class VdmLogTreeContentProvider implements ITreeContentProvider
{

	private Set<Definition> visitedCount = new HashSet<Definition>();
	private Set<Definition> visited = new HashSet<Definition>();
	
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
			for (IAstNode elem : model.getRootElementList())
			{
				if (elem instanceof SystemDefinition)
				{
					return new Object[] { new TreeNodeContainer(null, (SystemDefinition)elem,true) };
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
			Definition def = parentNode.data;
			
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
			
			if (def instanceof ClassDefinition)
			{
				List<Definition> defs =  getChildrenOf((ClassDefinition) def);
				
				return encapsulateDefs(parentNode,defs);
			}
			if (def instanceof InstanceVariableDefinition)
			{
				List<Definition> defs =  getChildrenOf((InstanceVariableDefinition) def);
				return encapsulateDefs(parentNode,defs);
			}
			
		}
		return new Object[] {};
	}

	public Object[] getChildren(Object parentElement)
	{
		return getChildren(parentElement,true);
	}

	private Object[] encapsulateDefs(TreeNodeContainer parentNode, List<Definition> defs)
	{
		List<TreeNodeContainer> result = new Vector<TreeNodeContainer>();
		
		for (Definition definition : defs)
		{			
				result.add(new TreeNodeContainer(parentNode, definition,isVirtual(definition)));
			
		}
		
		return result.toArray();
	}

	private List<Definition> getChildrenOf(
			InstanceVariableDefinition instance)
	{
		List<Definition> result = new Vector<Definition>();

		Type instanceType = instance.type;

		if (instanceType instanceof OptionalType)
		{
			instanceType = ((OptionalType) instanceType).type;
		}

		if (instanceType instanceof ClassType)
		{
			ClassDefinition internalClass = ((ClassType) instanceType).classdef;
			if (!(internalClass instanceof CPUClassDefinition || internalClass instanceof BUSClassDefinition))
			{
				result.addAll(getChildrenOf(internalClass));
			}

		}

		return result;
	}

	private List<Definition> getChildrenOf(ClassDefinition c)
	{
		List<Definition> result = new Vector<Definition>();

		for (Definition def : c.getDefinitions())
		{
			if (def instanceof InstanceVariableDefinition)
			{
				if (isWorthAdding(def))
				{
					result.add(def);
				}
			}
		}

		return result;
	}

	private boolean isVirtual(Definition def)
	{
		Type instanceType = def.getType();
		
		if (instanceType instanceof OptionalType)
		{
			instanceType = ((OptionalType) instanceType).type;
		}

		if (instanceType instanceof ClassType)
		{
			return true;

		}
		
		return false;
		
	}
	
	private boolean isWorthAdding(Definition def)
	{
		Type instanceType = def.getType();

		if(def instanceof InstanceVariableDefinition)
		{
			if (instanceType instanceof OptionalType)
			{
				instanceType = ((OptionalType) instanceType).type;
			}

			if (instanceType instanceof ClassType)
			{
				ClassDefinition internalClass = ((ClassType) instanceType).classdef;
				if (internalClass instanceof CPUClassDefinition
						|| internalClass instanceof BUSClassDefinition)
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
