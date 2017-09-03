package com.nortal.cas.support.esteid;

import java.security.GeneralSecurityException;

import org.apereo.cas.authentication.AbstractAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;

public class DelegateAuthenticationHandler extends AbstractAuthenticationHandler {

	private AuthenticationHandler handler;

	private String errorMessage;

	private Class<? extends Credential> supportedClass;

	public DelegateAuthenticationHandler(AuthenticationHandler handler, String errorMessage,
			Class<? extends Credential> supportedClass) {
		super(handler.getName(), null, null, handler.getOrder());
		this.handler = handler;
		this.errorMessage = errorMessage;
		this.supportedClass = supportedClass;
	}

	@Override
	public HandlerResult authenticate(Credential credential) throws GeneralSecurityException, PreventedException {
		try {
			return handler.authenticate(credential);
		}
		catch (GeneralSecurityException e) {
			if (errorMessage != null) {
				throw new GeneralSecurityException(errorMessage);
			}
			else {
				throw e;
			}
		}
	}

	@Override
	public boolean supports(Credential credential) {
		return credential != null && supportedClass.isAssignableFrom(credential.getClass());
	}

}
