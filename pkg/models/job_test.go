package models

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestJobType_DisplayName(t *testing.T) {
	tests := []struct {
		jobType  JobType
		expected string
	}{
		{JobTypeTest, "Test 🧪"},
		{JobTypeBuild, "Build 🏗️"},
		{JobTypeDeploy, "Deploy 📦"},
		{JobTypePublishDocs, "Publish Docs 🧩"},
		{JobType("Unknown"), "Unknown"},
	}

	for _, tt := range tests {
		t.Run(string(tt.jobType), func(t *testing.T) {
			assert.Equal(t, tt.expected, tt.jobType.DisplayName())
		})
	}
}

func TestJobStatus_Color(t *testing.T) {
	tests := []struct {
		status   JobStatus
		expected string
	}{
		{JobStatusProgress, "#DBAB09"},
		{JobStatusSuccess, "#00FF00"},
		{JobStatusFailure, "#FF0000"},
		{JobStatusCancelled, "#FFFF00"},
		{JobStatusUnknown, "#DBAB09"},
		{JobStatus("Unknown"), "#DBAB09"},
	}

	for _, tt := range tests {
		t.Run(string(tt.status), func(t *testing.T) {
			assert.Equal(t, tt.expected, tt.status.Color())
		})
	}
}

func TestJobStatus_DisplayName(t *testing.T) {
	tests := []struct {
		status   JobStatus
		expected string
	}{
		{JobStatusProgress, "In Progress 🚧"},
		{JobStatusSuccess, "Success ✅"},
		{JobStatusFailure, "Failure ❌"},
		{JobStatusCancelled, "Cancelled ⚠️"},
		{JobStatusUnknown, "Something went wrong ❓"},
		{JobStatus("Unknown"), "Something went wrong ❓"},
	}

	for _, tt := range tests {
		t.Run(string(tt.status), func(t *testing.T) {
			assert.Equal(t, tt.expected, tt.status.DisplayName())
		})
	}
}
