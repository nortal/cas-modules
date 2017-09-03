package com.nortal.cas.support.mobileid;

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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import com.nortal.cas.support.esteid.DelegateAuthenticationHandler;
import com.nortal.cas.support.esteid.X509CredentialsToPrincipalResolver;
import com.nortal.cas.support.extraui.ExtraUiRegistry;
import com.nortal.cas.support.mobileid.controller.MobileIdController;
import com.nortal.cas.support.mobileid.service.MobileIdService;
import com.nortal.cas.support.mobileid.service.impl.MobileIdServiceImpl;

@Configuration
@EnableConfigurationProperties(MobileIdProperties.class)
public class MobileidConfiguration {
	
	@Autowired
	private MobileIdProperties mobileIdProperties;
	
	@Autowired
	private MessageSource messageSource;
	
	@Bean
	MobileIdService mobileIdService() {
		MobileIdServiceImpl mobileIdServiceImpl = new MobileIdServiceImpl();
		mobileIdServiceImpl.setServiceName(mobileIdProperties.getServiceName());
		mobileIdServiceImpl.setServiceUrl(mobileIdProperties.getServiceUrl());
		return mobileIdServiceImpl;
	}
	
	@Bean
	MobileIdController mobileIdController() {
		MobileIdController controller = new MobileIdController();
		controller.setIsTestNumbersEnabled(mobileIdProperties.isTestNumbersEnabled());
		controller.setMessageSource(messageSource);
		controller.setMobileIdService(mobileIdService());
		controller.setmIdCheckCount(mobileIdProperties.getmIdCheckCount());
		controller.setmIdCheckDelay(mobileIdProperties.getmIdCheckDelay());
		return controller;
	}

	@Bean
	AuthenticationHandler mobileidAuthenticationHandler() {
		return new DelegateAuthenticationHandler(new X509CredentialsAuthenticationHandler(Pattern.compile(".*")),
				MobileidMessage.BAD_MOBILE_ID_AUTH.getMessageCode(), MobileIdCredential.class);
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
	public CasWebflowConfigurer mobileidWebflowConfigurer() {
		extraUiRegistry.add("mobileId");
		return new MobileidWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry);
	}

	@Bean
	public Action mobileidCredentialsAction() {
		return new MobileIdCredentialsAction(initialAuthenticationAttemptWebflowEventResolver,
				serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
	}

	@Bean
	@ConditionalOnMissingBean(name = "mobileidPrincipalResolver")
	public X509CredentialsToPrincipalResolver mobileidPrincipalResolver() {
		return new X509CredentialsToPrincipalResolver();
	}

	@Configuration("mobileidAuthenticationEventExecutionPlanConfiguration")
	public class MobileidAuthenticationEventExecutionPlanConfiguration
			implements AuthenticationEventExecutionPlanConfigurer {

		@Override
		public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
			plan.registerAuthenticationHandlerWithPrincipalResolver(mobileidAuthenticationHandler(), mobileidPrincipalResolver());
		}
	}
}
