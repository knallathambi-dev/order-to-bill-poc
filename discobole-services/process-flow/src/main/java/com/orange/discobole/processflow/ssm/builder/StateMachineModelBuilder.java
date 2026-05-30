// Software Name: process-flow
// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt
// Software description: The TMF701 ProcessFlow API allows management of business process definition and execution. \\r\\n\\r\\nThis API is used as a Java library in the Order Capture Management and Orchestration Delivery Fallout Management APIs.\\r\\n\\r\\n\\r\\n

package com.orange.discobole.processflow.ssm.builder;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.model.*;
import org.springframework.statemachine.config.model.verifier.DefaultStateMachineModelVerifier;
import org.springframework.statemachine.config.model.verifier.StateMachineModelVerifier;
import org.springframework.statemachine.ensemble.StateMachineEnsemble;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.monitor.StateMachineMonitor;
import org.springframework.statemachine.security.SecurityRule;
import org.springframework.statemachine.state.PseudoStateKind;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.statemachine.transition.TransitionKind;

import com.orange.discobole.processflow.exception.InvalidParameterException;
import com.orange.discobole.processflow.ssm.action.StateMachineStateAction;
import com.orange.discobole.processflow.ssm.guard.StateMachineGuard;

import jakarta.annotation.Resource;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Configuration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StateMachineModelBuilder {

    @Resource
    private StateMachinePropertiesLoader properties;

    @Resource
    private ApplicationContext applicationContext;

    private final Collection<StateData<String, String>> stateDatas = new ArrayList<>();
    private final Collection<TransitionData<String, String>> transitionDatas = new ArrayList<>();
    private final Map<String, LinkedList<ChoiceData<String, String>>> choices = new HashMap<>();
    private final Map<String, LinkedList<JunctionData<String, String>>> junctions = new HashMap<>();
    private final Map<String, List<String>> forks = new HashMap<>();
    private final Map<String, List<String>> joins = new HashMap<>();
    private String processDefinitionKey = "";

    public StateMachineModel<String, String> build(String processDefinitionKey) {
        checkProcessDefinitionKey(processDefinitionKey);
        setProcessDefinitionKey(processDefinitionKey);
            StateMachinePropertiesLoader.StateMachine ssm = properties.getStateMachines().stream()
                .filter(stateMachine -> stateMachine.getName().equals(processDefinitionKey))
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("there is no process flow with name : " + processDefinitionKey));

        buildState(ssm);
        buildTransition(ssm);
        HashMap<String, List<ChoiceData<String, String>>> choicesCopy = new HashMap<>();
        choicesCopy.putAll(choices);
        HashMap<String, List<JunctionData<String, String>>> junctionsCopy = new HashMap<>();
        junctionsCopy.putAll(junctions);

		final ConfigurationData<String, String> configurationData = new ConfigurationData<>(null, true,
				null, new ArrayList<StateMachineListener<String, String>>(), false, null, null, null, null, true,
				new DefaultStateMachineModelVerifier<String, String>(), processDefinitionKey, null, null);
//        final ConfigurationData<String, String> configurationData = new ConfigurationData<>(null, new SyncTaskExecutor(), new ConcurrentTaskScheduler(), true,
//                null, new ArrayList<StateMachineListener<String, String>>(), false,
//                null, null, null, null, true, new DefaultStateMachineModelVerifier<String, String>(), processDefinitionKey, null, null);

		
        StateMachineModel<String, String> stateMachineModel = new DefaultStateMachineModel<>(configurationData, new StatesData<>(stateDatas),
                new TransitionsData<>(transitionDatas, choicesCopy, junctionsCopy, forks, joins, null, null, null));

        return stateMachineModel;

    }

    private static void checkProcessDefinitionKey(String processDefinitionKey) {
        if (Objects.isNull(processDefinitionKey) || processDefinitionKey.isEmpty())
            throw new InvalidParameterException("missing required field processFlowSpecification");
    }

    private void buildState(StateMachinePropertiesLoader.StateMachine ssm) {
        for (StateMachinePropertiesLoader.StateMachine.State state : ssm.getStates()) {
            String parent = state.getParentState() != null && !state.getParentState().isBlank() ? state.getParentState() : null;
            String regionId = state.getRegionId() != null && !state.getRegionId().isBlank() ? state.getRegionId() : null;
            StateData<String, String> stateData = new StateData<>(parent, regionId, state.getName(), false);

            stateData.setInitialAction(resolveStateAction(state.getInitialAction()));
            setExitActionInStateData(state, stateData);

            if (state.getPseudoStateKind() == null || state.getPseudoStateKind().isEmpty()) {
                stateDatas.add(stateData);
            } else {
                PseudoStateKind pseudoStateKind = PseudoStateKind.valueOf(state.getPseudoStateKind());
                switch (pseudoStateKind) {
                    case INITIAL:
                        stateData.setInitial(true);
                        stateData.setInitialAction(resolveStateAction(state.getInitialAction()));
                        stateDatas.add(stateData);
                        break;
                    case CHOICE:
                        stateData.setPseudoStateKind(PseudoStateKind.CHOICE);
                        stateDatas.add(stateData);
                        break;
                    case FORK:
                        stateData.setPseudoStateKind(PseudoStateKind.FORK);
                        stateDatas.add(stateData);
                        break;
                    case JOIN:
                        stateData.setPseudoStateKind(PseudoStateKind.JOIN);
                        stateDatas.add(stateData);
                        break;
                    case JUNCTION:
                        stateData.setPseudoStateKind(PseudoStateKind.JUNCTION);
                        stateDatas.add(stateData);
                        break;
                    case END:
                        stateData.setEnd(true);
                        stateDatas.add(stateData);
                        break;
                }
            }
        }
    }

    private void setExitActionInStateData(StateMachinePropertiesLoader.StateMachine.State state, StateData<String, String> stateData) {
        Collection<Function<StateContext<String, String>, Mono<Void>>> exitActions = new ArrayList<>();

        for(String exitAction: state.getExitActions()) {
            if (applicationContext.containsBean(exitAction)) {
                exitActions.add(context ->
                        (Mono<Void>) (applicationContext.getBean(exitAction, StateMachineStateAction.class).apply(context))
                );
            }
        }

        stateData.setExitActions(exitActions);
    }

    private void buildTransition(StateMachinePropertiesLoader.StateMachine ssm) {
        for (StateMachinePropertiesLoader.StateMachine.Transition transition : ssm.getTransitions()) {

            String transitionSourceKind = findTransitionSourceKind(transition.getSource());
            PseudoStateKind pseudoStateKind = transitionSourceKind != null && !transitionSourceKind.isEmpty() ? PseudoStateKind.valueOf(transitionSourceKind) : null;
            if (pseudoStateKind == PseudoStateKind.CHOICE) {

                LinkedList<ChoiceData<String, String>> list = choices.get(transition.getSource());
                if (list == null) {
                    list = new LinkedList<>();
                    choices.put(transition.getSource(), list);
                }
                Function<StateContext<String,String>, Mono<Boolean>> isGuardPresent = resolveGuard(transition.getGuard());
                Guard<String, String> guard = null;
                if(isGuardPresent != null){
                    guard = context->isGuardPresent.apply(context).block();
                }
//                Collection<Action<String, String>> actions = resolveTransitionActions(transition.getActions());
                Collection<Action<String, String>> actions = resolveTransitionActions(transition.getActions())
                	    .stream()
                	    .map(function -> (Action<String, String>) (stateContext -> {
                	        Mono<Void> monoResult = function.apply(stateContext);
                	        monoResult.then();
//                	        return (context) -> monoResult.then();;
                	    }))
                	    .collect(Collectors.toList());


                // we want null guards to be at the end
                if (guard == null) {
                    list.addLast(new ChoiceData<>(transition.getSource(), transition.getTarget(), guard, actions));
                } else {
                    list.addFirst(new ChoiceData<>(transition.getSource(), transition.getTarget(), guard, actions));
                }
            } else if (pseudoStateKind == PseudoStateKind.JUNCTION) {

                LinkedList<JunctionData<String, String>> list = junctions.get(transition.getSource());
                if (list == null) {
                    list = new LinkedList<JunctionData<String, String>>();
                    junctions.put(transition.getSource(), list);
                }
                Guard<String, String> guard = context->resolveGuard(transition.getGuard()).apply(context).block();
//                Collection<Action<String, String>> actions = resolveTransitionActions(transition.getActions());
                Collection<Action<String, String>> actions = resolveTransitionActions(transition.getActions())
                	    .stream()
                	    .map(function -> (Action<String, String>) (stateContext -> {
                	        Mono<Void> monoResult = function.apply(stateContext);
                	        monoResult.then();
//                	        return (context) -> monoResult.then();;
                	    }))
                	    .collect(Collectors.toList());
                // we want null guards to be at the end
                if (guard == null) {
                    list.addLast(new JunctionData<>(transition.getSource(), transition.getTarget(), guard, actions));
                } else {
                    list.addFirst(new JunctionData<>(transition.getSource(), transition.getTarget(), guard, actions));
                }
            } else if (pseudoStateKind == PseudoStateKind.FORK) {

                List<String> list = forks.get(transition.getSource());
                if (list == null) {
                    list = new ArrayList<>();
                    forks.put(transition.getSource(), list);
                }
                list.add(transition.getTarget());
            } else if (findTransitionSourceKind(transition.getTarget()) != null && !findTransitionSourceKind(transition.getTarget()).isEmpty() && PseudoStateKind.valueOf(findTransitionSourceKind(transition.getTarget())) == PseudoStateKind.JOIN) {

                List<String> list = joins.get(transition.getTarget());
                if (list == null) {
                    list = new ArrayList<>();
                    joins.put(transition.getTarget(), list);
                }
                list.add(transition.getSource());
            } else {
//                transitionDatas.add(new TransitionData<String,String>(transition.getSource(),
//                        transition.getTarget(), transition.getEvent(), resolveTransitionActions(transition.getActions()),
//                        resolveGuard(transition.getGuard()), TransitionKind.valueOf(transition.getKind())));

				transitionDatas.add(new TransitionData<>(transition.getSource(), transition.getTarget(),
						transition.getEvent(), resolveTransitionActions(transition.getActions()),
						resolveGuard(transition.getGuard()), TransitionKind.valueOf(transition.getKind())));
            }
        }
    }

