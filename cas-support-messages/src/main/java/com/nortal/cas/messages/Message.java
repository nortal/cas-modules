/**
 *   Copyright 2017 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.nortal.cas.messages;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * MessageResolver for MessageInterface.<br/>
 * You can easily return or throw(checked) single or list of messages from any layer. <br/>
 * Or you could use getMessage() to find the messageText immediately.
 * @author Allar Saarnak
 */
public class Message extends DefaultMessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	/**
	 * Create message object.<br/>
	 * To use it later, for example:
	 * <pre>messageSource.getMessage(message, locale);</pre>
	 */
	public Message(MessageInterface message){
		super(new String[]{message.getMessageCode()}, message.getMessageCode());
	}

	/**
	 * Create message object with arguments.<br/>
	 * To use it later, for example:
	 * <pre>messageSource.getMessage(message, locale);</pre>
	 */
	public Message(MessageInterface message, Object... arguments){
		super(new String[]{message.getMessageCode()}, arguments, message.getMessageCode());
	}
	
	/**
	 * Finds the messageText. 
	 */
	public static String getMessage(MessageSource messageSource, Locale locale, MessageInterface message){
		return messageSource.getMessage(new Message(message), locale);
	}
	/**
	 * Finds the messageText. 
	 * <br/>Locale is taken from {@link LocaleContextHolder}.getLocale() <b>only works</b> with {@link DispatcherServlet}
	 */
	public static String getMessage(MessageSource messageSource, MessageInterface message){
		return getMessage(messageSource, getLocale(), message);
	}
	
	/**
	 * Finds the messageText. 
	 */
	public static String getMessage(MessageSource messageSource, Locale locale, MessageInterface message, Object... messageArguments){
		return messageSource.getMessage(new Message(message, messageArguments), locale);
	}
	
	/**
	 * Finds the messageText. 
	 * <br/>Locale is taken from {@link LocaleContextHolder}.getLocale() <b>only works</b> with {@link DispatcherServlet}
	 */
	public static String getMessage(MessageSource messageSource,  MessageInterface message, Object... messageArguments){
		return getMessage(messageSource, getLocale(), message, messageArguments);
	}

}
