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
