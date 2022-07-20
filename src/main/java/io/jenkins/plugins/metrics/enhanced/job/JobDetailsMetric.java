package io.jenkins.plugins.metrics.enhanced.job;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.model.*;
import io.jenkins.plugins.metrics.enhanced.EnhancedMetrics;
import io.jenkins.plugins.metrics.enhanced.generic.label.AbstractGenericMetric;
import io.jenkins.plugins.metrics.enhanced.generic.label.GenericMetric;
import jenkins.branch.MultiBranchProject;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.*;
import java.util.logging.Logger;

public class JobDetailsMetric extends AbstractGenericMetric {

    private static Logger logger = Logger.getLogger(JobDetailsMetric.class.getName());

    public JobDetailsMetric() {
        super(
                EnhancedMetrics.generateMetricName("job_count_details"),
                "Build Count, Child Job Count, Child PR Count of the jobs",
                Arrays.asList("name","type","countType")
        );
    }

    public List<GenericMetric> getGenericMetrics(){
        List<GenericMetric> genericMetrics = new ArrayList<>();
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if( jenkins == null) {
            logger.warning("Jenkins Instance is null, returning empty metrics");
            return genericMetrics;
        }

        List<Item> items = jenkins.getAllItems();
        items.forEach(item -> {
            Boolean addMetrics = false;
            String name = item.getFullName();
            String type = item.getClass().getName();
            logger.finer(String.format("Processing Job:%s - Type:%s", name,type));
            Long buildCount = 0L;
            Long childJobCount = 0L;
            Long childPRCount = 0L;
            if( item instanceof Job){
                Job job = (Job) item;
                buildCount = (long) ((job.getLastBuild() != null) ? job.getLastBuild().getNumber() : 0);
                logger.fine(String.format("Getting buildCount for Job:%s", job.getFullDisplayName()));
                addMetrics = true;
            } else if ( item instanceof MultiBranchProject ){
                MultiBranchProject multiBranchProject = (MultiBranchProject) item;
                Collection subItems = multiBranchProject.getItems();
                childPRCount = subItems.stream().filter(subItem -> { return ((WorkflowJob)subItem).getName().startsWith("PR-"); }).count();
                childJobCount = ( subItems.size() - childPRCount);
                logger.fine(String.format("Getting buildCount/childPRCount/childJobCount for MultiBranchProject:%s", multiBranchProject.getFullDisplayName()));
                addMetrics = true;
            } else if( item instanceof Folder){
                logger.fine(String.format("Job Name:%s - Type:%s - Nothing to do for Folder type", name, type));
            }
            else {
                logger.warning(String.format("Job Name:%s - Type:%s - Undefined for JobDetailsMetric", name, type));
            }
            if( addMetrics) {
                genericMetrics.add(new GenericMetric(buildCount, name, type, "buildCount"));
                genericMetrics.add(new GenericMetric(childJobCount, name, type, "childJobCount"));
                genericMetrics.add(new GenericMetric(childPRCount, name, type, "childPRCount"));
            }
        });
        return genericMetrics;
    }
}
