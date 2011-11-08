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
package org.destecs.ide.simeng.listener;

import java.util.List;

import org.destecs.ide.simeng.ui.views.InfoTableView;

public class BaseListener
{
	protected InfoTableView view;
	private int count = 0;
	protected int refreshCount = 1;
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
