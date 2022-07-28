package io.jenkins.plugins.metrics.enhanced.script;

import hudson.FilePath;
import hudson.util.RemotingDiagnostics;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.value.AbstractGenericMetric;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptApproveWaitingMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(ScriptApproveWaitingMetric.class.getName());
    private final String getWaitingApprovalsScript =
            "import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval\n" +
            "ScriptApproval scriptApproval = ScriptApproval.get()\n" +
            "println(scriptApproval.getPendingScripts().size() + scriptApproval.getPendingSignatures().size() + scriptApproval.getPendingClasspathEntries().size())";

    public ScriptApproveWaitingMetric() {
        super(EnhancedMetrics.generateMetricName("script_approve_waiting_count"), "Provides number of script waiting for Administrative Approval");
    }

    @Override
    public Double getGenericMetric() {
        try {
            String scriptResult =  RemotingDiagnostics.executeGroovy(getWaitingApprovalsScript, FilePath.localChannel);
            logger.fine(String.format("ScriptApproveWaitingMetric is calculated. Approval waiting scripts:%s",scriptResult));
            return Double.valueOf(scriptResult);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return (double)-1;
        }
    }
}
