---
name: testcase-generate-workflow
description: Test case generation workflow from design documents
version: 1.0.0
trigger: test-designer
---

# Test Case Generation Workflow

## Overview

This workflow generates comprehensive test cases from design documents (Word or Markdown). It follows a structured three-phase process: Requirement Analysis, Test Requirement Analysis, and Test Design.

## Input

- **Design Document**: Word (.docx) or Markdown (.md) format
- **Task ID**: Generated at workflow start for tracking

## Workflow Structure

### Phase 0: Initialization

1. **Generate Task ID**: Create a unique task ID (e.g., `TC-${timestamp}`)
2. **Create Task Directory**: Create a folder named with the task ID
3. **Document Conversion**:
   - If input is Word (.docx) → Convert to `doc.md` using word-to-markdown
   - If input is Markdown → Treat as `doc.md`
4. Store all intermediate outputs in the task directory

---

### Phase 1: Requirement Analysis

**Executor**: test-req-preprocessor

#### Step 1.1: Requirement Formatting
- Use `requirement-document-preprocessor` skill
- Input: `doc.md`
- Output: `requirements.md`

#### Step 1.2: Customer Problem Identification
- Use `mece-decomposition` skill
- Input: `requirements.md`, `doc.md`
- Output: `sub_problems.json`

#### Step 1.3: 5W2H Analysis
- Use `5w2h-analysis` skill
- Input: `sub_problems.json`, `doc.md`
- Output: `sub_problems_5w2h.json`

#### Step 1.4: Term Replacement
- Use `term-dictionary` skill
- Input: `sub_problems_5w2h.json`, `sub_problems.json`
- Output: `sub_problems_5w2h_replaced.json`, `sub_problems_replaced.json`

#### Step 1.5: Rule Splitting
- Use `rule-split` skill
- Input: `sub_problems_5w2h_replaced.json`
- Output: `rule_l3_atom.json`, `rule_l4_factor.json`, `rule_l3_l4_relation.json`

#### Step 1.6: Usecase Extraction
- Use `usecase-extraction` skill
- Input: `sub_problems_5w2h_replaced.json`
- Output: `usecase.json`, `non_func_req.json`

#### Step 1.7: Usecase-Rule Matching
- Use `usecase-rule-matcher` skill
- Input: `usecase.json`, `rule_l3_atom.json`
- Output: `usecase_with_rule.json`

---

### Phase 2: Test Requirement Analysis

**Executor**: test-requirement-analyst

#### Branch 1: Feature Atomic Capability Extraction

##### Step 2.1.1: Feature Tree Atomic Extraction
- Use `feature-tree-atomic-extraction` skill
- Input: `doc.md`
- Output: `feature_atomic.json`

##### Step 2.1.2: Feature Atomic IBO Filler
- Use `feature-atomic-ibo-filler` skill
- Input: `feature_atomic.json`, `doc.md`, `rule_l3_atom.json`
- Output: `feature_atomic_ibo.json`

#### Branch 2: Implementation Atomic Capability Extraction

##### Step 2.2.1: Implementation Atomic Capability Extractor
- Use `implementation-atomic-capability-extractor` skill
- Input: `doc.md`
- Output: `implementation_atomic.json`

##### Step 2.2.2: Interface IBO Filler
- Use `interface-ibo-filler` skill
- Input: `doc.md`, `implementation_atomic.json`
- Output: `implementation_atomic_ibo.json`

**Note**: Branch 1 and Branch 2 can execute in parallel

---

### Phase 3: Test Design

**Executor**: test-design-expert

#### Branch 1: Feature Atomic Capability → Logical Test Cases

##### Step 3.1.1: Logical Testpoint Generation
- Use `feature-atomic-capability-testpoint-generation` skill
- Input: `feature_atomic_ibo.json`
- Output: `logic_testpoint.json`

##### Step 3.1.2: Testpoint to Testcase
- Use `testpoint-to-testcase` skill
- Input: `logic_testpoint.json`
- Output: `logic_testcase.json`

##### Step 3.1.3: Logic Testcase to Excel
- Use `logic-testcase-to-excel` skill
- Input: `logic_testpoint.json`, `logic_testcase.json`
- Output: `logic_testcase_export.xlsx`

#### Branch 2: Implementation Atomic Capability → Interface Test Cases

##### Step 3.2.1: Interface Testpoint Generation
- Use `interface-testpoint-generation` skill
- Input: `implementation_atomic_ibo.json`, `rule_l3_atom.json`
- Output: `interface_testpoint.json`

