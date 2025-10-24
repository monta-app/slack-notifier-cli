import * as fs from 'fs';
import { GitHubEvent, BaseGitHubContext } from '../models/types';

export interface GitHubEventInput {
  eventPath: string;
  repository: string;
  runId: string;
  workflow: string;
  refName: string;
}

export async function parseGitHubEvent(input: GitHubEventInput): Promise<GitHubEvent> {
  const eventJson = fs.readFileSync(input.eventPath, 'utf8');
  const context = parseEventFromJson(eventJson);

  return {
    repository: input.repository,
    refName: input.refName,
    runId: input.runId,
    displayName: context.displayName,
    commitSHA: context.sha,
    message: context.message,
    workflow: input.workflow,
    prUrl: context.prUrl
  };
}

function parseEventFromJson(eventJson: string): BaseGitHubContext {
  return parseOnJsonPush(eventJson) || 
         parseOnJsonOpened(eventJson) || 
         parseOnJsonCreated(eventJson) || 
         handleParseFailure();
}

function parseOnJsonPush(eventJson: string): BaseGitHubContext | null {
  try {
    const event = JSON.parse(eventJson);
    
    // Check if this looks like a push event
    if (event.head_commit && event.pusher) {
      return {
        displayName: event.pusher.name || event.pusher.username || event.pusher.email,
        sha: event.head_commit.id,
        message: event.head_commit.message,
        prUrl: undefined
      };
    }
    
    return null;
  } catch (error) {
    return null;
  }
}

function parseOnJsonOpened(eventJson: string): BaseGitHubContext | null {
  try {
    const event = JSON.parse(eventJson);
    
    // Check if this looks like a pull request opened event
    if (event.pull_request && event.action === 'opened') {
      return {
        displayName: event.pull_request.user.login,
        sha: event.pull_request.head.sha,
        message: event.pull_request.title,
        prUrl: event.pull_request.html_url
      };
    }
    
    return null;
  } catch (error) {
    return null;
  }
}

function parseOnJsonCreated(eventJson: string): BaseGitHubContext | null {
  try {
    const event = JSON.parse(eventJson);
    
    // Check if this looks like an issue created event
    if (event.issue && event.action === 'created') {
      return {
        displayName: event.sender.login,
        sha: undefined,
        message: event.issue.title,
        prUrl: event.issue.html_url
      };
    }
    
    return null;
  } catch (error) {
    return null;
  }
}

function handleParseFailure(): BaseGitHubContext {
  return {
    displayName: undefined,
    sha: undefined,
    message: undefined,
    prUrl: undefined
  };
}