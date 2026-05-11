# 测试用例生成主控 Skill 架构设计

## 1. 整体架构

```
test-case-generation (主控skill)
├── 任务管理模块
│   ├── 任务初始化 (创建任务ID和workspace)
│   ├── 任务状态追踪 (任务记录.json)
│   └── 断点恢复机制
│
├── 阶段1: 原始需求分析 (test-req-preprocessor)
│   ├── 步骤1.1: 需求格式化
│   ├── 步骤1.2: 客户问题识别
│   ├── 步骤1.3: 5W2H分析
│   ├── 步骤1.4: 术语替换
│   ├── 步骤1.5: 规则拆分
│   ├── 步骤1.6: usecase提取
│   └── 步骤1.7: usecase规则匹配
│
├── 阶段2: 测试需求分析 (test-requirement-analyst) [可并行]
│   ├── 分支1: 特性原子能力
│   └── 分支2: 实现原子能力
│
└── 阶段3: 测试设计 (test-design-expert) [可并行]
    ├── 分支1: 特性原子能力 → 逻辑用例
    └── 分支2: 实现原子能力 → 接口用例
```

## 2. 任务记录 Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["task_id", "created_at", "status", "phases"],
  "properties": {
    "task_id": {
      "type": "string",
      "description": "唯一任务标识符"
    },
    "created_at": {
      "type": "string",
      "format": "date-time"
    },
    "updated_at": {
      "type": "string",
      "format": "date-time"
    },
    "status": {
      "type": "string",
      "enum": ["pending", "running", "paused", "completed", "failed"],
      "description": "任务整体状态"
    },
    "input": {
      "type": "object",
      "properties": {
        "document_path": {"type": "string"},
        "document_type": {"type": "string", "enum": ["word", "markdown"]}
      }
    },
    "phases": {
      "type": "object",
      "properties": {
        "phase1": {
          "type": "object",
          "properties": {
            "status": {"type": "string"},
            "started_at": {"type": "string"},
            "completed_at": {"type": "string"},
            "steps": {
              "type": "object",
              "properties": {
                "step_1_1": {
                  "type": "object",
                  "properties": {
                    "status": {"type": "string"},
                    "output": {"type": "string"},
                    "error": {"type": "string"}
                  }
                },
                "step_1_2": {"$ref": "#/definitions/stepStatus"},
                "step_1_3": {"$ref": "#/definitions/stepStatus"},
                "step_1_4": {"$ref": "#/definitions/stepStatus"},
                "step_1_5": {"$ref": "#/definitions/stepStatus"},
                "step_1_6": {"$ref": "#/definitions/stepStatus"},
                "step_1_7": {"$ref": "#/definitions/stepStatus"}
              }
            }
          }
        },
        "phase2_branch1": {
          "type": "object",
          "properties": {
            "status": {"type": "string"},
            "steps": {
              "type": "object",
              "properties": {
                "step_2_1_1": {"$ref": "#/definitions/stepStatus"},
                "step_2_1_2": {"$ref": "#/definitions/stepStatus"}
              }
            }
          }
        },
        "phase2_branch2": {
          "type": "object",
          "properties": {
            "status": {"type": "string"},
            "steps": {
              "type": "object",
              "properties": {
                "step_2_2_1": {"$ref": "#/definitions/stepStatus"},
                "step_2_2_2": {"$ref": "#/definitions/stepStatus"}
              }
            }
          }
        },
        "phase3_branch1": {
          "type": "object",
          "properties": {
            "status": {"type": "string"},
            "steps": {
              "type": "object",
              "properties": {
                "step_3_1_1": {"$ref": "#/definitions/stepStatus"},
                "step_3_1_2": {"$ref": "#/definitions/stepStatus"},
                "step_3_1_3": {"$ref": "#/definitions/stepStatus"}
              }
            }
          }
        },
        "phase3_branch2": {
          "type": "object",
          "properties": {
            "status": {"type": "string"},
            "steps": {
              "type": "object",
              "properties": {
                "step_3_2_1": {"$ref": "#/definitions/stepStatus"},
                "step_3_2_2": {"$ref": "#/definitions/stepStatus"},
                "step_3_2_3": {"$ref": "#/definitions/stepStatus"}
              }
            }
          }
        }
      }
    },
    "outputs": {
      "type": "object",
      "properties": {
        "logic_testcase": {"type": "string"},
        "interface_testcase": {"type": "string"},
        "non_func_req": {"type": "string"}
      }
    }
  },
  "definitions": {
    "stepStatus": {
      "type": "object",
      "properties": {
        "status": {"type": "string", "enum": ["pending", "running", "completed", "failed", "skipped"]},
        "started_at": {"type": "string"},
        "completed_at": {"type": "string"},
        "output": {"type": "string"},
        "error": {"type": "string"}
      }
    }
  }
}
```

## 3. 数据Schema对齐规范

### 3.1 核心数据对象 Schema

```json
{
  "definitions": {
    "subProblem": {
      "type": "object",
      "required": ["id", "problem_statement", "category"],
      "properties": {
        "id": {"type": "string"},
        "problem_statement": {"type": "string"},
        "category": {"type": "string"},
        "parent_id": {"type": ["string", "null"]},
        "children": {"type": "array", "items": {"$ref": "#"}}
      }
    },
    "fiveW2H": {
      "type": "object",
      "required": ["what", "why", "who", "when", "where", "how", "how_much"],
      "properties": {
        "what": {"type": "string"},
        "why": {"type": "string"},
        "who": {"type": "string"},
        "when": {"type": "string"},
        "where": {"type": "string"},
        "how": {"type": "string"},
        "how_much": {"type": "string"}
      }
    },
    "ruleAtom": {
      "type": "object",
      "required": ["rule_id", "rule_level", "content"],
      "properties": {
        "rule_id": {"type": "string"},
        "rule_level": {"type": "string", "enum": ["L3", "L4"]},
        "content": {"type": "string"},
        "parent_rule_id": {"type": ["string", "null"]},
        "related_problems": {"type": "array", "items": {"type": "string"}}
      }
    },
    "usecase": {
      "type": "object",
      "required": ["usecase_id", "title", "steps", "expected_result"],
      "properties": {
        "usecase_id": {"type": "string"},
        "title": {"type": "string"},
        "steps": {"type": "array", "items": {"type": "string"}},
        "expected_result": {"type": "string"},
        "related_rules": {"type": "array", "items": {"type": "string"}},
        "is_functional": {"type": "boolean"}
      }
    },
    "featureAtomic": {
      "type": "object",
      "required": ["feature_id", "name", "capabilities"],
      "properties": {
        "feature_id": {"type": "string"},
        "name": {"type": "string"},
        "capabilities": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "capability_id": {"type": "string"},
              "name": {"type": "string"},
              "description": {"type": "string"},
              "input": {"type": "array", "items": {"type": "string"}},
              "output": {"type": "array", "items": {"type": "string"}}
            }
          }
        }
      }
    },
    "implementationAtomic": {
      "type": "object",
      "required": ["implementation_id", "name", "interfaces"],
      "properties": {
        "implementation_id": {"type": "string"},
        "name": {"type": "string"},
        "interfaces": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "interface_id": {"type": "string"},
              "name": {"type": "string"},
              "method": {"type": "string"},
              "endpoint": {"type": "string"},
              "parameters": {"type": "array"}
            }
          }
        }
      }
    },
    "testpoint": {
      "type": "object",
      "required": ["testpoint_id", "type", "content"],
      "properties": {
        "testpoint_id": {"type": "string"},
        "type": {"type": "string", "enum": ["logic", "interface"]},
        "content": {"type": "string"},
        "precondition": {"type": "string"},
        "test_data": {"type": "array"},
        "expected_result": {"type": "string"}
      }
    },
    "testcase": {
      "type": "object",
      "required": ["testcase_id", "testpoint_id", "title", "steps"],
      "properties": {
        "testcase_id": {"type": "string"},
        "testpoint_id": {"type": "string"},
        "title": {"type": "string"},
        "steps": {"type": "array", "items": {"type": "string"}},
        "test_data": {"type": "array"},
        "expected_result": {"type": "string"},
        "priority": {"type": "string", "enum": ["high", "medium", "low"]}
      }
    }
  }
}
```

## 4. Skill 调用序列

### 4.1 阶段1串行执行

```
1. word-to-markdown (如需要)
   └─ output: doc.md

