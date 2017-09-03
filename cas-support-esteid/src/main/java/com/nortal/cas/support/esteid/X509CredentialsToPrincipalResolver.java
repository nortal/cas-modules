package com.nortal.cas.support.esteid;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.apache.commons.lang.StringUtils.trimToNull;

import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.apereo.cas.adaptors.x509.authentication.principal.AbstractX509PrincipalResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Similar implementation like
 * {@link org.jasig.cas.adaptors.x509.authentication.principal.X509CertificateCredentialsToIdentifierPrincipalResolver}. Because esteid
 * SubjectDN does not conform to the standard
 * {@link org.jasig.cas.adaptors.x509.authentication.principal.X509CertificateCredentialsToIdentifierPrincipalResolver} expects it had to be
 * customized
 * 
 * @author Priit Liivak
 * @author Allar Saarnak
 */
public class X509CredentialsToPrincipalResolver extends AbstractX509PrincipalResolver {

   private static final Logger log = LoggerFactory.getLogger(X509CredentialsToPrincipalResolver.class);

   /**
    * Entry name in certificate that holds username info
    */
   public static final String ID_CODE = "SERIALNUMBER";

   private static final String FIRST_NAME = "GIVENNAME";

   private static final String LAST_NAME = "SURNAME";

   public static final String COUNTRY_CODE = "C";

   @Override
   protected String resolvePrincipalInternal(final X509Certificate certificate) {
      String certificateSubject = certificate.getSubjectDN().getName();
      if (log.isInfoEnabled()) {
         log.info("Creating principal for: " + certificateSubject);
      }
      String idCode = getDistinguishedNameField(certificateSubject, ID_CODE);
      String firstName = getDistinguishedNameField(certificateSubject, FIRST_NAME);
      String lastName = getDistinguishedNameField(certificateSubject, LAST_NAME);
      String countryCode = getDistinguishedNameField(certificateSubject, COUNTRY_CODE);

      return resolvePrincipal(idCode, countryCode, firstName, lastName);
   }

   /**
    * Override this method as needed.
    * @return Principal id for the person attributes found in the certificate.
    */
   protected String resolvePrincipal(String idCode, String countryCode, String firstName, String lastName) {
  	 return countryCode + idCode;
   }

   public static String getDistinguishedNameField(String certificateSubject, String fieldName) {
      String fieldValue = substringAfter(certificateSubject, fieldName + "=");
      // If serial number contains ',' then it is not the last value
      if (StringUtils.contains(fieldValue, ',')) {
         fieldValue = substringBefore(fieldValue, ",");
      }
      if (log.isDebugEnabled()) {
         log.debug("Parsed " + fieldName + " =" + fieldValue);
      }
      return trimToNull(fieldValue);
   }

}
