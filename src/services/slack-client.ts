import { WebClient } from '@slack/web-api';
import { GitHubEvent, JobType, JobStatus, SlackMessage, SlackBlock, SlackAttachment } from '../models/types';

export class SlackClient {
  private client: WebClient;
  private serviceName?: string;
  private serviceEmoji?: string;
  private slackChannelId: string;

  constructor(config: {
    serviceName?: string;
    serviceEmoji?: string;
    slackToken: string;
    slackChannelId: string;
  }) {
    this.client = new WebClient(config.slackToken);
    this.serviceName = config.serviceName;
    this.serviceEmoji = config.serviceEmoji;
    this.slackChannelId = config.slackChannelId;
  }

  async create(
    githubEvent: GitHubEvent,
    jobType: JobType,
    jobStatus: JobStatus
  ): Promise<string> {
    const message = this.generateMessageFromGithubEvent(
      githubEvent,
      jobType,
      jobStatus
    );

    const response = await this.client.chat.postMessage({
      channel: this.slackChannelId,
      text: message.text,
      blocks: message.blocks,
      attachments: message.attachments
    });

    if (!response.ts) {
      throw new Error('Failed to create Slack message - no timestamp returned');
    }

    return response.ts;
  }

  async update(
    messageId: string,
    githubEvent: GitHubEvent,
    jobType: JobType,
    jobStatus: JobStatus
  ): Promise<string> {
    // Get previous message to preserve existing attachments
    const previousMessage = await this.getSlackMessageById(messageId);
    
    const message = this.generateMessageFromGithubEvent(
      githubEvent,
      jobType,
      jobStatus,
      messageId,
      previousMessage?.attachments
    );

    const response = await this.client.chat.update({
      channel: this.slackChannelId,
      ts: messageId,
      text: message.text,
      blocks: message.blocks,
      attachments: message.attachments
    });

    if (!response.ts) {
      throw new Error('Failed to update Slack message - no timestamp returned');
    }

    return response.ts;
  }

  private async getSlackMessageById(messageId: string): Promise<any> {
    try {
      const response = await this.client.conversations.history({
        channel: this.slackChannelId,
        oldest: messageId,
        inclusive: true,
        limit: 1
      });

      return response.messages?.[0];
    } catch (error) {
      console.error('Failed to get Slack message:', error);
      return null;
    }
  }

  private generateMessageFromGithubEvent(
    githubEvent: GitHubEvent,
    jobType: JobType,
    jobStatus: JobStatus,
    messageId?: string,
    previousAttachments?: any[]
  ): SlackMessage {
    const attachments = new Map<JobType, SlackAttachment>();

    // Preserve previous attachments
    if (previousAttachments) {
      for (const attachment of previousAttachments) {
        if (attachment.jobType) {
          attachments.set(attachment.jobType, attachment);
        }
      }
    }

    // Add/update current job status
    attachments.set(jobType, {
      color: JobStatus.getColor(jobStatus),
      jobType,
      fields: [
        {
          title: JobType.getLabel(jobType),
          value: JobStatus.getMessage(jobStatus),
          short: false
        }
      ]
    });

    return this.generateSlackMessageFromEvent(
      githubEvent,
      messageId,
      Array.from(attachments.values())
    );
  }

  private generateSlackMessageFromEvent(
    githubEvent: GitHubEvent,
    messageId?: string,
    attachments?: SlackAttachment[]
  ): SlackMessage {
    const title = this.buildTitle(githubEvent);

    const blocks: SlackBlock[] = [
      {
        type: 'header',
        text: {
          type: 'plain_text',
          text: title
        }
      },
      {
        type: 'divider'
      },
      {
        type: 'section',
        fields: [
          {
            type: 'mrkdwn',
            text: ` \n*Branch:*\n${githubEvent.refName}`
          },
          {
            type: 'mrkdwn',
            text: ` \n*Run:*\n<${this.getRunUrl(githubEvent)}|${githubEvent.runId}>`
          },
          {
            type: 'mrkdwn',
            text: ` \n*Committer:*\n${githubEvent.displayName || 'Unknown'}`
          },
          {
            type: 'mrkdwn',
            text: ` \n*Message:*\n<${this.getChangeUrl(githubEvent)}|${this.getChangeMessage(githubEvent)}>`
          },
          {
            type: 'mrkdwn',
            text: ` \n*Change:*\n<${this.getChangeUrl(githubEvent)}|${this.getChangeIdentifier(githubEvent)}>`
          }
        ]
      },
      {
        type: 'divider'
      }
    ];

    return {
      channel: this.slackChannelId,
      ts: messageId,
      text: title,
      blocks,
      attachments
    };
  }

  private buildTitle(githubEvent: GitHubEvent): string {
    const parts = [];
    
    if (this.serviceEmoji) {
      parts.push(this.serviceEmoji);
    }
    
    parts.push(githubEvent.repository);
    
    if (githubEvent.workflow) {
      parts.push(githubEvent.workflow);
    }
    
    if (this.serviceName) {
      parts.push(this.serviceName);
    }

    return parts.join(' ');
  }

  private getRunUrl(githubEvent: GitHubEvent): string {
    return `https://github.com/${githubEvent.repository}/actions/runs/${githubEvent.runId}`;
  }

  private getChangeIdentifier(githubEvent: GitHubEvent): string {
    if (githubEvent.commitSHA) {
      return githubEvent.commitSHA;
    } else if (githubEvent.prUrl) {
      return this.getPRIdentifier(githubEvent.prUrl) || 'PR';
    }
    return 'Unknown';
  }

  private getChangeUrl(githubEvent: GitHubEvent): string {
    if (githubEvent.commitSHA) {
      return `https://github.com/${githubEvent.repository}/commit/${githubEvent.commitSHA}`;
    } else if (githubEvent.prUrl) {
      return githubEvent.prUrl;
    }
    return `https://github.com/${githubEvent.repository}/`;
  }

  private getChangeMessage(githubEvent: GitHubEvent): string {
    return githubEvent.message
      ?.replace(/\n/g, ' ')
      ?.replace(/\r/g, ' ')
      ?.replace(/</g, '')
      ?.replace(/>/g, '')
      ?.substring(0, 120) || 'No message';
  }

  private getPRIdentifier(url: string): string | null {
    const regex = /pull\/\d+/;
    const match = url.match(regex);
    return match ? match[0] : null;
  }
}