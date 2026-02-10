export enum JobType {
  Test = 'Test',
  Build = 'Build',
  Deploy = 'Deploy',
  PublishDocs = 'PublishDocs'
}

export namespace JobType {
  export function fromString(value: string): JobType {
    const jobType = Object.values(JobType).find(type => 
      type.toLowerCase() === value.toLowerCase()
    );
    if (!jobType) {
      throw new Error(`Unknown job type: ${value}`);
    }
    return jobType;
  }

  export function getLabel(jobType: JobType): string {
    switch (jobType) {
      case JobType.Test:
        return 'Test :test_tube:';
      case JobType.Build:
        return 'Build :building_construction:️';
      case JobType.Deploy:
        return 'Deploy :package:';
      case JobType.PublishDocs:
        return 'Publish Docs :jigsaw:';
      default:
        return jobType;
    }
  }
}

export enum JobStatus {
  Progress = 'Progress',
  Success = 'Success',
  Failure = 'Failure',
  Cancelled = 'Cancelled',
  Unknown = 'Unknown'
}

export namespace JobStatus {
  export function fromString(value: string): JobStatus {
    const status = Object.values(JobStatus).find(status => 
      status.toLowerCase() === value.toLowerCase()
    );
    return status || JobStatus.Unknown;
  }

  export function getMessage(status: JobStatus): string {
    switch (status) {
      case JobStatus.Progress:
        return 'In Progress :construction:';
      case JobStatus.Success:
        return 'Success :white_check_mark:';
      case JobStatus.Failure:
        return 'Failure :x:';
      case JobStatus.Cancelled:
        return 'Cancelled :warning:';
      case JobStatus.Unknown:
        return 'Something went wrong :question:';
      default:
        return 'Unknown status';
    }
  }

  export function getColor(status: JobStatus): string {
    switch (status) {
      case JobStatus.Progress:
        return '#DBAB09';
      case JobStatus.Success:
        return '#00FF00';
      case JobStatus.Failure:
        return '#FF0000';
      case JobStatus.Cancelled:
        return '#FFFF00';
      case JobStatus.Unknown:
        return '#DBAB09';
      default:
        return '#DBAB09';
    }
  }
}

export interface GitHubEvent {
  repository: string;
  refName: string;
  runId: string;
  displayName?: string;
  commitSHA?: string;
  message?: string;
  workflow?: string;
  prUrl?: string;
}

export interface SlackMessage {
  channel: string;
  ts?: string;
  text: string;
  blocks: SlackBlock[];
  attachments?: SlackAttachment[];
}

export interface SlackBlock {
  type: string;
  text?: {
    type: string;
    text: string;
  };
  fields?: Array<{
    type: string;
    text: string;
  }>;
}

export interface SlackAttachment {
  color: string;
  fields: Array<{
    title: string;
    value: string;
    short: boolean;
  }>;
  jobType?: JobType;
}

export interface BaseGitHubContext {
  displayName?: string;
  sha?: string;
  message?: string;
  prUrl?: string;
}