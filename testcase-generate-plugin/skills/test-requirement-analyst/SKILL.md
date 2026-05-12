---
name: test-requirement-analyst
description: Test requirement analysis workflow for test case generation
version: 1.0.0
trigger: test-requirement-analyst
---

# Test Requirement Analyst Workflow

## Phase 2: Test Requirement Analysis

**Executor**: test-requirement-analyst

### Input
- `doc.md` - Design document
- `rule_l3_atom.json` - Atomic rules from Phase 1

### Branch 1: Feature Atomic Capability Extraction

#### Step 2.1.1: Feature Tree Atomic Extraction
- **Skill**: feature-tree-atomic-extraction
- **Input**: `doc.md`
- **Output**: `feature_atomic.json`

#### Step 2.1.2: Feature Atomic IBO Filler
- **Skill**: feature-atomic-ibo-filler
- **Input**: `feature_atomic.json`, `doc.md`, `rule_l3_atom.json`
- **Output**: `feature_atomic_ibo.json`

### Branch 2: Implementation Atomic Capability Extraction

#### Step 2.2.1: Implementation Atomic Capability Extractor
- **Skill**: implementation-atomic-capability-extractor
- **Input**: `doc.md`
- **Output**: `implementation_atomic.json`

#### Step 2.2.2: Interface IBO Filler
- **Skill**: interface-ibo-filler
- **Input**: `doc.md`, `implementation_atomic.json`
- **Output**: `implementation_atomic_ibo.json`

### Execution Note
Branch 1 and Branch 2 can execute in parallel.

### Output Artifacts
| Artifact | Description |
|----------|-------------|
| `feature_atomic.json` | Feature atomic capabilities |
| `feature_atomic_ibo.json` | Feature atomic (IBO filled) |
| `implementation_atomic.json` | Implementation atomic capabilities |
| `implementation_atomic_ibo.json` | Implementation atomic (IBO filled) |