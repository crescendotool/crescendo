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
