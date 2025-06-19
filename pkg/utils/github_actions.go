package utils

import (
	"fmt"
	"os"
)

// SetGitHubOutput sets an output variable for GitHub Actions
func SetGitHubOutput(name, value string) error {
	// Get the GitHub output file path
	outputFile := os.Getenv("GITHUB_OUTPUT")
	if outputFile == "" {
		// Fallback to stdout if GITHUB_OUTPUT is not set (for local testing)
		fmt.Printf("::set-output name=%s::%s\n", name, value)
		return nil
	}

	// Open the output file in append mode
	file, err := os.OpenFile(outputFile, os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0644)
	if err != nil {
		return fmt.Errorf("failed to open GitHub output file: %w", err)
	}
	defer file.Close()

	// Write the output in the format expected by GitHub Actions
	_, err = fmt.Fprintf(file, "%s=%s\n", name, value)
	if err != nil {
		return fmt.Errorf("failed to write to GitHub output file: %w", err)
	}

	return nil
}

// GetGitHubEnv gets a GitHub environment variable with a fallback
func GetGitHubEnv(key, fallback string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return fallback
}

// IsGitHubActions checks if we're running in GitHub Actions environment
func IsGitHubActions() bool {
	return os.Getenv("GITHUB_ACTIONS") == "true"
}
