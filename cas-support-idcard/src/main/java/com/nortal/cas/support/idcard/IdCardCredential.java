package com.nortal.cas.support.idcard;

import java.security.cert.X509Certificate;

import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;

public class IdCardCredential extends X509CertificateCredential {

	private static final long serialVersionUID = 1L;

	public IdCardCredential(X509Certificate[] certificates) {
		super(certificates);
	}

}