//    private Collection<Action<String, String>> resolveTransitionActions(String[] actionNames) {
//        List<Action<String, String>> actions = new ArrayList<>();
//        if (actionNames != null) {
//            Arrays.stream(actionNames).forEach(actionName -> actions.add(context -> {
//                if (applicationContext.containsBean(actionName))
//                    applicationContext.getBean(actionName, StateMachineStateAction.class).execute(context);
//            }));
//
//        }
//        return actions;
//    }

	private Collection<Function<StateContext<String, String>, Mono<Void>>> resolveTransitionActions(
			String[] actionNames) {
		Collection<Function<StateContext<String, String>, Mono<Void>>> actions = new ArrayList<>();
		if (actionNames != null) {
			Arrays.stream(actionNames).forEach(actionName -> actions.add(context -> {
				if (applicationContext.containsBean(actionName))
					return (Mono<Void>)(applicationContext.getBean(actionName, StateMachineStateAction.class).apply(context));
				else
					return Mono.empty();
			}));
		}
		return actions;
	}

    private Action<String, String> resolveStateAction(String actionName) {
        if (actionName != null) {
            return context -> {
                if (applicationContext.containsBean(actionName))
                    applicationContext.getBean(actionName, StateMachineStateAction.class).apply(context);
            };
        } else
            return null;
    }

