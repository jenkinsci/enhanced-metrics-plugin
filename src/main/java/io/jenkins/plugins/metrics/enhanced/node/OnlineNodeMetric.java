package io.jenkins.plugins.metrics.enhanced.node;

import hudson.model.Computer;
import hudson.model.Node;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.label.AbstractGenericMetric;
import io.jenkins.plugins.metrics.enhanced.generic.label.GenericMetric;
import jenkins.model.Jenkins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineNodeMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(OnlineNodeMetric.class.getName());

    public OnlineNodeMetric() {
        super(EnhancedMetrics.generateMetricName("online_nodes"), "Node Online Status",  Collections.singletonList("name"));
    }

    public List<GenericMetric> getGenericMetrics(){
        List<GenericMetric> metrics = new ArrayList<>();
        try {
            Jenkins jenkins = Jenkins.getInstanceOrNull();
            if (jenkins == null) {
                logger.warning("Jenkins Instance is null, returning empty metrics");
                return metrics;
            }
            List<Node> nodes = jenkins.getNodes();
            nodes.forEach(node -> {
                Computer computer = node.toComputer();
                GenericMetric genericMetric = new GenericMetric(
                        (computer != null && computer.isOnline()) ? 1 : 0,
                        node.getNodeName()
                );
                metrics.add(genericMetric);
                logger.fine(String.format("OnlineNodeMetric is added for node:%s - status:%s", node.getNodeName(), genericMetric.getValue()));
            });
            return metrics;
        }
        catch (Exception e){
            logger.log(Level.WARNING, e.getMessage(), e);
            return metrics;
        }
    }
}
