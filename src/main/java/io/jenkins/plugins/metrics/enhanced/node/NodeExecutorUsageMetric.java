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

public class NodeExecutorUsageMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(NodeExecutorUsageMetric.class.getName());
    private static HashMap<String, Double> executorTotalCount = new HashMap<>();
    private static HashMap<String, Double> executorFreeCount = new HashMap<>();
    private static HashMap<String, Double> executorInUseCount = new HashMap<>();

    public NodeExecutorUsageMetric() {
        super(EnhancedMetrics.generateMetricName("node_executor_usage_count"), "Usage Count of Executors", Arrays.asList("name", "countType"));
        NodeExecutorUsageMetric.initCounters();
    }

    private synchronized static void initCounters() {
        try {
            Jenkins jenkins = Utils.getJenkins();
            Computer[] computers = jenkins.getComputers();
            Arrays.stream(computers).forEach(computer -> {
                Double totalCount = (double) 0;
                String nodeName = "undefined";
                if (computer.getNode() instanceof Hudson) {
                    nodeName = EnhancedMetrics.defaultNodeName;
                    totalCount = (double) jenkins.getNumExecutors();
                } else {
                    if (computer.getNode() != null) {
                        nodeName = computer.getNode().getNodeName();
                        totalCount = (double) computer.getNode().getNumExecutors();
                    }
                }
                Double inUseCount = Utils.getCounter(executorInUseCount, nodeName);
                Double freeCount = totalCount - inUseCount;
                Utils.setCounter(executorTotalCount, nodeName, totalCount);
                Utils.setCounter(executorFreeCount, nodeName, freeCount);
                Utils.setCounter(executorInUseCount, nodeName, inUseCount);
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }

    }

    @Override
    public List<GenericMetric> getGenericMetrics() {
        List<GenericMetric> genericMetrics = new ArrayList<>();
        NodeExecutorUsageMetric.executorTotalCount.entrySet().forEach(entry -> {
            genericMetrics.add(
                    new GenericMetric(entry.getValue(), entry.getKey(), "executorTotalCount")
            );
        });
        NodeExecutorUsageMetric.executorFreeCount.entrySet().forEach(entry -> {
            genericMetrics.add(
                    new GenericMetric(entry.getValue(), entry.getKey(), "executorFreeCount")
            );
        });
        NodeExecutorUsageMetric.executorInUseCount.entrySet().forEach(entry -> {
            genericMetrics.add(
                    new GenericMetric(entry.getValue(), entry.getKey(), "executorInUseCount")
            );
        });
        return genericMetrics;
    }

    @Extension
    public static class NodeUsageExecutionListener implements ExecutorListener {

        @Override
        public synchronized void taskAccepted(Executor executor, Queue.Task task) {
            NodeExecutorUsageMetric.initCounters();
            String nodeName = Utils.getNodeName(executor);
            Double totalCounter = Utils.getNodeExecutorNum(executor);
            Double inUseCounter = Utils.increaseCounter(executorInUseCount, nodeName);
            Double freeCounter = Utils.setCounter(executorFreeCount, nodeName, (totalCounter - inUseCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:inUseCounter - value:%s", nodeName, inUseCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:freeCounter - value:%s", nodeName, freeCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:totalCounter - value:%s", nodeName, totalCounter));
        }

        @Override
        public synchronized void taskCompleted(Executor executor, Queue.Task task, long durationMS) {
            NodeExecutorUsageMetric.initCounters();
            String nodeName = Utils.getNodeName(executor);
            Double totalCounter = Utils.getNodeExecutorNum(executor);
            Double inUseCounter = Utils.decreaseCounter(executorInUseCount, nodeName);
            Double freeCounter = Utils.setCounter(executorFreeCount, nodeName, (totalCounter - inUseCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:inUseCounter - value:%s", nodeName, inUseCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:freeCounter - value:%s", nodeName, freeCounter));
            logger.fine(String.format("NodeExecutorUsageMetric is added for nodeName:%s - counter:totalCounter - value:%s", nodeName, totalCounter));
        }
    }
}
