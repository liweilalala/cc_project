# 测试规范文档

## 1. 测试分层

| 层级 | 目录 | 范围 | 最低覆盖率 |
|-----|------|-----|-----------|
| 单元测试 | `tests/unit/` | 函数/类粒度 | 80% |
| 集成测试 | `tests/integration/` | 模块间交互 | 60% |
| E2E 测试 | `tests/e2e/` | 完整用户流程 | 关键路径 100% |

## 2. 命名规范

### 2.1 文件命名

```
test_<模块名>_<场景>.py
```

### 2.2 类命名

```
Test<被测类名>
```

### 2.3 方法命名

```
test_<操作>_<预期结果>
```

## 3. 单元测试规则

### 3.1 必须覆盖的场景

- 正常路径
- 边界条件（空值、零值、最大值）
- 异常路径（无效输入）
- 异步操作（如适用）

### 3.2 Mock 使用原则

- 外部依赖必须 Mock（数据库、API、文件系统）
- 被测模块内部逻辑不 Mock
- 使用 `pytest-mock` 或 `unittest.mock`

### 3.3 Fixture 规范

```python
@pytest.fixture
def <fixture_name>():
    """返回测试所需的数据或对象"""
    return <value>

@pytest.fixture(scope="class")
def <shared_fixture>():
    """类级别共享的 fixture"""
    return <value>
```

## 4. 集成测试规则

### 4.1 环境管理

- 使用 Docker Compose 管理外部依赖
- 测试结束后清理数据
- 使用独立测试数据库

### 4.2 Fixture 作用域

```python
@pytest.fixture(scope="class")
def db_engine():
    """类级别复用数据库引擎"""
    ...

@pytest.fixture
def db_session(db_engine):
    """每个测试独立会话，自动回滚"""
    ...
```

## 5. E2E 测试规则

### 5.1 Page Object 模式

- 每个页面/组件对应一个 Page Object 类
- Page Object 封装元素定位器和操作
- 测试代码只调用 Page Object 方法

### 5.2 元素定位优先级

1. `data-testid` 属性
2. CSS 选择器
3. XPath（最后使用）

### 5.3 等待策略

- 使用显式等待，避免 `time.sleep()`
- 页面导航后等待 DOM 就绪

## 6. 测试标记（Markers）

| 标记 | 用途 | 运行方式 |
|-----|------|---------|
| `@pytest.mark.unit` | 单元测试 | `pytest -m unit` |
| `@pytest.mark.integration` | 集成测试 | `pytest -m integration` |
| `@pytest.mark.e2e` | 端到端测试 | `pytest -m e2e` |
| `@pytest.mark.slow` | 耗时测试 | `pytest -m "not slow"` 跳过 |

## 7. 参数化测试

```python
@pytest.mark.parametrize("input,expected", [
    (1, 2),
    (2, 4),
    (3, 6),
])
def test_double(input, expected):
    assert input * 2 == expected
```

## 8. 覆盖率要求

```bash
# 生成覆盖率报告
pytest --cov=src --cov-report=html --cov-report=term-missing

# 合并覆盖率
pytest --cov=src --cov-report=xml
```

## 9. 测试数据管理

- 测试数据放在 `tests/fixtures/` 目录
- JSON/YAML 文件存储结构化数据
- 使用 Factory 模式生成动态数据

## 10. CI/CD 要求

- 所有测试必须通过才能合并
- 覆盖率下降超过 5% 阻塞合并
- E2E 测试在独立 Job 运行
