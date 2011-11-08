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
package org.destecs.core.sdp;

import java.util.HashMap;

public class SdpFactory {
	
	private HashMap<String, Object> sdps = null;
	
	public SdpFactory() {
		sdps = new HashMap<String, Object>();
		
	}
	
	public void addSdp(String key, Object value){
		sdps.put(key, value);
	}
	
	public HashMap<String, Object> getSdps(){
		return sdps;
	}
	
	
}
