package com.nortal.cas.support.idcard;

import java.security.cert.X509Certificate;

import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.AbstractNonInteractiveCredentialsAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action that is similar to {@link X509CertificateCredentialsNonInteractiveAction}. In addition to obtaining client
 * certificate from request, that certificate may also be validated if needed
 * 
 * @author Priit Liivak
 * @author Allar Saarnak
 */
public final class IdCardCredentialsAction extends AbstractNonInteractiveCredentialsAction {
	public static final String CERTIFICATE_REQUEST_ATTRIBUTE = "javax.servlet.request.X509Certificate";

	public IdCardCredentialsAction(CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
			CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
			AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy) {
		super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver,
				adaptiveAuthenticationPolicy);
	}

	@Override
	protected Credential constructCredentialsFromRequest(final RequestContext context) {
		SharedAttributeMap<Object> sessionMap = context.getExternalContext().getSessionMap();
		final X509Certificate[] certificates = (X509Certificate[]) sessionMap.get(CERTIFICATE_REQUEST_ATTRIBUTE);
		// Certificate is removed from session after attempt
		sessionMap.remove(CERTIFICATE_REQUEST_ATTRIBUTE);
		return new IdCardCredential(certificates);
	}
}
