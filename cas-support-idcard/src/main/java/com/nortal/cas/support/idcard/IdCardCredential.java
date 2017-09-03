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
package com.nortal.cas.support.idcard;

import java.security.cert.X509Certificate;

import org.apereo.cas.adaptors.x509.authentication.principal.X509CertificateCredential;

public class IdCardCredential extends X509CertificateCredential {

	private static final long serialVersionUID = 1L;

	public IdCardCredential(X509Certificate[] certificates) {
		super(certificates);
	}

}
