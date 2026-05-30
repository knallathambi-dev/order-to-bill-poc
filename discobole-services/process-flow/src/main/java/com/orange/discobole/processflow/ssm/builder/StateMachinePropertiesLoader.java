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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.orange.discobole.processflow.exception.InvalidConfigurationException;

import jakarta.annotation.PostConstruct;

import java.util.*;

@ConfigurationProperties("package-element")
@Configuration
public class StateMachinePropertiesLoader {

    static class StateMachine {

        static class State {
            private String name;
            private String description;
            private String entryActions;
            private List<String> exitActions;
            private List<String> stateActions;
            private String initialAction;
            private String pseudoStateKind;
            private String parentState;
            private String regionId;
            private String editable;
            private String hidden;

            @Override
            public int hashCode() {
                return name.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof State)
                    return name.equals(((State) obj).name);
                return false;
            }

            public List<String> getStateActions() {
                return stateActions;
            }

            public void setStateActions(List<String> stateActions) {
                this.stateActions = stateActions;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getEntryActions() {
                return entryActions;
            }

            public void setEntryActions(String entryActions) {
                this.entryActions = entryActions;
            }

            public List<String> getExitActions() {
                return exitActions;
            }

            public void setExitActions(List<String> exitActions) {
                this.exitActions = exitActions;
            }

            public String getInitialAction() {
                return initialAction;
            }

            public void setInitialAction(String initialAction) {
                this.initialAction = initialAction;
            }

            public String getPseudoStateKind() {
                return pseudoStateKind;
            }

            public void setPseudoStateKind(String pseudoStateKind) {
                this.pseudoStateKind = pseudoStateKind;
            }

            public String getParentState() {
                return parentState;
            }

            public void setParentState(String parentState) {
                this.parentState = parentState;
            }

            public String getRegionId() {
                return regionId;
            }

            public void setRegionId(String regionId) {
                this.regionId = regionId;
            }

            public String getEditable() {
                return editable;
            }

            public void setEditable(String editable) {
                this.editable = editable;
            }

            public String getHidden() {
                return hidden;
            }

            public void setHidden(String hidden) {
                this.hidden = hidden;
            }
        }

        static class Transition {
            private String source;
            private String target;
            private String event;
            private String[] actions;
            private String guard;
            private String kind;


            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Transition that = (Transition) o;
                return source.equals(that.source) &&
                        target.equals(that.target) &&
                        Objects.equals(event, that.event) &&
                        Arrays.equals(actions, that.actions) &&
                        Objects.equals(guard, that.guard) &&
                        Objects.equals(kind, that.kind);
            }

            @Override
            public int hashCode() {
                int result = Objects.hash(source, target, event, guard, kind);
                result = 31 * result + Arrays.hashCode(actions);
                return result;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getTarget() {
                return target;
            }

            public void setTarget(String target) {
                this.target = target;
            }

            public String getEvent() {
                return event;
            }

            public void setEvent(String event) {
                this.event = event;
            }

            public String[] getActions() {
                return actions;
            }

            public void setActions(String[] actions) {
                this.actions = actions;
            }

            public String getGuard() {
                return guard;
            }

            public void setGuard(String guard) {
                this.guard = guard;
            }

            public String getKind() {
                return kind;
            }

            public void setKind(String kind) {
                this.kind = kind;
            }
        }

        private String name;
        private List<State> states;
        private List<Transition> transitions;

        @PostConstruct
        private void init() {
            final Set<String> states = new HashSet<>();
            for (final State state : this.states)
                if (!states.add(state.name))
                    throw new InvalidConfigurationException("duplicate states cannot be defined: " + state.name);

            final Set<Transition> transitions = new HashSet<>();
            for (final Transition transition : this.transitions) {
                if (!transitions.add(transition))
                    throw new InvalidConfigurationException("duplicate events in transitions cannot be defined: " + transition.event);
                if (!states.contains(transition.source) || !states.contains(transition.target))
                    throw new InvalidConfigurationException("source/target used in transition must be defined in states: " + transition.source + "/" + transition.target);
            }
        }

        @Override
        public String toString() {
            return "StateMachineProperties{" +
                    "name='" + name + '\'' +
                    ", states=" + states +
                    ", transitions=" + transitions +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<State> getStates() {
            return states;
        }

        public void setStates(List<State> states) {
            this.states = states;
        }

        public List<Transition> getTransitions() {
            return transitions;
        }

        public void setTransitions(List<Transition> transitions) {
            this.transitions = transitions;
        }

    }

    private List<StateMachine> stateMachines;

    public List<StateMachine> getStateMachines() {
        return stateMachines;
    }

    public void setStateMachines(List<StateMachine> stateMachines) {
        this.stateMachines = stateMachines;
    }

}
