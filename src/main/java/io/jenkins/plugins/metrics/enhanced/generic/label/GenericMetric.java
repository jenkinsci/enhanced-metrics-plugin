package io.jenkins.plugins.metrics.enhanced.generic.label;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class GenericMetric {

    private List<String> labelValues;
    private double value;

    public GenericMetric(double value, String labelValue) {
        this.labelValues = Collections.singletonList(labelValue);
        this.value = value;
    }

    public GenericMetric(double value, String... labelValue) {
        this.labelValues = Arrays.asList(labelValue);
        this.value = value;
    }

}
