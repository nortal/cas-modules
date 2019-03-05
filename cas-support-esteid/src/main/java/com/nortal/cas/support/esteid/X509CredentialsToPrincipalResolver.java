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
   
   /**
    * Serialnumber prefix in new id cards (since 12/2018)
    */
   private static final String PNOEE_PREFIX = "PNOEE-";

   private static final String FIRST_NAME = "GIVENNAME";

   private static final String LAST_NAME = "SURNAME";

   public static final String COUNTRY_CODE = "C";

   @Override
   protected String resolvePrincipalInternal(final X509Certificate certificate) {
      String certificateSubject = certificate.getSubjectDN().getName();
      if (log.isInfoEnabled()) {
         log.info("Creating principal for: " + certificateSubject);
      }
      String idCode = trimSnPrefixes(getDistinguishedNameField(certificateSubject, ID_CODE));
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

	private static String trimSnPrefixes(String sn) {
		if (sn != null && sn.startsWith(PNOEE_PREFIX)) {
			return sn.substring(PNOEE_PREFIX.length());
		} else {
			return sn;
		}
	}
}
