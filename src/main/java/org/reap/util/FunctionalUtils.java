/**
 * 
 */

package org.reap.util;

import java.util.Optional;

import org.reap.CoreException;

/**
 * 函数式风格程序的脚手架.
 * 
 * @author 7cat
 */
public final class FunctionalUtils {

	public static <T> T orElseThrow(Optional<T> optional, String errorCode) {
		return optional.orElseThrow(() -> new CoreException(errorCode));
	}
}
