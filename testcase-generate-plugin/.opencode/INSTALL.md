# Installing testcase-generate-plugin for OpenCode

## Prerequisites

- [OpenCode.ai](https://opencode.ai) installed

## Installation

Add to the `plugin` array in your `opencode.json` (global or project-level):

```json
{
  "plugin": ["testcase-generate-plugin@file:///path/to/testcase-generate-plugin"]
}
```

Restart OpenCode. The plugin registers all skills automatically.

## Usage

Use the `skill` tool to load specific workflow skills:

```
skill tool to list skills
skill tool to load test-req-preprocessor
skill tool to load test-requirement-analyst
skill tool to load test-design-expert
```

## Available Skills

| Skill | Phase | Description |
|-------|-------|-------------|
| test-req-preprocessor | Phase 1 | Requirement analysis - 7 steps |
| test-requirement-analyst | Phase 2 | Test requirement analysis - 2 parallel branches |
| test-design-expert | Phase 3 | Test design - 2 parallel branches |

## Updating

Remove the plugin line from `opencode.json`, restart OpenCode, then re-add it to get the latest version.