import * as core from '@actions/core';
import { SlackClient } from './slack-client';
import { GitHubEvent, JobType, JobStatus } from '../models/types';

export interface PublishSlackServiceConfig {
  serviceName?: string;
  serviceEmoji?: string;
  slackToken: string;
  slackChannelId: string;
}

export interface PublishParams {
  githubEvent: GitHubEvent;
  jobType: JobType;
  jobStatus: JobStatus;
  slackMessageId?: string;
}

export class PublishSlackService {
  private slackClient: SlackClient;

  constructor(config: PublishSlackServiceConfig) {
    this.slackClient = new SlackClient(config);
  }

  async publish(params: PublishParams): Promise<string> {
    const { githubEvent, jobType, jobStatus, slackMessageId } = params;

    let messageId: string;

    if (!slackMessageId) {
      // Create new message
      messageId = await this.slackClient.create(githubEvent, jobType, jobStatus);
      console.log(`Created new Slack message with ID: ${messageId}`);
    } else {
      // Update existing message
      messageId = await this.slackClient.update(slackMessageId, githubEvent, jobType, jobStatus);
      console.log(`Updated Slack message with ID: ${messageId}`);
    }

    // Write to GitHub Actions output (equivalent to the Kotlin writeToOutput function)
    this.writeToOutput('SLACK_MESSAGE_ID', messageId);

    return messageId;
  }

  private writeToOutput(key: string, value: string): void {
    console.log(`Writing to output ${key} ${value}`);
    
    // Use GitHub Actions core to set output
    core.setOutput(key, value);
    
    // Also write to GITHUB_OUTPUT file for compatibility
    const githubOutput = process.env.GITHUB_OUTPUT;
    if (githubOutput) {
      try {
        const fs = require('fs');
        fs.appendFileSync(githubOutput, `${key}=${value}\n`);
      } catch (error) {
        console.error('Failed to write to GITHUB_OUTPUT file:', error);
      }
    }
  }
}