
## 提交规范

- feat - 新功能 feature
- fix - 修复 bug
- docs - 文档注释
- style - 代码格式(不影响代码运行的变动)
- refactor - 重构、优化(既不增加新功能，也不是修复bug)
- perf - 性能优化
- test - 增加测试
- chore - 构建过程或辅助工具的变动
- revert - 回退
- build - 打包

## Tag

- 查看已有 tag 列表：git tag
- 查看 tag 列表并过滤：git tag -l xxx
- 新建 tag ：git tag xxx
- 查看 tag 详细信息：git show tagName
- 切换到 tag：git checkout tagName
- 切换 tag 并创建分支：git checkout -b branchName tagName
- 删除 tag ：git tag -d tagName