package com.nortal.cas.support.idcard;

import static com.nortal.cas.support.idcard.IdCardCredentialsAction.CERTIFICATE_REQUEST_ATTRIBUTE;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Used to read id-card x509 certificates sent by apache frontend and set them to session.
 * This is useful with AJAX requests because Spring WebFlow can only exist in one url and we don't want to break the flow!
 * @author Allar Saarnak
 */
@Controller
public class IdCardCertificateController {

	private static final Logger log = getLogger(IdCardCertificateController.class);

	/**
	 * Adds persons id-card certificate to session.
	 */
	@RequestMapping(method = GET, value = "/idlogin")
	public ResponseEntity<Void> addPerson(HttpServletRequest request) {
		HttpStatus result = OK;
		final X509Certificate[] certificates = (X509Certificate[]) request.getAttribute(CERTIFICATE_REQUEST_ATTRIBUTE);
		if(certificates == null || certificates.length < 1){
			log.debug("No certificates found from ajax request!");
			//Let the AuthenticationHandler throw the exception if no certificates.
		}
		else{
			log.debug("Certificates found in ajax request!");
			log.debug(certificates[0].toString());
		}
		request.getSession().setAttribute(CERTIFICATE_REQUEST_ATTRIBUTE, certificates);

		return new ResponseEntity<Void>(result);
	}
}