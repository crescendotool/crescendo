package org.destecs.ide.simeng.internal.core;

import java.util.List;

import org.destecs.ide.simeng.ui.views.InfoTableView;

public class BaseListener
{
	protected InfoTableView view;
	private int count = 0;
	protected int refreshCount = 50;
	protected int initialColPack=3;
	
	public BaseListener(InfoTableView view)
	{
		this.view = view;
		this.view.resetBuffer();
	}

	
	public void insetData(List<String> data)
	{	
		if(count==initialColPack)
		{
			view.packColumns();
		}
		
		if(count==refreshCount)
		{
			view.refreshList();
			count=0;
		}
		count++;
		view.setDataList(data);
	}
}
