package org.destecs.ide.debug.launching.ui.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.protocol.ProxyICoSimProtocol;


public class SettingItem
{
	public enum ValueType
	{
		Bool, Real, RealPositive, Enum, String, Unknown, Double
	}

	public final String key;
	public String value;
	public final List<String> enumerations = new Vector<String>();
	public final ValueType type;
	public final HashMap<String, String> propertiesMap;

	public SettingItem(String key, String value, List<String> values,
			String type, HashMap<String, String> propertiesMap)
	{
		this.key = key;
		this.value = value;
		this.enumerations.addAll(values);
		this.type = convertType(type);
		this.propertiesMap = propertiesMap;

	}

	private ValueType convertType(String type)
	{
		if (type.equals("string"))
		{
			return ValueType.String;
		}
		if (type.equals("boolean"))
		{
			return ValueType.Bool;
		}
		if (type.equals("double"))
		{
			return ValueType.Double;
		}

		return ValueType.Unknown;
	}

	@Override
	public String toString()
	{
		return getShortName() + " = " + this.value + " possible values: "
				+ enumerations;
	}

	public String getShortName()
	{
		return key.substring(16);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SettingItem)
		{
			return this.key.equals(((SettingItem) obj).key);
		}
		return super.equals(obj);
	}

	public static void readSettingsFromProtocol(ProxyICoSimProtocol protocol,
			Set<SettingItem> settingItems) throws Exception
	{
		/*
		 * Querying 20sim settings
		 */
		settingItems.clear();
		/*
		 * Querying multiple implementations - transforming to settings
		 */
		List<Map<String, Object>> implementations = protocol.queryImplementations();
		for (Map<String, Object> map : implementations) {
			
			String name = (String) map.get("name");
			String value = (String) map.get("implementation");
			
			Object[] enumerations = (Object[]) map.get("implementations");
			List<String> enumerationsVector = new Vector<String>();
				
			for (Object object : enumerations) {
				if(object instanceof String)
				{
					enumerationsVector.add((String) object);
				}
			}
			
			
			SettingItem settingItem = new SettingItem(IDebugConstants.IMPLEMENTATION_PREFIX + name, value, enumerationsVector, "string", new HashMap<String, String>());
			settingItems.add(settingItem);
		}
		
		List<Map<String, Object>> getst = protocol.querySettings(new Vector<String>(Arrays.asList(new String[] {})));
		for (Map<String, Object> elem : getst)
		{
			Object[] enumerations = (Object[]) elem.get("enumerations");
			List<String> enumerationsVector = new Vector<String>();
				
			for (Object object : enumerations) {
				if(object instanceof String)
				{
					enumerationsVector.add((String) object);
				}
			}
			
			Object[] properties = (Object[]) elem.get("properties");
			
			HashMap<String, String> propertiesMap = new HashMap<String, String>();
			
			for (Object object : properties) {
				if(object instanceof HashMap)
				{
					@SuppressWarnings("unchecked")
					HashMap<String, Object> singlePropertyMap = (HashMap<String, Object>) object;
					String key = (String) singlePropertyMap.get("key");
					String value = (String) singlePropertyMap.get("value");
					propertiesMap.put(key, value);
				}
			}
			
			SettingItem item = new SettingItem(elem.get("key").toString(), elem.get("value").toString(), enumerationsVector  , elem.get("type").toString(),propertiesMap);
			settingItems.add(item);
		}
		
		
	}
}
