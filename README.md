Jenkins Enhanced Metrics Plugin
===============================

This plugin adds more detailed metrics for Jobs and Nodes by using Prometheus Collector.

# Enhanced Metrics

## User Metrics

```jenkins_enhanced_metrics_user_count_total``` (counter)
<br>&nbsp;&nbsp;The number of users defined in Jenkins.

## Node Metrics

```jenkins_enhanced_metrics_online_nodes_total{name="<Name of the Node>"}``` (value)
<br>&nbsp;&nbsp;Indicates if the node is online (1) or offline (0). 

```jenkins_enhanced_metrics_node_label_run_count_total{label="Name of the Node label"}``` (counter)
<br>&nbsp;&nbsp;Number of runs for each Node Label.

```jenkins_enhanced_metrics_node_run_count_total{name="Name of the Node"}``` (counter)
<br>&nbsp;&nbsp;Number of runs for each Node.

## Job Metrics

```jenkins_enhanced_metrics_job_count_details_total{name="<Name of the Job", type="<Class Name of the Job>",countType="buildCount/childJobCount/childPRCount" }``` (counter)
<br>&nbsp;&nbsp;Provides details for all Job defined in Jenkins.
<br>&nbsp;&nbsp;*buildCount*: Number of the last build.
<br>&nbsp;&nbsp;*childJobCount*: Number of the child jobs (If any).
<br>&nbsp;&nbsp;*childPRCount*: Number of the PR jobs (If any).
