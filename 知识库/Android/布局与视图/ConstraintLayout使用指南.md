
## 基础属性

```
<!-- 基本方向约束 -->
<!-- 我的什么位置在谁的什么位置 -->
app:layout_constraintTop_toTopOf=""           我的顶部和谁的顶部对齐
app:layout_constraintBottom_toBottomOf=""     我的底部和谁的底部对齐
app:layout_constraintLeft_toLeftOf=""         我的左边和谁的左边对齐
app:layout_constraintRight_toRightOf=""       我的右边和谁的右边对齐
app:layout_constraintStart_toStartOf=""       我的开始位置和谁的开始位置对齐
app:layout_constraintEnd_toEndOf=""           我的结束位置和谁的结束位置对齐

app:layout_constraintTop_toBottomOf=""        我的顶部位置在谁的底部位置
app:layout_constraintStart_toEndOf=""         我的开始位置在谁的结束为止
<!-- ...以此类推 -->
```

## 基线对齐

```
通过 layout_constraintBaseline_toBaselineOf 我们就可以让两个不同大小的文案基线对齐
```

## 参考资料

[万字长文 - 史上最全ConstraintLayout（约束布局）使用详解](https://juejin.cn/post/6949186887609221133)