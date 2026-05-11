# 测试用例生成流程

## 流程概览

```mermaid
flowchart TB
    subgraph Phase1["阶段1: 原始需求分析<br/>(test-req-preprocessor)"]
        direction TB
        A1["📄 设计文档<br/>(Word/Markdown)"]

        subgraph Step1_1["1.1 需求格式化"]
            A1_1{"文档类型?"}
            A1_2["word-to-markdown<br/>→ doc.md"]
            A1_3["直接作为<br/>doc.md"]
            A1_4["requirement-document-preprocessor<br/>→ requirements.md"]
        end

        subgraph Step1_2["1.2 客户问题识别"]
            A2["requirement-analysis-decomposition<br/>→ sub_problems_5w2h.json<br/>→ sub_problems.json"]
        end

        subgraph Step1_3["1.3 术语替换"]
            A3["term-dictionary<br/>→ sub_problems_5w2h_replaced.json<br/>→ sub_problems_replaced.json"]
        end

        subgraph Step1_4["1.4 规则拆分"]
            A4["rule-split<br/>→ l3_rules.json<br/>→ l4_rules.json"]
        end

        subgraph Step1_5["1.5 Usecase 提取"]
            A5["usecase-extraction<br/>→ usecase.json<br/>→ non_func_req.json"]
        end

        subgraph Step1_6["1.6 Usecase 规则匹配"]
            A6["usecase-rule-matcher<br/>→ usecase_with_rule.json"]
        end
    end

    subgraph Phase2["阶段2: 测试需求分析<br/>(test-requirement-analyst)"]
        direction TB

        subgraph Branch1["分支1: 提取特性原子能力"]
            B1_1["feature-tree-atomic-extraction<br/>→ feature_atomic.json"]
            B1_2["feature-atomic-ibo-filler<br/>+ doc.md + l3_rules.json<br/>→ feature_atomic_ibo.json"]
        end

        subgraph Branch2["分支2: 提取实现原子能力"]
            B2_1["implementation-atomic-capability-extractor<br/>→ implementation_atomic.json"]
            B2_2["interface-ibo-filler<br/>+ doc.md<br/>→ implementation_atomic_ibo.json"]
        end
    end

    subgraph Phase3["阶段3: 测试设计<br/>(test-design-expert)"]
        direction TB

        subgraph Branch3["分支1: 特性原子能力 → 逻辑用例"]
            C1_1["feature-atomic-capability-testpoint-generation<br/>+ atomic-capability.json<br/>→ logic_testpoint.json"]
            C1_2["testpoint-to-testcase<br/>→ logic_testcase.json"]
        end

        subgraph Branch4["分支2: 实现原子能力 → 接口用例"]
            C2_1["interface-testpoint-generation<br/>+ implementation_atomic_ibo.json + l3_rules.json<br/>→ interface_testpoint.json"]
            C2_2["testpoint-to-testcase<br/>→ interface_testcase.json"]
        end
    end

    subgraph Outputs["最终输出"]
        O1["logic_testcase.json<br/>逻辑测试用例"]
        O2["interface_testcase.json<br/>接口测试用例"]
        O3["non_func_req.json<br/>非功能需求"]
    end

    %% 阶段1内部流程
    A1 --> A1_1
    A1_1 -->|"Word"| A1_2
    A1_1 -->|"Markdown"| A1_3
    A1_2 --> A1_4
    A1_3 --> A1_4
    A1_4 --> A2 --> A3 --> A4 --> A5 --> A6

    %% 阶段1到阶段2
    A1_4 -.-> B1_1
    A1_4 -.-> B1_1
    A4 -.-> B1_2
    A4 -.-> B2_1
    A1_4 -.-> B2_1

    %% 阶段2并行执行
    B1_1 --> B1_2
    B2_1 --> B2_2

    %% 阶段2到阶段3
    B1_2 -.-> C1_1
    B2_2 -.-> C2_1
    A4 -.-> C2_1

    %% 阶段3并行执行
    C1_1 --> C1_2
    C2_1 --> C2_2

    %% 最终输出
    C1_2 --> O1
    C2_2 --> O2
    A5 -.-> O3
```

## 阶段说明

### 阶段1: 原始需求分析

**执行者:** `test-req-preprocessor`

| 步骤 | Skill | 输入 | 输出 |
|-----|-------|------|------|
| 1.1 | word-to-markdown + requirement-document-preprocessor | 设计文档 | requirements.md |
| 1.2 | requirement-analysis-decomposition | requirements.md | sub_problems_5w2h.json, sub_problems.json |
| 1.3 | term-dictionary | sub_problems_5w2h.json, sub_problems.json | sub_problems_5w2h_replaced.json, sub_problems_replaced.json |
| 1.4 | rule-split | sub_problems_5w2h_replaced.json | l3_rules.json, l4_rules.json |
| 1.5 | usecase-extraction | sub_problems_5w2h_replaced.json | usecase.json, non_func_req.json |
| 1.6 | usecase-rule-matcher | usecase.json, rules.json | usecase_with_rule.json |

### 阶段2: 测试需求分析

**执行者:** `test-requirement-analyst`

| 分支 | Skill | 输入 | 输出 |
|-----|-------|------|------|
| 分支1.1 | feature-tree-atomic-extraction | doc.md | feature_atomic.json |
| 分支1.2 | feature-atomic-ibo-filler | feature_atomic.json, doc.md, l3_rules.json | feature_atomic_ibo.json |
| 分支2.1 | implementation-atomic-capability-extractor | doc.md | implementation_atomic.json |
| 分支2.2 | interface-ibo-filler | doc.md, implementation_atomic.json | implementation_atomic_ibo.json |

**注意:** 分支1和分支2可同时执行

### 阶段3: 测试设计

**执行者:** `test-design-expert`

| 分支 | Skill | 输入 | 输出 |
|-----|-------|------|------|
| 分支1.1 | feature-atomic-capability-testpoint-generation | atomic-capability.json | logic_testpoint.json |
| 分支1.2 | testpoint-to-testcase | logic_testpoint.json | logic_testcase.json |
| 分支2.1 | interface-testpoint-generation | implementation_atomic_ibo.json, l3_rules.json | interface_testpoint.json |
| 分支2.2 | testpoint-to-testcase | interface_testpoint.json | interface_testcase.json |

**注意:** 分支1和分支2可同时执行

## 最终输出产物

| 文件 | 说明 |
|------|------|
| logic_testcase.json | 基于特性原子能力生成的逻辑测试用例 |
| interface_testcase.json | 基于实现原子能力生成的接口测试用例 |
| non_func_req.json | 从需求中提取的非功能性需求 |
