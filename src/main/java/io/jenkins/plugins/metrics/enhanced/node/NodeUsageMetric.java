package io.jenkins.plugins.metrics.enhanced.node;

import hudson.Extension;
import hudson.model.*;
import hudson.model.Queue;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.label.AbstractGenericMetric;
import io.jenkins.plugins.metrics.enhanced.generic.label.GenericMetric;
import io.jenkins.plugins.metrics.enhanced.utils.Utils;
import jenkins.model.Jenkins;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeUsageMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(NodeUsageMetric.class.getName());
    private static HashMap<String, Double> nodeRunCounters = new HashMap<>();

    public NodeUsageMetric() {
        super(EnhancedMetrics.generateMetricName("node_run_count"), "Usage Count of Nodes", Collections.singletonList("name"));
        NodeUsageMetric.initNodeRunCounters();
    }

    private static void initNodeRunCounters() {
        try {
            Jenkins jenkins = Utils.getJenkins();
            Computer[] computers = jenkins.getComputers();
            Arrays.stream(computers).forEach(computer -> {
                String nodeName = "undefined";
                if (computer.getNode() instanceof Hudson) {
                    nodeName = EnhancedMetrics.defaultNodeName;
                } else {
                    if (computer.getNode() != null) {
                        nodeName = computer.getNode().getNodeName();
                    }
                }
                if (!nodeRunCounters.containsKey(nodeName))
                    nodeRunCounters.put(nodeName, (double) 0);
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public List<GenericMetric> getGenericMetrics() {
        List<GenericMetric> genericMetrics = new ArrayList<>();
        NodeUsageMetric.nodeRunCounters.entrySet().forEach(entry -> {
            genericMetrics.add(
                    new GenericMetric(entry.getValue(), entry.getKey())
            );
        });
        return genericMetrics;
    }

    @Extension
    public static class NodeUsageExecutionListener implements ExecutorListener {

        @Override
        public synchronized void taskAccepted(Executor executor, Queue.Task task) {
            NodeUsageMetric.initNodeRunCounters();
            String nodeName = Utils.getNodeName(executor);
            Double counter = Utils.increaseCounter(nodeRunCounters, nodeName);
            logger.fine(String.format("NodeUsageMetric is added for nodeName:%s - task:%s - counter:%s", nodeName, task.getFullDisplayName(), counter));
        }
    }
}
