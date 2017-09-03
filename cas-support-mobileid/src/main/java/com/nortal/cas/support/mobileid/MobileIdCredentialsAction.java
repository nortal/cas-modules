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
