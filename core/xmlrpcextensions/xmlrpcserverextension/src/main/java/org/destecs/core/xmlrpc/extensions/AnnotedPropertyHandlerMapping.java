package org.destecs.core.xmlrpc.extensions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.destecs.core.xmlrpc.extensions.RpcMethod;




public class AnnotedPropertyHandlerMapping extends PropertyHandlerMapping
{
	  /** Searches for methods in the given class. For any valid
     * method, it creates an instance of {@link XmlRpcHandler}.
     * Valid methods are defined as follows:
     * <ul>
     *   <li>They must be public.</li>
     *   <li>They must not be static.</li>
     *   <li>The return type must not be void.</li>
     *   <li>The declaring class must not be
     *     {@link java.lang.Object}.</li>
     *   <li>If multiple methods with the same name exist,
     *     which meet the above conditins, then an attempt is
     *     made to identify a method with a matching signature.
     *     If such a method is found, then this method is
     *     invoked. If multiple such methods are found, then
     *     the first one is choosen. (This may be the case,
     *     for example, if there are methods with a similar
     *     signature, but varying subclasses.) Note, that
     *     there is no concept of the "most matching" method.
     *     If no matching method is found at all, then an
     *     exception is thrown.</li>
     * </ul>
     * @param pKey Suffix for building handler names. A dot and
     * the method name are being added.
     * @param pType The class being inspected.
     */ 
	@SuppressWarnings("unchecked")
	@Override
    protected void registerPublicMethods(String pKey,
    		Class pType) throws XmlRpcException {
		Class keyInterface = null;
		try
		{
			 keyInterface =Thread.currentThread().getContextClassLoader().loadClass(pKey);
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	Map map = new HashMap();
        Method[] methods = pType.getMethods();
        for (int i = 0;  i < methods.length;  i++) {
            final Method method = methods[i];
            if (!isHandlerMethod(method)) {
                continue;
            }
            String name =null;
            
            if(keyInterface == null)
            {
            	name = pKey + "." + method.getName();
            }else
            {
            	name = /*pKey + "." +*/getMethodName(keyInterface, method);
            }
            Method[] mArray;
            Method[] oldMArray = (Method[]) map.get(name);
            if (oldMArray == null) {
                mArray = new Method[]{method};
            } else {
                mArray = new Method[oldMArray.length+1];
                System.arraycopy(oldMArray, 0, mArray, 0, oldMArray.length);
                mArray[oldMArray.length] = method;
            }
            map.put(name, mArray);
        }

        for (Iterator iter = map.entrySet().iterator();  iter.hasNext();  ) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            Method[] mArray = (Method[]) entry.getValue();
            handlerMap.put(name, newXmlRpcHandler(pType, mArray));
        }
    }

	@SuppressWarnings("unchecked")
	private String getMethodName(Class keyInterface, Method method)
	{
		for (Method iMethod : keyInterface.getMethods())
		{
			if(iMethod.getName().equals(method.getName()))
			{
				RpcMethod rpcMethod = iMethod.getAnnotation(RpcMethod.class);
				if(rpcMethod!=null)
				{
					return rpcMethod.methodName();
				}
				break;
			}
		}
		return method.getName();
	}
}
