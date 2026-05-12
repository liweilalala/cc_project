# Test Case Generation Plugin

A plugin for test case generation with workflow agents: requirement analysis, test requirement analysis, and test design.

## Structure

```
testcase-generate-plugin/
├── .opencode/plugins/
│   └── testDesigner.js          # OpenCode plugin entry point
├── .claude-plugin/
│   └── plugin.json              # Claude Code plugin metadata
├── .claude-code/hooks/          # Claude Code hooks
│   ├── hooks.json
│   ├── run-hook.cmd
│   └── session-start
├── skills/
│   ├── test-req-preprocessor/   # Agent: Requirement Analysis
│   │   └── SKILL.md
│   ├── test-requirement-analyst/ # Agent: Test Requirement Analysis
│   │   └── SKILL.md
│   └── test-design-expert/      # Agent: Test Design
│       └── SKILL.md
├── package.json
└── README.md
```

## Installation

### OpenCode

Add to `~/.config/opencode/opencode.json`:

```json
{
  "plugin": ["testcase-generate-plugin@file:///path/to/testcase-generate-plugin"]
}
```

### Claude Code

```bash
ln -s /path/to/testcase-generate-plugin ~/.claude/plugins/testcase-generate-plugin
```

## Agents

| Agent | Phase | Description |
|-------|-------|-------------|
| test-req-preprocessor | Phase 1 | Requirement analysis - 7 steps |
| test-requirement-analyst | Phase 2 | Test requirement analysis - 2 parallel branches |
| test-design-expert | Phase 3 | Test design - 2 parallel branches |

## Usage

After installation, use the `skill` tool to load specific workflow skills:

- `test-req-preprocessor` - Requirement analysis workflow
- `test-requirement-analyst` - Test requirement analysis workflow
- `test-design-expert` - Test design workflow