package com.nortal.cas.support.mobileid;

import org.apereo.cas.web.flow.AbstractCasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class MobileidWebflowConfigurer extends AbstractCasWebflowConfigurer {

	private static final String EVENT_ID_START_AUTHENTICATE_MOBILE_ID = "startAuthenticateMobileId";

	public MobileidWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
			final FlowDefinitionRegistry loginFlowDefinitionRegistry) {
		super(flowBuilderServices, loginFlowDefinitionRegistry);
	}

	@Override
	protected void doInitialize() throws Exception {
		final Flow flow = getLoginFlow();
		if (flow != null) {

			final ActionState actionState = createActionState(flow, EVENT_ID_START_AUTHENTICATE_MOBILE_ID,
					createEvaluateAction("mobileidCredentialsAction"));
			actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS,
					CasWebflowConstants.TRANSITION_ID_SEND_TICKET_GRANTING_TICKET));
			actionState.getTransitionSet()
					.add(createTransition(CasWebflowConstants.TRANSITION_ID_WARN, CasWebflowConstants.TRANSITION_ID_WARN));
			actionState.getTransitionSet()
					.add(createTransition(CasWebflowConstants.TRANSITION_ID_ERROR, CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM));
			actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE,
					CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM));
			
      actionState.getExitActionList().add(createEvaluateAction("clearWebflowCredentialsAction"));
      registerMultifactorProvidersStateTransitionsIntoWebflow(actionState);

			ViewState viewLoginState = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
			createTransitionForState(viewLoginState, "submitMobileId", EVENT_ID_START_AUTHENTICATE_MOBILE_ID, true);
		}
	}
}
