package com.nortal.cas.support.mobileid;

import static org.slf4j.LoggerFactory.getLogger;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;
import org.slf4j.Logger;

import com.nortal.cas.support.mobileid.enums.MobileIdStatus;
import com.nortal.cas.support.mobileid.model.MobileIdAuth;

public class MobileIdCredential extends X509CertificateCredential {
	private static final Logger logger = getLogger(MobileIdCredential.class);

	private static final long serialVersionUID = 1L;

	private static final String START_CERTIFICATE = "-----BEGIN CERTIFICATE-----\n";

	private static final String END_CERTIFICATE = "\n-----END CERTIFICATE-----\n";

	public MobileIdCredential(MobileIdAuth mobileIdAuthResult) {
		super(toCertificates(mobileIdAuthResult));
	}

	private static X509Certificate[] toCertificates(MobileIdAuth mobileIdAuthResult) {
		X509Certificate cert = toCertificate(mobileIdAuthResult);
		if (cert != null) {
			return new X509Certificate[] { cert };
		} else {
			return new X509Certificate[] {};
		}
	}

	private static X509Certificate toCertificate(MobileIdAuth mobileIdAuthResult) {
		if (mobileIdAuthResult == null || StringUtils.isBlank(mobileIdAuthResult.getCertificateData())
				|| !MobileIdStatus.USER_AUTHENTICATED.equals(mobileIdAuthResult.getStatus())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Certificates not found in mobile auth result or status is incorrect: " + mobileIdAuthResult);
			}
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Certificate found in mobile id auth result.");
		}

		X509Certificate cert = null;
		StringBuilder fullCertificate = new StringBuilder(START_CERTIFICATE);
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			fullCertificate.append(mobileIdAuthResult.getCertificateData());
			fullCertificate.append(END_CERTIFICATE);

			if (logger.isTraceEnabled()) {
				logger.trace(
						"parsing certificate for: " + mobileIdAuthResult.getPhoneNo() + " cert:\n" + fullCertificate.toString());
			}

			cert = (X509Certificate) certificateFactory
					.generateCertificate(new StringInputStream(fullCertificate.toString()));
		}
		catch (CertificateException e) {
			logger.error("Could not parse mobile id certificate for: " + mobileIdAuthResult.getPhoneNo() + " Certificate:\n"
					+ fullCertificate.toString(), e);
			e.printStackTrace();
		}
		return cert;
	}
}
