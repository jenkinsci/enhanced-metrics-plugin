package io.jenkins.plugins.metrics.enhanced.collector;

import hudson.Extension;
import io.jenkins.plugins.metrics.enhanced.job.JobDetailsMetric;
import io.jenkins.plugins.metrics.enhanced.node.NodeLabelUsageMetric;
import io.jenkins.plugins.metrics.enhanced.node.NodeUsageMetric;
import io.jenkins.plugins.metrics.enhanced.node.OnlineNodeMetric;
import io.jenkins.plugins.metrics.enhanced.user.UserMetric;
import io.prometheus.client.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Extension
public class MetricsCollector extends Collector {

    private static Logger logger = Logger.getLogger(MetricsCollector.class.getName());

    @Override
    public List<MetricFamilySamples> collect() {
        logger.info("Adding Jenkins Enhanced Metrics to Prometheus Collector");
        List<MetricFamilySamples> metricFamilySamples = new ArrayList<>();
        metricFamilySamples.add(new OnlineNodeMetric().getCounterMetricFamily());
        metricFamilySamples.add(new NodeLabelUsageMetric().getCounterMetricFamily());
        metricFamilySamples.add(new UserMetric().getCounterMetricFamily());
        metricFamilySamples.add(new NodeUsageMetric().getCounterMetricFamily());
        metricFamilySamples.add(new JobDetailsMetric().getCounterMetricFamily());
        return metricFamilySamples;
    }
}
