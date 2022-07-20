package io.jenkins.plugins.metrics.enhanced.node;

import hudson.Extension;
import hudson.model.*;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.label.AbstractGenericMetric;
import io.jenkins.plugins.metrics.enhanced.generic.label.GenericMetric;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class NodeUsageMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(NodeUsageMetric.class.getName());
    private static HashMap<String, Double> nodeRunCounters = new HashMap<>();

    public NodeUsageMetric() {
        super(EnhancedMetrics.generateMetricName("node_run_count"), "Usage Count of Nodes", Collections.singletonList("name"));
        this.initNodeRunCounters();
    }

    private void initNodeRunCounters(){
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if( jenkins == null){
            logger.warning("Jenkins instance is null, skipping nodeRunCounters initialization");
            return;
        }
        List<Node> nodes = jenkins.getNodes();
        nodes.forEach(node -> {
            String nodeName = node.getNodeName();
            if(!nodeRunCounters.containsKey(nodeName))
                nodeRunCounters.put(nodeName, (double)0);
        });

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
        public void taskAccepted(Executor executor, Queue.Task task) {
            String nodeName = "undefined";
            Computer computer = executor.getOwner();
            Node node = computer.getNode();
            if (node != null) {
                if (node instanceof Hudson) {
                    // Means it is running on the builtinNode
                    logger.fine("Node is instance of Hudson. Setting node name as 'builtin'");
                    nodeName = EnhancedMetrics.defaultNodeName;
                } else {
                    nodeName = node.getNodeName();
                }
            } else {
                logger.warning("Node is null. Setting node name as 'undefined'");
            }
            Double counter = (double) 1;
            if (NodeUsageMetric.nodeRunCounters.containsKey(nodeName)) {
                counter = NodeUsageMetric.nodeRunCounters.get(nodeName);
                counter = counter + 1;
            }
            NodeUsageMetric.nodeRunCounters.put(nodeName, counter);
            logger.fine(String.format("NodeUsageMetric is added for nodeName:%s - task:%s - counter:%s", nodeName, task.getFullDisplayName(), counter));
        }
    }
}
