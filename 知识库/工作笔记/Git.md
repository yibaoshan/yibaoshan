
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

## Stash

- 保存当前进度：git stash
- 保存进度并添加注释：git stash save 'message...'
- 显示保存进度的列表：git stash list
- 恢复：
  - git stash pop：恢复并删除最新保存的进度
  - git stash apply：恢复进度，但不删除
- 删除：
  - git stash drop stash_id：删除一个进度存储，若不指定 stash_id ，则删除最新
  - git stash clear：删除所有