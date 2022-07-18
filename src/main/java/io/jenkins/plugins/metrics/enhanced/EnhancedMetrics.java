package io.jenkins.plugins.metrics.enhanced;

public class EnhancedMetrics {

    public static final String metricPrefix = "jenkins_enhanced_metrics";
    public static String generateMetricName(String metricName){
        return String.format("%s_%s", metricPrefix,metricName);
    }
}
