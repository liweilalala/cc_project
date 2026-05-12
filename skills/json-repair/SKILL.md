---
name: json-repair
description: Use when JSON parse fails due to format errors - automatically repairs malformed JSON using json_repair library with fallback strategies
---

# JSON Repair Skill

## Overview

When standard `json.loads()` fails due to format errors in LLM output, automatically repair the JSON using the `json_repair` library.

## Core Strategy

```python
import json
from json_repair import loads as repair_loads

def safe_parse_json(json_str):
    try:
        return json.loads(json_str)
    except json.JSONDecodeError:
        print("标准解析失败，正在尝试自动修复...")
        return repair_loads(json_str, ensure_ascii=False)
```

## When Triggered

**Automatically when:**
- Reading a `.json` file and `json.JSONDecodeError` occurs
- LLM output contains format errors (trailing commas, unclosed brackets, etc.)

## Workflow

1. **Capture** the raw JSON string that failed to parse
2. **Attempt** standard `json.loads()` first (fastest path)
3. **On failure**, invoke `repair_loads()` from `json_repair` library
4. **Report** the fix applied and return repaired object

## Common LLM JSON Errors

| Error Type | Example | Fix Strategy |
|------------|---------|--------------|
| Trailing comma | `{"a": 1,}` | Remove trailing comma |
| Unclosed bracket | `{"a": [1, 2}` | Close bracket |
| Single quotes | `{'a': 1}` | Replace with double quotes |
| Comments | `{"a": 1} // comment` | Strip comments |
| Chinese/unicode | `{"name": "你好"}` | `ensure_ascii=False` |
| Markdown wrapper | ```json\n{...}\n``` | Strip code fences |

## Repair Process

```
1. Strip markdown code fences (```json ... ```)
2. Attempt repair_loads(..., ensure_ascii=False)
3. If still fails, provide detailed error location
4. Return repaired JSON object
```

## Reporting

After successful repair, report:
- What error was detected
- What fix was applied
- Confirm valid JSON structure achieved