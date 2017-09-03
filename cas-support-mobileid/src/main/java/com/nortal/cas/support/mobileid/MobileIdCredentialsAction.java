/*
 * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license distributed with this file and available online at
 * http://www.ja-sig.org/products/cas/overview/license/
 */
package com.nortal.cas.support.mobileid;

import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import com.nortal.cas.support.mobileid.controller.MobileIdController;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;

/**
 * Used to create Credentials. Validation is done in {@link AuthenticationHandler} and {@link AuthenticationException} is thrown when
 * needed. <br>
 * Error messages are displayed to client from {@link AbstractInteractiveCredentialsAction} <br>
 * that is similar to {@link X509CertificateCredentialsNonInteractiveAction} that doesn't support error messages.
 * 
 * @author Priit Liivak
 * @author Allar Saarnak
 */
public final class MobileIdCredentialsAction extends AbstractNonInteractiveCredentialsAction {

   public MobileIdCredentialsAction(CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
			CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
			AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
		super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver,
				adaptiveAuthenticationPolicy);
	}

	@Override
   protected Credential constructCredentialsFromRequest(final RequestContext context) {
      @SuppressWarnings("rawtypes")
			SharedAttributeMap sessionMap = context.getExternalContext().getSessionMap();
      MobileIdAuth mobileIdAuthResult = (MobileIdAuth) sessionMap.get(MobileIdController.MOBILE_AUTH_SESSION_KEY);
      // Certificate is removed from session after attempt
      sessionMap.remove(MobileIdController.MOBILE_AUTH_SESSION_KEY);
      return new MobileIdCredential(mobileIdAuthResult);
   }

}
