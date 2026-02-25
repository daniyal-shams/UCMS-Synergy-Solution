package com.nexora.synergy.Onboarding.saga;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
public class OnboardingOrchestrator {

    private final StateMachine<OnboardingState, OnboardingEvent> stateMachine;

    public OnboardingOrchestrator(
            StateMachine<OnboardingState, OnboardingEvent> stateMachine
    ) {
        this.stateMachine = stateMachine;
    }

    public void subscriptionActivated() {
        stateMachine.sendEvent(OnboardingEvent.SUBSCRIPTION_ACTIVATED);
    }

    public void tenantProvisioned() {
        stateMachine.sendEvent(OnboardingEvent.TENANT_PROVISIONED);
    }

    public void primaryCampusCreated() {
        stateMachine.sendEvent(OnboardingEvent.PRIMARY_CAMPUS_CREATED);
    }

    public void adminCreated() {
        stateMachine.sendEvent(OnboardingEvent.ADMIN_CREATED);
    }

    public void fail() {
        stateMachine.sendEvent(OnboardingEvent.FAILURE);
    }
}
