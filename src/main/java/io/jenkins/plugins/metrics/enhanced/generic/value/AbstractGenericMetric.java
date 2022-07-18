package io.jenkins.plugins.metrics.enhanced.generic.value;

import io.prometheus.client.CounterMetricFamily;

public abstract class AbstractGenericMetric implements IGenericMetric {

    private String metricName;
    private String metricHelp;

    public AbstractGenericMetric(String metricName, String metricHelp) {
        this.metricName = metricName;
        this.metricHelp = metricHelp;
    }

    public CounterMetricFamily getCounterMetricFamily(){
        return new CounterMetricFamily(this.metricName, this.metricHelp, this.getGenericMetric());
    }
}
