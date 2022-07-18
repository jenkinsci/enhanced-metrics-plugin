package io.jenkins.plugins.metrics.enhanced.user;

import hudson.model.User;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.value.AbstractGenericMetric;

import java.util.logging.Logger;


public class UserMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(UserMetric.class.getName());

    public UserMetric() {
        super(EnhancedMetrics.generateMetricName("user_count"), "Total Number of Users");
    }

    @Override
    public Double getGenericMetric() {
        return (double) User.getAll().size();
    }
}
