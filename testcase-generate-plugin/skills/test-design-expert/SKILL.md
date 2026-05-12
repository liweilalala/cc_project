---
name: test-design-expert
description: Test design workflow for test case generation
version: 1.0.0
trigger: test-design-expert
---

# Test Design Expert Workflow

## Phase 3: Test Design

**Executor**: test-design-expert

### Input
- `feature_atomic_ibo.json` - Feature atomic (IBO filled)
- `implementation_atomic_ibo.json` - Implementation atomic (IBO filled)
- `rule_l3_atom.json` - Atomic rules

### Branch 1: Feature Atomic Capability → Logical Test Cases

#### Step 3.1.1: Logical Testpoint Generation
- **Skill**: feature-atomic-capability-testpoint-generation
- **Input**: `feature_atomic_ibo.json`
- **Output**: `logic_testpoint.json`

#### Step 3.1.2: Testpoint to Testcase
- **Skill**: testpoint-to-testcase
- **Input**: `logic_testpoint.json`
- **Output**: `logic_testcase.json`

#### Step 3.1.3: Logic Testcase to Excel
- **Skill**: logic-testcase-to-excel
- **Input**: `logic_testpoint.json`, `logic_testcase.json`
- **Output**: `logic_testcase_export.xlsx`

### Branch 2: Implementation Atomic Capability → Interface Test Cases

#### Step 3.2.1: Interface Testpoint Generation
- **Skill**: interface-testpoint-generation
- **Input**: `implementation_atomic_ibo.json`, `rule_l3_atom.json`
- **Output**: `interface_testpoint.json`

#### Step 3.2.2: Interface Testpoint to Testcase
- **Skill**: testpoint-to-testcase
- **Input**: `interface_testpoint.json`
- **Output**: `interface_testcase.json`

#### Step 3.2.3: Interface Testcase to Excel
- **Skill**: interface-testcase-to-excel
- **Input**: `interface_testpoint.json`, `interface_testcase.json`
- **Output**: `interface_testcase_export.xlsx`

### Execution Note
Branch 1 and Branch 2 can execute in parallel.

### Output Artifacts
| Artifact | Description |
|----------|-------------|
| `logic_testpoint.json` | Logical testpoints |
| `logic_testcase.json` | Logical test cases |
| `logic_testcase_export.xlsx` | Logical test cases (Excel) |
| `interface_testpoint.json` | Interface testpoints |
| `interface_testcase.json` | Interface test cases |
| `interface_testcase_export.xlsx` | Interface test cases (Excel) |