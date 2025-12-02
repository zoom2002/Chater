# 后期代码优化思路

## 1. 架构与模式优化

### 当前问题
*   **MVC 耦合严重**：`Activity` 同时承担了 UI 逻辑、业务逻辑（如发送消息流程）和数据访问入口（持有 `MessageDao`），导致代码臃肿（God Activity），难以测试和维护。
*   **主线程数据库操作**：目前所有的数据库读写（`messageDao.getConversations` 等）均在主线程（UI 线程）直接执行。虽然数据量小时感知不明显，但数据量一大必然导致 ANR。

### 优化方案
*   **引入 MVVM 架构**：
    *   **View 层** (`Activity`/`Fragment`)：只负责 UI 展示和用户交互监听。
    *   **ViewModel 层**：持有 LiveData/StateFlow，负责业务逻辑处理，不持有 Android Context。
    *   **Repository 层**：作为单一数据源，统一管理本地数据库和（未来的）网络请求。
*   **异步处理**：
    *   使用 **RxJava** 将数据库 I/O 操作切换到 IO 线程执行。
    *   使用 `LiveData` 或 `Flow` 观察数据库变化，实现“数据驱动 UI”，而不是每次手动调用 `loadConversations()`。

## 2. 数据库与存储优化 

### 当前问题
*   **原生 SQLite 繁琐**：使用 `SQLiteOpenHelper` 和 `Cursor` 需要编写大量样板代码，且容易出错（如列名拼写错误、忘记关闭 Cursor）。
*   **无缓存机制**：每次进入界面都重新查询数据库。

### 优化方案
*   **迁移至 Room 数据库**：
    *   Google 官方推荐的 ORM 库，提供编译时 SQL 检查。
    *   支持直接返回 `LiveData<List<Message>>` 或 `Flow`，简化数据观察。
*   **分页加载 (Paging)**：
    *   聊天记录可能成千上万条，一次性加载所有 `List<Message>` 会占用大量内存。
    *   引入 **Jetpack Paging 3** 库，实现聊天记录的分页懒加载（滑动到顶部时加载旧消息）。

## 3. UI 与交互体验优化 

### 当前问题
*   **手动布局计算**：`ChatActivity` 中手动监听 `OnGlobalLayout` 来计算键盘高度，但兼容性可能随 Android 版本变化而受影响。
*   **刷新效率低**：`RecyclerView` 使用 `notifyDataSetChanged()` 全量刷新，性能较差且没有动画。

### 优化方案
*   **WindowInsetsController**：使用 Android 11+ 的 `WindowInsets` API 来更优雅、标准地监听和控制键盘动画，实现键盘与输入框的“丝滑”同步位移。
*   **DiffUtil**：在 Adapter 中使用 `DiffUtil` 或 `ListAdapter`，只更新变化的数据项，提升列表渲染性能并获得免费的增删动画。