2. requirement-document-preprocessor
   ├─ input: doc.md
   └─ output: requirements.md

3. mece-decomposition
   ├─ input: requirements.md, doc.md
   └─ output: sub_problems.json

4. 5w2h-analysis
   ├─ input: sub_problems.json, doc.md
   └─ output: sub_problems_5w2h.json

5. term-dictionary
   ├─ input: sub_problems_5w2h.json, sub_problems.json
   └─ output: sub_problems_5w2h_replaced.json, sub_problems_replaced.json

6. rule-split
   ├─ input: sub_problems_5w2h_replaced.json
   └─ output: rule_l3_atom.json, rule_l4_factor.json, rule_l3_l4_relation.json

7. usecase-extraction
   ├─ input: sub_problems_5w2h_replaced.json
   └─ output: usecase.json, non_func_req.json

8. usecase-rule-matcher
   ├─ input: usecase.json, rule_l3_atom.json
   └─ output: usecase_with_rule.json
```

### 4.2 阶段2并行执行

```
分支1:                          分支2:
feature-tree-atomic-extraction   implementation-atomic-capability-extractor
├─ input: doc.md                 ├─ input: doc.md
└─ output: feature_atomic.json   └─ output: implementation_atomic.json
         ↓                                   ↓
feature-atomic-ibo-filler         interface-ibo-filler
├─ input: feature_atomic.json    ├─ input: doc.md, implementation_atomic.json
├─       doc.md                   └─ output: implementation_atomic_ibo.json
├─       rule_l3_atom.json
└─ output: feature_atomic_ibo.json
```

### 4.3 阶段3并行执行

```
分支1:                          分支2:
feature-atomic-capability-       interface-testpoint-generation
testpoint-generation             ├─ input: implementation_atomic_ibo.json
├─ input: feature_atomic_ibo.json├─       rule_l3_atom.json
└─ output: logic_testpoint.json  └─ output: interface_testpoint.json
         ↓                                   ↓
