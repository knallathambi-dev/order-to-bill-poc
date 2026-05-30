// SPDX-FileCopyrightText: 2025 Orange SA
// SPDX-License-Identifier: MIT
//
// This software is distributed under the MIT License,
// the text of which is available at https://opensource.org/license/mit
// or see the "LICENSE.txt" file for more details.
//
// Authors: See CONTRIBUTORS.txt

package com.orange.discobole.productcatalog.productoffering.bdd.models;

import java.io.Serializable;

public class ProcessFlowInfo implements Serializable{
        private String processFlowId;
        private String taskFlowId;
        public ProcessFlowInfo(String processFlowId, String taskFlowId) {
            super();
            this.processFlowId = processFlowId;
            this.taskFlowId = taskFlowId;
        }
        public String getProcessFlowId() {
            return processFlowId;
        }
        public void setProcessFlowId(String processFlowId) {
            this.processFlowId = processFlowId;
        }
        public String getTaskFlowId() {
            return taskFlowId;
        }
        public void setTaskFlowId(String taskFlowId) {
            this.taskFlowId = taskFlowId;
        }
        @Override
        public String toString() {
            return "ProcessFlowInformation [processFlowId=" + processFlowId + ", taskFlowId=" + taskFlowId + "]";
        }

}
