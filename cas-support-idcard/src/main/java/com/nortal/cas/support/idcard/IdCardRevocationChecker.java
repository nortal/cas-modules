package com.nortal.cas.support.idcard;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.validation.constraints.NotNull;

import org.apereo.cas.adaptors.x509.authentication.revocation.checker.RevocationChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.factory.NotaryFactory;

public class IdCardRevocationChecker implements RevocationChecker {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@NotNull
	private JdigidocConfigurationInitializer jdigidocConfigurationInitializer;

	// If certificate should be validated
	private boolean validateIdCertificate;

	@Override
	public void check(X509Certificate certificate) throws GeneralSecurityException {
		if (!validateCertificate(certificate)) {
			log.debug("Certificate was found to be invalid");
			throw new GeneralSecurityException();
		}
	}
	
	/**
	 * Special validation logic that applies to Estonian id card validation. Checks if id card was closed or stolen!
	 * 
	 * @param cert
	 *          Certificate that has allready passed generic validations.
	 * @return true if valid or validation disabled
	 */
	@SuppressWarnings("deprecation")
	private boolean validateCertificate(X509Certificate cert) {
		if (!validateIdCertificate) {
			return true;
		}

		NotaryFactory notary = jdigidocConfigurationInitializer.getNotary();
		try {
			notary.checkCertificate(cert);
			log.info("Id card validation OK");
		}
		catch (DigiDocException e) {
			log.error("Certificate is not valid", e);
			return false;
		}
		catch (Throwable e) {
			log.error("Certificate validation failed with unkown exception", e);
			throw new RuntimeException(e);
		}
		return true;
	}

	public void setValidateIdCertificate(boolean validateIdCertificate) {
		this.validateIdCertificate = validateIdCertificate;
	}

	public void setJdigidocConfigurationInitializer(JdigidocConfigurationInitializer jdigidocConfigurationInitializer) {
		this.jdigidocConfigurationInitializer = jdigidocConfigurationInitializer;
	}
}