//    private Guard<String, String> resolveGuard(String guardName) {
//
//        if (guardName != null && !guardName.isBlank()) {
//            return context -> {
//                if (applicationContext.containsBean(guardName)) {
//                    return applicationContext.getBean(guardName, StateMachineGuard.class).evaluate(context);
//                } else
//                    return false;
//            };
//        } else
//            return null;
//    }



  private Function<StateContext<String,String>, Mono<Boolean>> resolveGuard(String guardName) {
      if (guardName != null && !guardName.isBlank()) {
          return context -> {
              if (applicationContext.containsBean(guardName)) {
                  return (Mono<Boolean>) applicationContext.getBean(guardName, StateMachineGuard.class).apply(context);
              } else
                  return Mono.just(false);
          };
      } else
          return null;
  }



    private String findTransitionSourceKind(String source) {
        String processDefinitionKey = getProcessDefinitionKey();
        StateMachinePropertiesLoader.StateMachine ssm = properties.getStateMachines().stream().filter(stateMachine -> stateMachine.getName().equals(processDefinitionKey)).findFirst().get();
        for (StateMachinePropertiesLoader.StateMachine.State state : ssm.getStates()) {
            if (source != null && source.equals(state.getName())) {
                return state.getPseudoStateKind();
            }
        }
        return null;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public Map<String, Boolean> getEditableStates(String processDefinitionKey) {
        Map<String, Boolean> editableStates = new HashMap<>();
        StateMachinePropertiesLoader.StateMachine ssm = properties.getStateMachines().stream().filter(stateMachine ->
                stateMachine.getName().equals(processDefinitionKey)).findFirst().get();
        for (StateMachinePropertiesLoader.StateMachine.State state : ssm.getStates()) {
            if (state.getEditable() != null) {
                editableStates.put(state.getName(), Boolean.valueOf(state.getEditable()));
            }
        }
        return editableStates;
    }

    public Map<String, Boolean> getHiddenStates(String processDefinitionKey) {
        Map<String, Boolean> hiddenStates = new HashMap<>();
        StateMachinePropertiesLoader.StateMachine ssm = properties.getStateMachines().stream().filter(stateMachine ->
                stateMachine.getName().equals(processDefinitionKey)).findFirst().get();
        for (StateMachinePropertiesLoader.StateMachine.State state : ssm.getStates()) {
            if (state.getHidden() != null) {
                hiddenStates.put(state.getName(), Boolean.valueOf(state.getHidden()));
            }
        }
        return hiddenStates;
    }
}
