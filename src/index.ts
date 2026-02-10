import * as core from '@actions/core';
import { PublishSlackService } from './services/publish-slack-service';
import { parseGitHubEvent } from './utils/github-event-parser';
import { JobType, JobStatus } from './models/types';

async function run(): Promise<void> {
  try {
    // Get inputs from GitHub Action
    const githubEventPath = core.getInput('github-event-path') || process.env.GITHUB_EVENT_PATH || '';
    const githubRepository = core.getInput('github-repository') || process.env.GITHUB_REPOSITORY || '';
    const githubRunId = core.getInput('github-run-id') || process.env.GITHUB_RUN_ID || '';
    const githubWorkflow = core.getInput('github-workflow') || process.env.GITHUB_WORKFLOW || '';
    const githubRefName = core.getInput('github-ref-name') || process.env.GITHUB_REF_NAME || '';
    
    const serviceName = core.getInput('service-name') || process.env.PUBLISH_SLACK_SERVICE_NAME || undefined;
    const serviceEmoji = core.getInput('service-emoji') || process.env.PUBLISH_SLACK_SERVICE_EMOJI || undefined;
    const jobType = core.getInput('job-type') || process.env.PUBLISH_SLACK_JOB_TYPE || '';
    const jobStatus = core.getInput('job-status') || process.env.PUBLISH_SLACK_JOB_STATUS || '';
    const slackToken = core.getInput('slack-token') || process.env.SLACK_APP_TOKEN || '';
    const slackChannelId = core.getInput('slack-channel-id') || process.env.SLACK_CHANNEL_ID || '';
    const slackMessageId = core.getInput('slack-message-id') || process.env.SLACK_MESSAGE_ID || undefined;

    // Validate required inputs
    if (!githubEventPath) throw new Error('github-event-path is required');
    if (!githubRepository) throw new Error('github-repository is required');
    if (!githubRunId) throw new Error('github-run-id is required');
    if (!githubWorkflow) throw new Error('github-workflow is required');
    if (!githubRefName) throw new Error('github-ref-name is required');
    if (!jobType) throw new Error('job-type is required');
    if (!jobStatus) throw new Error('job-status is required');
    if (!slackToken) throw new Error('slack-token is required');
    if (!slackChannelId) throw new Error('slack-channel-id is required');

    // Parse GitHub event
    const githubEvent = await parseGitHubEvent({
      eventPath: githubEventPath,
      repository: githubRepository,
      runId: githubRunId,
      workflow: githubWorkflow,
      refName: githubRefName
    });

    // Create service and publish notification
    const publishService = new PublishSlackService({
      serviceName: serviceName || undefined,
      serviceEmoji: serviceEmoji || undefined,
      slackToken,
      slackChannelId
    });

    const messageId = await publishService.publish({
      githubEvent,
      jobType: JobType.fromString(jobType),
      jobStatus: JobStatus.fromString(jobStatus),
      slackMessageId: slackMessageId || undefined
    });

    // Set output for subsequent workflow steps
    core.setOutput('slack-message-id', messageId);
    
    console.log(`Successfully published Slack notification. Message ID: ${messageId}`);
  } catch (error) {
    core.setFailed(error instanceof Error ? error.message : 'Unknown error occurred');
  }
}

run();