testpoint-to-testcase            testpoint-to-testcase
├─ input: logic_testpoint.json   ├─ input: interface_testpoint.json
└─ output: logic_testcase.json   └─ output: interface_testcase.json
         ↓                                   ↓
logic-testcase-to-excel          interface-testcase-to-excel
├─ input: logic_testpoint.json   ├─ input: interface_testpoint.json
├─       logic_testcase.json     ├─       interface_testcase.json
└─ output: logic_testcase_export.xlsx
```

## 5. 目录结构

```
{workspace}/
└── {task_id}/
    ├── 任务记录.json          # 任务状态追踪
    ├── doc.md                # 原始文档markdown
    ├── requirements.md       # 预处理后需求
    ├── sub_problems.json     # 客户问题
    ├── sub_problems_5w2h.json
    ├── sub_problems_5w2h_replaced.json
    ├── sub_problems_replaced.json
    ├── rule_l3_atom.json
    ├── rule_l4_factor.json
    ├── rule_l3_l4_relation.json
    ├── usecase.json
    ├── usecase_with_rule.json
    ├── non_func_req.json
    ├── feature_atomic.json
    ├── feature_atomic_ibo.json
    ├── implementation_atomic.json
    ├── implementation_atomic_ibo.json
    ├── logic_testpoint.json
    ├── logic_testcase.json
    ├── logic_testcase_export.xlsx
    ├── interface_testpoint.json
    ├── interface_testcase.json
    └── interface_testcase_export.xlsx
```

## 6. 断点恢复机制

1. **状态持久化**: 每个步骤完成后立即更新 `任务记录.json`
2. **幂等设计**: 支持重新执行任何步骤，已存在的输出会被覆盖
3. **依赖检查**: 步骤执行前检查前置步骤是否完成
4. **跳过已完成**: 恢复时自动跳过已完成的步骤

## 7. 并行执行策略

- 阶段1: 必须串行执行（步骤间有依赖）
- 阶段2: 分支1和分支2可完全并行
- 阶段3: 分支1和分支2可完全并行
- 使用 Task tool 并行启动阶段2和阶段3的分支