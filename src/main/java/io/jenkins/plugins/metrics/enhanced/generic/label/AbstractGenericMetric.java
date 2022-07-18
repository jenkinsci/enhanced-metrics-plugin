package io.jenkins.plugins.metrics.enhanced.generic.label;

import io.prometheus.client.CounterMetricFamily;

import java.util.List;

public abstract class AbstractGenericMetric implements IGenericMetric {

    private String metricName;
    private String metricHelp;
    private List<String> labelNames;

    public AbstractGenericMetric(String metricName, String metricHelp, List<String> labelNames) {
        this.metricName = metricName;
        this.metricHelp = metricHelp;
        this.labelNames = labelNames;
    }

    public CounterMetricFamily getCounterMetricFamily(){
        CounterMetricFamily counterMetricFamily = new CounterMetricFamily(this.metricName, this.metricHelp, this.labelNames);
        this.getGenericMetrics().forEach(genericMetric -> {
            counterMetricFamily.addMetric(genericMetric.getLabelValues(),genericMetric.getValue());
        });
        return counterMetricFamily;
    }

}
