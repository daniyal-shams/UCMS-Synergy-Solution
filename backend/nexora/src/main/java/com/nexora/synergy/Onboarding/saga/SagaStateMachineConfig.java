package com.nexora.synergy.Onboarding.saga;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.*;

@Configuration
@EnableStateMachine
public class SagaStateMachineConfig
        extends StateMachineConfigurerAdapter<OnboardingState, OnboardingEvent> {

    @Override
    public void configure(
            StateMachineStateConfigurer<OnboardingState, OnboardingEvent> states
    ) throws Exception {

        states
                .withStates()
                .initial(OnboardingState.REGISTERED)
                .state(OnboardingState.SUBSCRIPTION_ACTIVE)
                .state(OnboardingState.TENANT_PROVISIONED)
                .state(OnboardingState.PRIMARY_CAMPUS_CREATED)
                .state(OnboardingState.ADMIN_CREATED)
                .state(OnboardingState.ACTIVATED)
                .end(OnboardingState.FAILED);
    }

    @Override
    public void configure(
            StateMachineTransitionConfigurer<OnboardingState, OnboardingEvent> transitions
    ) throws Exception {

        transitions
                .withExternal()
                .source(OnboardingState.REGISTERED)
                .target(OnboardingState.SUBSCRIPTION_ACTIVE)
                .event(OnboardingEvent.SUBSCRIPTION_ACTIVATED)
                .and()
                .withExternal()
                .source(OnboardingState.SUBSCRIPTION_ACTIVE)
                .target(OnboardingState.TENANT_PROVISIONED)
                .event(OnboardingEvent.TENANT_PROVISIONED)
                .and()
                .withExternal()
                .source(OnboardingState.TENANT_PROVISIONED)
                .target(OnboardingState.PRIMARY_CAMPUS_CREATED)
                .event(OnboardingEvent.PRIMARY_CAMPUS_CREATED)
                .and()
                .withExternal()
                .source(OnboardingState.PRIMARY_CAMPUS_CREATED)
                .target(OnboardingState.ADMIN_CREATED)
                .event(OnboardingEvent.ADMIN_CREATED)
                .and()
                .withExternal()
                .source(OnboardingState.ADMIN_CREATED)
                .target(OnboardingState.ACTIVATED)
                .event(OnboardingEvent.ADMIN_CREATED)
                .and()
                .withExternal()
                .source(OnboardingState.REGISTERED)
                .target(OnboardingState.FAILED)
                .event(OnboardingEvent.FAILURE);
    }
}
