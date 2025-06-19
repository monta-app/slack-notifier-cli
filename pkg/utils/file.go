package utils

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"os"
)

// ReadJSONFile reads and parses a JSON file
func ReadJSONFile(filepath string, target interface{}) error {
	data, err := ioutil.ReadFile(filepath)
	if err != nil {
		return fmt.Errorf("failed to read file %s: %w", filepath, err)
	}

	if err := json.Unmarshal(data, target); err != nil {
		return fmt.Errorf("failed to parse JSON from %s: %w", filepath, err)
	}

	return nil
}

// WriteJSONFile writes data to a JSON file
func WriteJSONFile(filepath string, data interface{}) error {
	jsonData, err := json.MarshalIndent(data, "", "  ")
	if err != nil {
		return fmt.Errorf("failed to marshal JSON: %w", err)
	}

	if err := ioutil.WriteFile(filepath, jsonData, 0644); err != nil {
		return fmt.Errorf("failed to write file %s: %w", filepath, err)
	}

	return nil
}

// FileExists checks if a file exists
func FileExists(filepath string) bool {
	_, err := os.Stat(filepath)
	return !os.IsNotExist(err)
}

// ReadFileBytes reads a file and returns its content as bytes
func ReadFileBytes(filepath string) ([]byte, error) {
	data, err := ioutil.ReadFile(filepath)
	if err != nil {
		return nil, fmt.Errorf("failed to read file %s: %w", filepath, err)
	}
	return data, nil
}