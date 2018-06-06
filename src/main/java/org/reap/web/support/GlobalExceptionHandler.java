/*
 * Copyright (c) 2018-present the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.reap.web.support;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reap.CoreException;
import org.reap.common.ResponseCodes;
import org.reap.support.DefaultResult;
import org.reap.support.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器，统一将异常转换为 {@link Result}.
 * 
 * @author 7cat
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Log logger = LogFactory.getLog(GlobalExceptionHandler.class);

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler({ CoreException.class, Exception.class })
	@ResponseBody
	ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) {
		logger.error("handle exception: ", ex);
		if (!(ex instanceof CoreException)) {
			return new ResponseEntity<>(
					DefaultResult.newFailResult(ResponseCodes.RESPONSE_CODE_SYSTEM_ERROR, ex.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			CoreException core = (CoreException) ex;
			String responseMessage = core.getMessage() == null
					? messageSource.getMessage(core.getCode(), core.getArgs(), Locale.getDefault())
					: core.getMessage();
			// 对于正常的业务异常 HttpStatus 返回 OK 而非服务端错误
			return new ResponseEntity<>(DefaultResult.newFailResult(core.getCode(), responseMessage), HttpStatus.OK);
		}

	}
}
