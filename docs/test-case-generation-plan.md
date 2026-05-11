# 测试用例生成方案

## 1. 概述

本方案为 Python 项目提供一套完整的测试用例生成方法论，覆盖单元测试、集成测试和端到端测试三个层次。

## 2. 技术栈

| 测试类型 | 工具 | 说明 |
|---------|------|------|
| 单元测试 | pytest + pytest-cov | 轻量级、易扩展、覆盖率报告 |
| 集成测试 | pytest + pytest-docker | 容器化依赖服务 |
| 端到端测试 | Playwright | 现代化浏览器自动化 |
| Mock/Stub | pytest-mock, unittest.mock | 隔离外部依赖 |
| Fixture | pytest fixtures | 测试数据与状态管理 |

## 3. 测试目录结构

```
tests/
├── unit/                 # 单元测试
│   ├── __init__.py
│   ├── conftest.py       # 共享 fixtures
│   ├── test_module_a.py
│   └── test_module_b.py
├── integration/          # 集成测试
│   ├── __init__.py
│   ├── conftest.py
│   ├── test_api/
│   └── test_database/
├── e2e/                 # 端到端测试
│   ├── __init__.py
│   ├── pages/           # Page Objects
│   ├── tests/
│   └── playwright.config.ts
└── conftest.py          # 根级共享配置
```

## 4. 测试用例生成策略

### 4.1 单元测试

**原则：** 每个 Python 模块/函数对应一个 `test_*.py` 文件

**用例设计模式：**
```
test_<模块名>_<场景>.py
```

**示例：**

```python
# tests/unit/test_user_service.py
import pytest
from src.services.user_service import UserService

class TestUserService:
    """用户服务单元测试"""

    @pytest.fixture
    def user_service(self):
        return UserService()

    @pytest.fixture
    def sample_user(self):
        return {"id": 1, "name": "张三", "email": "zhangsan@example.com"}

    def test_create_user_success(self, user_service, sample_user):
        """创建用户成功场景"""
        result = user_service.create(sample_user)
        assert result["id"] == 1
        assert result["name"] == "张三"

    def test_create_user_invalid_email(self, user_service):
        """无效邮箱场景"""
        with pytest.raises(ValueError, match="Invalid email"):
            user_service.create({"email": "invalid-email"})

    @pytest.mark.parametrize("name,email,expected", [
        ("张三", "zhangsan@example.com", True),
        ("李四", "lisi@example.com", True),
        ("", "test@example.com", False),
        ("王五", "invalid-email", False),
    ])
    def test_validate_user_input(self, name, email, expected):
        """参数化测试：用户输入验证"""
        # ...
```

### 4.2 集成测试

**原则：** 测试模块间交互，使用真实或容器化的依赖

**用例设计模式：**
```
test_<功能域>_<交互场景>.py
```

**Docker Compose 支持：**

```yaml
# docker-compose.test.yml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: test_db
      POSTGRES_USER: test_user
      POSTGRES_PASSWORD: test_pass
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"
```

**示例：**

```python
# tests/integration/test_database/test_user_repository.py
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

class TestUserRepository:
    """用户仓库集成测试"""

    @pytest.fixture(scope="class")
    def db_engine(self):
        engine = create_engine("postgresql://test_user:test_pass@localhost:5432/test_db")
        yield engine
        engine.dispose()

    @pytest.fixture
    def db_session(self, db_engine):
        Session = sessionmaker(bind=db_engine)
        session = Session()
        yield session
        session.rollback()
        session.close()

    def test_save_and_retrieve_user(self, db_session):
        """保存并检索用户"""
        user = User(name="张三", email="zhangsan@example.com")
        db_session.add(user)
        db_session.commit()

        retrieved = db_session.query(User).filter_by(email="zhangsan@example.com").first()
        assert retrieved is not None
        assert retrieved.name == "张三"
```

### 4.3 端到端测试

**原则：** 模拟真实用户操作，使用 Page Object 模式

**用例设计模式：**
```
test_<页面/功能>_<操作>.py
```

**Page Object 示例：**

```python
# tests/e2e/pages/login_page.py
from playwright.sync_api import Page

class LoginPage:
    def __init__(self, page: Page):
        self.page = page

    def navigate(self):
        self.page.goto("/login")
        return self

    def login(self, username: str, password: str):
        self.page.fill("#username", username)
        self.page.fill("#password", password)
        self.page.click("#submit")
        return self

    def get_error_message(self) -> str:
        return self.page.text_content(".error-message")
```

**E2E 测试示例：**

```python
# tests/e2e/tests/test_auth_flow.py
import pytest
from playwright.sync_api import Page
from tests.e2e.pages.login_page import LoginPage
from tests.e2e.pages.dashboard_page import DashboardPage

class TestAuthFlow:
    """认证流程端到端测试"""

    def test_successful_login(self, page: Page):
        """登录成功流程"""
        login_page = LoginPage(page)
        login_page.navigate().login("admin", "password123")

        dashboard = DashboardPage(page)
        assert dashboard.is_on_page()
        assert dashboard.get_welcome_message() == "欢迎, admin"

    def test_failed_login_shows_error(self, page: Page):
        """登录失败显示错误"""
        login_page = LoginPage(page)
        login_page.navigate().login("admin", "wrong_password")

        assert login_page.get_error_message() == "用户名或密码错误"
```

## 5. 测试用例管理

### 5.1 标记（Markers）

```python
# conftest.py
def pytest_configure(config):
    config.addinivalue_line("markers", "slow: marks tests as slow")
    config.addinivalue_line("markers", "integration: marks tests as integration tests")
    config.addinivalue_line("markers", "e2e: marks tests as end-to-end tests")
```

使用示例：
```python
@pytest.mark.integration
def test_database_connection():
    pass

@pytest.mark.e2e
def test_complete_user_journey():
    pass
```

### 5.2 运行命令

```bash
# 运行所有测试
pytest

# 运行单元测试
pytest tests/unit/

# 运行集成测试
pytest tests/integration/ -m integration

# 运行 E2E 测试
pytest tests/e2e/ -m e2e

# 跳过慢速测试
pytest -m "not slow"

# 生成覆盖率报告
pytest --cov=src --cov-report=html
```

## 6. CI/CD 集成

```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  unit-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: '3.11'
      - run: pip install pytest pytest-cov
      - run: pytest tests/unit/ --cov=src --cov-report=xml

  integration-test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: test_db
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_pass
        ports:
          - 5432:5432
    steps:
      - uses: actions/checkout@v4
      - run: pip install pytest pytest-docker
      - run: pytest tests/integration/

  e2e-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: npm install
      - run: npx playwright install --with-deps
      - run: pytest tests/e2e/
```

## 7. 测试覆盖率目标

| 测试类型 | 最低覆盖率目标 |
|---------|--------------|
| 单元测试 | 80% |
| 集成测试 | 60% |
| E2E 测试 | 关键用户流程 100% |

## 8. 下一步

1. 在项目中创建上述目录结构
2. 安装依赖：`pip install pytest pytest-cov pytest-mock playwright`
3. 编写第一个单元测试
4. 配置 CI/CD 流程
