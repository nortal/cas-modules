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

import java.util.regex.Pattern;

import org.apereo.cas.adaptors.x509.authentication.handler.support.X509CredentialsAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import com.nortal.cas.support.esteid.DelegateAuthenticationHandler;
import com.nortal.cas.support.esteid.X509CredentialsToPrincipalResolver;
import com.nortal.cas.support.extraui.ExtraUiRegistry;

@Configuration
public class IdCardConfiguration {

	@Bean
	IdCardCertificateController idCardCertificateController() {
		return new IdCardCertificateController();
	}

	@Bean
	JdigidocConfigurationInitializer jdigidocConfigurationInitializer() {
		return new JdigidocConfigurationInitializer();
	}

	@Bean
	@ConditionalOnMissingBean
	IdCardRevocationChecker idCardRevocationChecker() {
		IdCardRevocationChecker checker = new IdCardRevocationChecker();
		checker.setJdigidocConfigurationInitializer(jdigidocConfigurationInitializer());
		checker.setValidateIdCertificate(true);
		return checker;
	}

	@Bean
	AuthenticationHandler idCardAuthenticationHandler() {
		return new DelegateAuthenticationHandler(
				new X509CredentialsAuthenticationHandler(Pattern.compile(".*"), idCardRevocationChecker()),
				"error.authentication.credentials.bad.id_card", IdCardCredential.class);
	}

	@Autowired
	@Qualifier("adaptiveAuthenticationPolicy")
	private AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy;

	@Autowired
	@Qualifier("serviceTicketRequestWebflowEventResolver")
	private CasWebflowEventResolver serviceTicketRequestWebflowEventResolver;

	@Autowired
	@Qualifier("initialAuthenticationAttemptWebflowEventResolver")
	private CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver;

	@Autowired(required = false)
	@Qualifier("loginFlowRegistry")
	private FlowDefinitionRegistry loginFlowDefinitionRegistry;

	@Autowired(required = false)
	private FlowBuilderServices flowBuilderServices;

	@Autowired
	private ExtraUiRegistry extraUiRegistry;
	
	@Bean
	public CasWebflowConfigurer x509WebflowConfigurer() {
		extraUiRegistry.add("idcard");
		return new IdCardWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry);
	}

	@Bean
	public Action idCardCredentialsAction() {
		return new IdCardCredentialsAction(initialAuthenticationAttemptWebflowEventResolver,
				serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
	}

	@Bean
	@ConditionalOnMissingBean(name = "idCardPrincipalResolver")
	public X509CredentialsToPrincipalResolver idCardPrincipalResolver() {
		return new X509CredentialsToPrincipalResolver();
	}

	@Configuration("idCardAuthenticationEventExecutionPlanConfiguration")
	public class IdCardAuthenticationEventExecutionPlanConfiguration
			implements AuthenticationEventExecutionPlanConfigurer {

		@Override
		public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
			plan.registerAuthenticationHandlerWithPrincipalResolver(idCardAuthenticationHandler(), idCardPrincipalResolver());
		}
	}
}
