package io.jenkins.plugins.metrics.enhanced.node;

import hudson.Extension;
import hudson.model.*;
import hudson.model.Queue;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.label.AbstractGenericMetric;
import io.jenkins.plugins.metrics.enhanced.generic.label.GenericMetric;
import io.jenkins.plugins.metrics.enhanced.utils.Utils;

import java.util.*;
import java.util.logging.Logger;

public class NodeLabelUsageMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(NodeLabelUsageMetric.class.getName());
    private static HashMap<String, Double> labelRunCounters = new HashMap<>();

    public NodeLabelUsageMetric() {
        super(EnhancedMetrics.generateMetricName("node_label_run_count"), "Usage count of Labels", Collections.singletonList("label"));
    }

    @Override
    public List<GenericMetric> getGenericMetrics() {
        List<GenericMetric> genericMetrics = new ArrayList<>();
        NodeLabelUsageMetric.labelRunCounters.entrySet().forEach(entry -> {
            genericMetrics.add(
                    new GenericMetric(entry.getValue(), entry.getKey())
            );
        });
        return genericMetrics;
    }

    @Extension
    public static class NodeLabelUsageExecutionListener implements ExecutorListener {

        @Override
        public synchronized void taskAccepted(Executor executor, Queue.Task task) {
            String label = EnhancedMetrics.defaultNodeName;
            if( task.getAssignedLabel() != null)
                label = task.getAssignedLabel().getName();
            Double counter = Utils.increaseCounter(labelRunCounters,label);
            logger.fine(String.format("NodeLabelUsageMetric is added for label:%s - task:%s - counter:%s", label, task.getFullDisplayName(), counter));
        }
    }
}
