/**
 * bargetorCommon
 * com.bargetor.service.common.bcp.version
 * BCPApiVersion.java
 * 
 * 2015年6月16日-下午10:22:50
 *  2015Bargetor-版权所有
 *
 */
package com.bargetor.service.common.bcp.version;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.Mapping;

/**
 *
 * BCPApiVersion
 * 
 * kin
 * kin
 * 2015年6月16日 下午10:22:50
 * 
 * @version 1.0.0
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface BCPAPIVersion {
	/**
     * 版本号
     * @return
     */
    int value();
}
