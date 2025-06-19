package models

// JobType represents the type of job being executed
type JobType string

const (
	JobTypeTest        JobType = "Test"
	JobTypeBuild       JobType = "Build"
	JobTypeDeploy      JobType = "Deploy"
	JobTypePublishDocs JobType = "PublishDocs"
)

// DisplayName returns the display name with emoji for the job type
func (jt JobType) DisplayName() string {
	switch jt {
	case JobTypeTest:
		return "Test 🧪"
	case JobTypeBuild:
		return "Build 🏗️"
	case JobTypeDeploy:
		return "Deploy 📦"
	case JobTypePublishDocs:
		return "Publish Docs 🧩"
	default:
		return string(jt)
	}
}

// JobStatus represents the status of a job
type JobStatus string

const (
	JobStatusProgress  JobStatus = "Progress"
	JobStatusSuccess   JobStatus = "Success"
	JobStatusFailure   JobStatus = "Failure"
	JobStatusCancelled JobStatus = "Cancelled"
	JobStatusUnknown   JobStatus = "Unknown"
)

// Color returns the Slack color for the job status
func (js JobStatus) Color() string {
	switch js {
	case JobStatusProgress:
		return "#DBAB09"
	case JobStatusSuccess:
		return "#00FF00"
	case JobStatusFailure:
		return "#FF0000"
	case JobStatusCancelled:
		return "#FFFF00"
	case JobStatusUnknown:
		return "#DBAB09"
	default:
		return "#DBAB09"
	}
}

// DisplayName returns the display name with emoji for the job status
func (js JobStatus) DisplayName() string {
	switch js {
	case JobStatusProgress:
		return "In Progress 🚧"
	case JobStatusSuccess:
		return "Success ✅"
	case JobStatusFailure:
		return "Failure ❌"
	case JobStatusCancelled:
		return "Cancelled ⚠️"
	case JobStatusUnknown:
		return "Something went wrong ❓"
	default:
		return string(js)
	}
}