##### Step 3.2.2: Interface Testpoint to Testcase
- Use `testpoint-to-testcase` skill
- Input: `interface_testpoint.json`
- Output: `interface_testcase.json`

##### Step 3.2.3: Interface Testcase to Excel
- Use `interface-testcase-to-excel` skill
- Input: `interface_testpoint.json`, `interface_testcase.json`
- Output: `interface_testcase_export.xlsx`

**Note**: Branch 1 and Branch 2 can execute in parallel

---

## Output Artifacts

| Artifact | Description |
|----------|-------------|
| `doc.md` | Converted design document |
| `requirements.md` | Preprocessed requirements |
| `sub_problems.json` | Customer problems (MECE decomposition) |
| `sub_problems_5w2h.json` | 5W2H analysis results |
| `sub_problems_5w2h_replaced.json` | Terms replaced (5W2H) |
| `sub_problems_replaced.json` | Terms replaced |
| `rule_l3_atom.json` | L3 atomic rules |
| `rule_l4_factor.json` | L4 factor rules |
| `rule_l3_l4_relation.json` | Rule relations |
| `usecase.json` | Usecases |
| `non_func_req.json` | Non-functional requirements |
| `usecase_with_rule.json` | Usecases with matched rules |
| `feature_atomic.json` | Feature atomic capabilities |
| `feature_atomic_ibo.json` | Feature atomic (IBO filled) |
| `implementation_atomic.json` | Implementation atomic capabilities |
| `implementation_atomic_ibo.json` | Implementation atomic (IBO filled) |
| `logic_testpoint.json` | Logical testpoints |
| `logic_testcase.json` | Logical test cases |
| `logic_testcase_export.xlsx` | Logical test cases (Excel) |
| `interface_testpoint.json` | Interface testpoints |
| `interface_testcase.json` | Interface test cases |
| `interface_testcase_export.xlsx` | Interface test cases (Excel) |

## Workflow Graph

```
Input Document (Word/MD)
         │
         ▼
    ┌─────────────────────────────────────────┐
    │          Phase 0: Initialization         │
    │  • Generate Task ID                      │
    │  • Create Task Directory                  │
    │  • Convert to doc.md                      │
    └─────────────────────────────────────────┘
         │
         ▼
    ┌─────────────────────────────────────────┐
    │     Phase 1: Requirement Analysis         │
    │         (test-req-preprocessor)           │
    │  1.1 → requirements.md                    │
    │  1.2 → sub_problems.json                  │
    │  1.3 → sub_problems_5w2h.json             │
    │  1.4 → sub_problems*_replaced.json        │
    │  1.5 → rule_l3_atom/l4/relation.json      │
    │  1.6 → usecase.json, non_func_req.json    │
    │  1.7 → usecase_with_rule.json             │
    └─────────────────────────────────────────┘
         │
         ▼
    ┌──────────────────────┐   ┌──────────────────────┐
    │ Branch 1: Feature     │   │ Branch 2: Implementation │
    │ test-req-analyst      │   │ test-req-analyst     │
    │ 2.1.1 → feature_atomic.json │  2.2.1 → implementation_atomic.json │
    │ 2.1.2 → feature_atomic_ibo.json │  2.2.2 → implementation_atomic_ibo.json │
    └──────────────────────┘   └──────────────────────┘
         │                           │
         ▼                           ▼
    ┌──────────────────────┐   ┌──────────────────────┐
    │ Branch 1: Test Design │   │ Branch 2: Test Design │
    │ test-design-expert    │   │ test-design-expert  │
    │ 3.1.1 → logic_testpoint.json │  3.2.1 → interface_testpoint.json │
    │ 3.1.2 → logic_testcase.json │  3.2.2 → interface_testcase.json │
    │ 3.1.3 → logic_testcase_export.xlsx │  3.2.3 → interface_testcase_export.xlsx │
    └──────────────────────┘   └──────────────────────┘
```

## Execution Guidelines

1. **Task Directory**: All intermediate and final artifacts MUST be stored in the task directory
2. **Parallel Execution**: Branch 1 and Branch 2 in Phase 2 and Phase 3 can be executed in parallel by different subagents
3. **Skill Loading**: Use OpenCode's native `skill` tool to load required skills when needed
4. **Subagent Dispatch**: Use OpenCode's subagent system to dispatch parallel branches