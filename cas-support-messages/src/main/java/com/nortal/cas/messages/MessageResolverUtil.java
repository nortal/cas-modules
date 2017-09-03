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

import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageResolver;

public class MessageResolverUtil {
	/**
	 * Get messageResolver for error messages by message code.
	 */
	public static MessageResolver getErrorMessageResolver(MessageInterface messageInterface) {
		return new MessageBuilder().error().code(messageInterface.getMessageCode()).build();
	}

	/**
	 * Get messageResolver for info messages by message code.
	 */
	public static MessageResolver getInfoMessageResolver(MessageInterface messageInterface) {
		return new MessageBuilder().info().code(messageInterface.getMessageCode()).build();
	}
}
