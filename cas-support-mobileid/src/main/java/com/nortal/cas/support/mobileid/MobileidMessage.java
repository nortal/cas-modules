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
package com.nortal.cas.support.mobileid;

import com.nortal.cas.messages.Message;
import com.nortal.cas.messages.MessageInterface;

/**
 * MobileidMessage messages message code holder. <br/>
 * Used to create {@link Message} and find authentication message text.
 * 
 * @author Allar Saarnak
 */
public enum MobileidMessage implements MessageInterface {
	BAD_MOBILE_ID_AUTH("error.authentication.credentials.bad.mobile_id");

	private String messageCode;

	private MobileidMessage(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessageCode() {
		return messageCode;
	}
}
