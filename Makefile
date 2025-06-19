# Slack Notifier CLI - Build Configuration

# Binary name
BINARY_NAME=slack-notifier-cli

# Version
VERSION=2.0.0

# Build directory
BUILD_DIR=bin

# Go build flags
LDFLAGS=-ldflags "-X main.Version=$(VERSION) -s -w"

# Default target
.PHONY: all
all: clean build

# Clean build artifacts
.PHONY: clean
clean:
	rm -rf $(BUILD_DIR)
	go clean

# Build for current platform
.PHONY: build
build:
	mkdir -p $(BUILD_DIR)
	go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME) .

# Cross-compile for all supported platforms
.PHONY: build-all
build-all: clean
	mkdir -p $(BUILD_DIR)
	
	# macOS ARM64
	GOOS=darwin GOARCH=arm64 go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME)-darwin-arm64 .
	
	# macOS x64
	GOOS=darwin GOARCH=amd64 go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME)-darwin-amd64 .
	
	# Linux x64
	GOOS=linux GOARCH=amd64 go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME)-linux-amd64 .
	
	# Linux ARM64
	GOOS=linux GOARCH=arm64 go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME)-linux-arm64 .
	
	# Windows x64
	GOOS=windows GOARCH=amd64 go build $(LDFLAGS) -o $(BUILD_DIR)/$(BINARY_NAME)-windows-amd64.exe .

# Install dependencies
.PHONY: deps
deps:
	go mod download
	go mod tidy

# Run tests
.PHONY: test
test:
	go test -v ./...

# Run tests with coverage
.PHONY: test-coverage
test-coverage:
	go test -v -coverprofile=coverage.out ./...
	go tool cover -html=coverage.out -o coverage.html

# Format code
.PHONY: fmt
fmt:
	go fmt ./...

# Lint code
.PHONY: lint
lint:
	golangci-lint run

# Run the application (development)
.PHONY: run
run:
	go run . publish --help

# Quick development build and test
.PHONY: dev
dev: fmt build test

# Create release archives
.PHONY: release
release: build-all
	cd $(BUILD_DIR) && \
	tar -czf $(BINARY_NAME)-darwin-arm64.tar.gz $(BINARY_NAME)-darwin-arm64 && \
	tar -czf $(BINARY_NAME)-darwin-amd64.tar.gz $(BINARY_NAME)-darwin-amd64 && \
	tar -czf $(BINARY_NAME)-linux-amd64.tar.gz $(BINARY_NAME)-linux-amd64 && \
	tar -czf $(BINARY_NAME)-linux-arm64.tar.gz $(BINARY_NAME)-linux-arm64 && \
	zip $(BINARY_NAME)-windows-amd64.zip $(BINARY_NAME)-windows-amd64.exe

# Help
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  all         - Clean and build for current platform"
	@echo "  build       - Build for current platform"
	@echo "  build-all   - Cross-compile for all supported platforms"
	@echo "  clean       - Remove build artifacts"
	@echo "  deps        - Install dependencies"
	@echo "  test        - Run tests"
	@echo "  test-coverage - Run tests with coverage report"
	@echo "  fmt         - Format code"
	@echo "  lint        - Run linter"
	@echo "  run         - Run application in development mode"
	@echo "  dev         - Quick development cycle (format, build, test)"
	@echo "  release     - Create release archives"
	@echo "  help        - Show this help message"