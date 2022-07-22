package io.jenkins.plugins.metrics.enhanced.utils;

import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Hudson;
import hudson.model.Node;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import jenkins.model.Jenkins;

import java.util.HashMap;
import java.util.logging.Logger;

public class Utils {

    private static Logger logger = Logger.getLogger(Utils.class.getName());

    public static Double calculateFreeCounter(Double totalCounter, Double inUseCounter){
        Double freeCount = totalCounter - inUseCounter;
        if( freeCount < (double)0)
            freeCount = (double)0;
        return freeCount;
    }

    public static String getNodeName(Executor executor){
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
        return nodeName;
    }

    public static Double getNodeExecutorNum(Executor executor){
        Double executorNum = (double)0;
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if( jenkins == null){
            logger.warning("Jenkins instance is null, skipping nodeRunCounters initialization");
            return (double)0;
        }
        Computer computer = executor.getOwner();
        Node node = computer.getNode();
        if (node != null) {
            if (node instanceof Hudson) {
                // Means it is running on the builtinNode
                logger.fine("Node is instance of Hudson. Setting node name as 'builtin'");
                executorNum = (double) jenkins.getNumExecutors();
            } else {
                executorNum = (double) node.getNumExecutors();
            }
        } else {
            logger.warning("Node is null. Setting node name as 'undefined'");
        }
        return executorNum;
    }

    public static Double increaseCounter(HashMap<String, Double> counter, String keyName){
        Double value = (double) 1;
        if (counter.containsKey(keyName)) {
            value = counter.get(keyName);
            value = value + 1;
        }
        counter.put(keyName, value);
        return value;
    }
    public static Double decreaseCounter(HashMap<String, Double> counter, String keyName){
        Double value = (double) 0;
        if (counter.containsKey(keyName)) {
            value = counter.get(keyName);
            value = value - 1;
            if( value < 0)
                value = (double)0;
        }
        counter.put(keyName, value);
        return value;
    }

    public static Double getCounter(HashMap<String, Double> counter, String keyName){
        if( !counter.containsKey(keyName))
            return (double)0;
        Double value = counter.get(keyName);
        if( value < 0)
            value = (double)0;
        return value;
    }

    public static Double setCounter(HashMap<String, Double> counter, String keyName, Double value){
        counter.put(keyName,value);
        return value;
    }

    public static Double setCounterIfNotExists(HashMap<String, Double> counter, String keyName, Double value){
        if( !counter.containsKey(keyName))
            counter.put(keyName,value);
        return value;
    }

    public static Jenkins getJenkins() throws Exception {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if( jenkins == null){
            throw new Exception("Failed to get Jenkins Instance Object");
        }
        return jenkins;
    }
}
