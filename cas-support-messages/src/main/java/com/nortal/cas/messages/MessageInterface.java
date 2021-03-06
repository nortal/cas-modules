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

import org.springframework.binding.message.MessageResolver;

/**
 * Message code holder. <br/>
 * Used to create {@link Message} or {@link MessageResolver} using {@link MessageResolverUtil} and find message text. 
 * @author Allar Saarnak
 */
public interface MessageInterface {
	/**
	 * @return Code that is used to get the message text.
	 */
	String getMessageCode();
}
