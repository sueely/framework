package com.pci.controller.activiti.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.HistoricActivityInstanceQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.GetDeploymentProcessDefinitionCmd;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivitiHistoryGraphBuilder {
    private static Logger logger = LoggerFactory
            .getLogger(ActivitiHistoryGraphBuilder.class);
    private String processInstanceId;
    private ProcessDefinitionEntity processDefinitionEntity;
    private List<HistoricActivityInstance> historicActivityInstances;
    private List<HistoricActivityInstance> visitedHistoricActivityInstances = new ArrayList<HistoricActivityInstance>();
    private Map<String, Node> nodeMap = new HashMap<String, Node>();

    public ActivitiHistoryGraphBuilder(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Graph build() {
        this.fetchProcessDefinitionEntity();
        this.fetchHistoricActivityInstances();

        Graph graph = new Graph();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            Node currentNode = new Node();
            currentNode.setId(historicActivityInstance.getId());
            currentNode.setName(historicActivityInstance.getActivityId());
            currentNode.setType(historicActivityInstance.getActivityType());
            currentNode
                    .setActive(historicActivityInstance.getEndTime() == null);
            logger.debug("currentNode : {}:{}", currentNode.getName(),
                    currentNode.getId());

            Edge previousEdge = this.findPreviousEdge(currentNode,
                    historicActivityInstance.getStartTime().getTime());

            if (previousEdge == null) {
                if (graph.getInitial() != null) {
                    throw new IllegalStateException("already set an initial.");
                }

                graph.setInitial(currentNode);
            } else {
                logger.debug("previousEdge : {}", previousEdge.getName());
            }

            nodeMap.put(currentNode.getId(), currentNode);
            visitedHistoricActivityInstances.add(historicActivityInstance);
        }

        if (graph.getInitial() == null) {
            throw new IllegalStateException("cannot find initial.");
        }

        return graph;
    }

    public void fetchProcessDefinitionEntity() {
        String processDefinitionId = Context.getCommandContext()
                .getHistoricProcessInstanceEntityManager()
                .findHistoricProcessInstance(processInstanceId)
                .getProcessDefinitionId();
        GetDeploymentProcessDefinitionCmd cmd = new GetDeploymentProcessDefinitionCmd(
                processDefinitionId);
        processDefinitionEntity = cmd.execute(Context.getCommandContext());
    }

    public void fetchHistoricActivityInstances() {
        HistoricActivityInstanceQueryImpl historicActivityInstanceQueryImpl = new HistoricActivityInstanceQueryImpl();
        // historicActivityInstanceQueryImpl.processInstanceId(processInstanceId)
        // .orderByHistoricActivityInstanceStartTime().asc();
        // TODO: 如果用了uuid会�?成这样排序出问题
        // 但是如果用startTime，可能出现因为处理�?度太快，时间�?��，导致次序颠倒的问题
        historicActivityInstanceQueryImpl.processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceId().asc();

        Page page = new Page(0, 100);
        historicActivityInstances = Context
                .getCommandContext()
                .getHistoricActivityInstanceEntityManager()
                .findHistoricActivityInstancesByQueryCriteria(
                        historicActivityInstanceQueryImpl, page);
    }

    public Edge findPreviousEdge(Node currentNode, long currentStartTime) {
        String activityId = currentNode.getName();
        ActivityImpl activityImpl = processDefinitionEntity
                .findActivity(activityId);
        HistoricActivityInstance nestestHistoricActivityInstance = null;
        String temporaryPvmTransitionId = null;

        // 遍历进入当前节点的所有连�?
        for (PvmTransition pvmTransition : activityImpl
                .getIncomingTransitions()) {
            PvmActivity source = pvmTransition.getSource();

            String previousActivityId = source.getId();

            HistoricActivityInstance visitiedHistoryActivityInstance = this
                    .findVisitedHistoricActivityInstance(previousActivityId);

            if (visitiedHistoryActivityInstance == null) {
                continue;
            }

            // 如果上一个节点还未完成，说明不可能是从这个节点过来的，跳�?
            if (visitiedHistoryActivityInstance.getEndTime() == null) {
                continue;
            }

            logger.debug("current activity start time : {}", new Date(
                    currentStartTime));
            logger.debug("nestest activity end time : {}",
                    visitiedHistoryActivityInstance.getEndTime());

            // 如果当前节点的开始时间，比上�?��节点的结束时间要早，跳过
            if (currentStartTime < visitiedHistoryActivityInstance.getEndTime()
                    .getTime()) {
                continue;
            }

            if (nestestHistoricActivityInstance == null) {
                nestestHistoricActivityInstance = visitiedHistoryActivityInstance;
                temporaryPvmTransitionId = pvmTransition.getId();
            } else if ((currentStartTime - nestestHistoricActivityInstance
                    .getEndTime().getTime()) > (currentStartTime - visitiedHistoryActivityInstance
                    .getEndTime().getTime())) {
                // 寻找离当前节点最近的上一个节�?
                // 比较上一个节点的endTime与当前节点startTime的差
                nestestHistoricActivityInstance = visitiedHistoryActivityInstance;
                temporaryPvmTransitionId = pvmTransition.getId();
            }
        }

        // 没找到上�?��节点，就返回null
        if (nestestHistoricActivityInstance == null) {
            return null;
        }

        Node previousNode = nodeMap
                .get(nestestHistoricActivityInstance.getId());

        if (previousNode == null) {
            return null;
        }

        logger.debug("previousNode : {}:{}", previousNode.getName(),
                previousNode.getId());

        Edge edge = new Edge();
        edge.setName(temporaryPvmTransitionId);
        previousNode.getEdges().add(edge);
        edge.setSrc(previousNode);
        edge.setDest(currentNode);

        return edge;
    }

    public HistoricActivityInstance findVisitedHistoricActivityInstance(
            String activityId) {
        for (int i = visitedHistoricActivityInstances.size() - 1; i >= 0; i--) {
            HistoricActivityInstance historicActivityInstance = visitedHistoricActivityInstances
                    .get(i);

            if (activityId.equals(historicActivityInstance.getActivityId())) {
                return historicActivityInstance;
            }
        }

        return null;
    }
}
