/**
 * 
 */
package org.destecs.protocol;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author kela
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcMethod
{
	String methodName();
}
