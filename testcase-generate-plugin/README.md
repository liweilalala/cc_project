# Test Case Generation Plugin for OpenCode

## Overview

This plugin provides a comprehensive test case generation workflow that transforms design documents (Word or Markdown) into structured test cases.

## Structure

```
testcase-generate-plugin/
├── .opencode/
│   └── plugins/
│       └── testDesigner.js      # Plugin entry point
├── skills/
│   └── testcase-generate-workflow/
│       └── SKILL.md             # Main workflow skill
└── package.json                 # Package configuration
```

## Installation

### Option 1: Local Installation

Reference the plugin in your `opencode.json`:

```json
{
  "plugin": ["testcase-generate-plugin@file:///home/admin/workspace/cc_project/testcase-generate-plugin"]
}
```

### Option 2: Git Installation

```json
{
  "plugin": ["testcase-generate-plugin@git+https://github.com/your-repo/testcase-generate-plugin.git"]
}
```

## Workflow

The plugin implements a three-phase workflow:

1. **Requirement Analysis** - Preprocess and analyze design documents
2. **Test Requirement Analysis** - Extract feature and implementation atomic capabilities
3. **Test Design** - Generate logical and interface test cases

See `skills/testcase-generate-workflow/SKILL.md` for detailed workflow documentation.

## Supported Skills

| Skill | Purpose |
|-------|---------|
| requirement-document-preprocessor | Format requirement documents |
| mece-decomposition | Customer problem identification |
| 5w2h-analysis | 5W2H analysis |
| term-dictionary | Terminology replacement |
| rule-split | Rule extraction |
| usecase-extraction | Usecase extraction |
| usecase-rule-matcher | Match usecases with rules |
| feature-tree-atomic-extraction | Feature atomic extraction |
| feature-atomic-ibo-filler | IBO filling for features |
| implementation-atomic-capability-extractor | Implementation atomic extraction |
| interface-ibo-filler | IBO filling for interfaces |
| feature-atomic-capability-testpoint-generation | Generate logical testpoints |
| interface-testpoint-generation | Generate interface testpoints |
| testpoint-to-testcase | Convert testpoints to testcases |
| logic-testcase-to-excel | Export logical testcases to Excel |
| interface-testcase-to-excel | Export interface testcases to Excel |