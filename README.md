# SkillWheel

`SkillWheel` 是一个基于 Minecraft Forge 1.20.1 的技能轮盘模组，主要用于把玩家身上的指定物品汇总成一个径向菜单，并将选择结果回传给 KubeJS 处理。

它更像一个“前端选择器”：

- 负责扫描玩家身上的技能物品
- 负责显示轮盘和 HUD
- 负责把选中的物品信息发送给 KubeJS

真正的技能逻辑通常由 KubeJS 脚本继续处理。

## 环境

- Minecraft: `1.20.1`
- Forge: `47.4.6`
- Java: `17`
- Mod ID: `skillwheel`

## 依赖关系

### 必需依赖

- `KubeJS`

`mods.toml` 中已将 `kubejs` 声明为必需依赖，因此没有 KubeJS 时本模组不能正常作为完整方案使用。

### 可选联动

- `Curios`

安装 Curios 后，技能轮会额外扫描 Curios 饰品栏中的技能物品；未安装时只扫描原版栏位。

## 核心功能

### 1. 技能轮盘

客户端按下默认按键 `R` 后，会打开一个圆形技能轮盘界面。

技能轮会从以下位置收集物品：

- 主手
- 副手
- 护甲栏
- Curios 栏位（仅在安装 Curios 时）

只有带有 `skillwheel:skills` 标签的物品，才会进入轮盘可选列表。

### 2. HUD 快捷显示

符合条件的物品还可以显示在屏幕左下角 HUD 中。

HUD 显示条件：

- 配置项 `hudEnabled = true`
- 当前没有打开其他界面
- 玩家处于第一人称视角
- 物品同时命中 `skillwheel:skills` 和 `skillwheel:skills_ui`

HUD 会额外显示该物品当前的冷却遮罩。

### 3. 选择结果回传给 KubeJS

玩家在轮盘里点击物品后，模组不会自己施放技能，而是把选择结果通过 `kjs$sendData("skillwheel", data)` 发给 KubeJS。

因此常见用法是：

- 物品负责作为“技能入口”
- SkillWheel 负责选择
- KubeJS 根据返回数据执行真正的技能逻辑

## 玩家操作方式

### 打开轮盘

- 默认按键：`R`
- 再按一次同样按键可关闭轮盘

### 轮盘操作

- 左键：选择当前高亮物品
- 右键：切换下一页

轮盘每页最多显示 `6` 个物品。

### 子菜单操作

如果物品被定义为子菜单物品，则：

- 左键进入子菜单
- 子菜单内左键选择具体选项
- 子菜单内右键返回上一级

子菜单最多支持 `4` 个方向选项。

## 物品标签

本项目当前识别以下标签：

- `skillwheel:skills`
- `skillwheel:skills_ui`
- `skillwheel:submenu`

其中前两个已经在资源目录中给了示例标签文件，`submenu` 由代码支持，但需要你自己补充数据标签文件。

### 1. `skillwheel:skills`

表示该物品可以进入技能轮盘。

示例路径：

```text
data/skillwheel/tags/items/skills.json
```

示例内容：

```json
{
  "replace": false,
  "values": [
    "yourmod:skill_item"
  ]
}
```

### 2. `skillwheel:skills_ui`

表示该物品允许显示在左下角 HUD 中。

注意：

- 只有同时命中 `skillwheel:skills` 和 `skillwheel:skills_ui` 的物品，才会出现在 HUD
- 只进轮盘、不进 HUD 的物品，可以只加 `skillwheel:skills`

示例路径：

```text
data/skillwheel/tags/items/skills_ui.json
```

示例内容：

```json
{
  "replace": false,
  "values": [
    "yourmod:skill_item"
  ]
}
```

### 3. `skillwheel:submenu`

表示该物品是一个“子菜单入口”。

这个标签的作用不是直接施放技能，而是在点击时先展开 4 向子菜单。

建议自行创建：

```text
data/skillwheel/tags/items/submenu.json
```

示例内容：

```json
{
  "replace": false,
  "values": [
    "yourmod:stance_book"
  ]
}
```

## 子菜单 NBT 格式

带有 `skillwheel:submenu` 标签的物品，还需要携带一个 `submenu` 的 `CompoundTag`，键名必须是 `1` 到 `4` 的字符串。

示例：

```nbt
{submenu:{1:"火",2:"水",3:"风",4:"雷"}}
```

