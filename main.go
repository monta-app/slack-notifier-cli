package main

import (
	"log"
	"os"

	"github.com/urfave/cli/v2"
	"slack-notifier-cli/cmd"
)

func main() {
	app := &cli.App{
		Name:        "slack-notifier-cli",
		Usage:       "Send Slack notifications from GitHub Actions",
		Description: "A CLI tool for sending structured Slack notifications from GitHub Actions workflows",
		Version:     "2.0.0",
		Authors: []*cli.Author{
			{
				Name: "Monta",
			},
		},
		Commands: []*cli.Command{
			cmd.PublishCommand(),
		},
		// Default action if no command is specified - run publish
		Action: func(c *cli.Context) error {
			return cmd.PublishCommand().Action(c)
		},
	}

	if err := app.Run(os.Args); err != nil {
		log.Fatal(err)
	}
}