或写成命令思路：

```mcfunction
give @p yourmod:stance_book{submenu:{1:"火",2:"水",3:"风",4:"雷"}}
```

规则如下：

- 只有键 `1` 到 `4` 会被识别
- 至少存在一个合法选项时，子菜单才能打开
- 如果物品带了 `skillwheel:submenu` 标签但没有合法 `submenu` NBT，客户端日志会报错

## KubeJS 接收数据格式

当玩家在轮盘中完成选择时，模组会向 `skillwheel` 频道发送一个 `CompoundTag`。

基础字段如下：

- `item`: 被选中物品的完整 `ItemStack` NBT
- `sourceType`: 来源类型
- `slotIndex`: 来源槽位索引
- `slotName`: Curios 槽位名，仅 Curios 来源时可能存在
- `isSubmenu`: 是否来自子菜单
- `submenuIndex`: 子菜单项索引，仅子菜单选择时存在

### `sourceType` 可能值

- `vanilla_mainhand`
- `vanilla_offhand`
- `vanilla_armor`
- `curios`

### 数据含义

- 普通轮盘点击时：`isSubmenu = false`
- 子菜单点击时：`isSubmenu = true`，并附带 `submenuIndex`

## KubeJS 使用思路

推荐把 SkillWheel 当成一个选择输入层，然后在 KubeJS 中根据物品、来源和子菜单索引做分发。

常见逻辑包括：

- 不同技能书触发不同法术
- 同一技能书根据 `submenuIndex` 触发 4 种技能分支
- 根据 `sourceType` 区分主手技能、副手技能、饰品技能
- 根据物品冷却、玩家状态、维度或职业做进一步限制

如果你已经在项目里扩展了客户端到 KubeJS 的数据回传协议，也可以在脚本中继续基于这些字段做更细分的处理。<mccoremem id="03g0yq47r2mz0vy2d9wgici4w" />

## 常见接入方案

### 方案 1：技能物品只进轮盘

- 给物品加 `skillwheel:skills`
- 不加 `skillwheel:skills_ui`

适合不希望常驻 HUD 的技能书、法器、卷轴。

### 方案 2：技能物品同时进轮盘和 HUD

- 给物品同时加 `skillwheel:skills`
- 给物品同时加 `skillwheel:skills_ui`

适合需要持续观察冷却状态的技能物品。

### 方案 3：一个物品展开多分支技能

- 给物品加 `skillwheel:skills`
- 给物品加 `skillwheel:submenu`
- 写入 `submenu` NBT

适合姿态切换、元素切换、技能分页、武器模式切换等玩法。

## 配置项

本模组当前只有一个公共配置项：

- `hudEnabled`

作用：

- `true`：启用左下角技能 HUD
- `false`：关闭左下角技能 HUD

这是 Forge `COMMON` 配置，实际文件通常位于运行目录的配置文件夹中。

## 实现概要

项目当前主要由以下部分组成：

- `KeyBindings`：注册默认按键 `R`
- `ClientInit`：监听按键并打开/关闭轮盘界面
- `RadialMenuScreen`：负责渲染轮盘、多页切换、子菜单和点击选择
- `HudRenderer`：渲染左下角技能图标与冷却遮罩
- `ItemSources`：从玩家主手、副手、护甲和 Curios 中收集可选物品
- `Network`：把选择结果通过 `kjs$sendData("skillwheel", data)` 发给 KubeJS

## 开发与运行

### 运行客户端

```powershell
.\gradlew runClient
```

### 运行服务端

```powershell
.\gradlew runServer
```

### 构建 Jar

```powershell
.\gradlew build
```

构建产物通常位于：

```text
build/libs/
```

## 注意事项

- 没有 `skillwheel:skills` 标签的物品，不会出现在轮盘里
- HUD 物品必须同时命中 `skillwheel:skills` 和 `skillwheel:skills_ui`
- 轮盘每页最多显示 `6` 个物品，超过后需右键翻页
- 子菜单最多支持 `4` 个选项
- Curios 来源需要安装 Curios 才会被扫描
- 本模组本身不执行技能效果，核心逻辑要由 KubeJS 或其他脚本继续处理

## License

项目配置中声明的许可证为 `All Rights Reserved`。若后续仓库补充更明确的授权文件，请以实际授权文件为准。
