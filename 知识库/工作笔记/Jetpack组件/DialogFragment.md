
```kotlin
open class BaseDialogFragment : DialogFragment() {
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return WeakDialog(requireContext(), theme)
    }
    
}
open class WeakDialog : Dialog {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}
    protected constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {}

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        super.setOnCancelListener(Weak.proxy(listener))
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(Weak.proxy(listener))
    }

    override fun setOnShowListener(listener: OnShowListener?) {
        super.setOnShowListener(Weak.proxy(listener))
    }

    object Weak {

        fun proxy(real: DialogInterface.OnCancelListener?): WeakOnCancelListener {
            return WeakOnCancelListener(real)
        }

        fun proxy(real: DialogInterface.OnDismissListener?): WeakOnDismissListener {
            return WeakOnDismissListener(real)
        }

        fun proxy(real: OnShowListener?): WeakOnShowListener {
            return WeakOnShowListener(real)
        }
    }

    class WeakOnCancelListener(real: DialogInterface.OnCancelListener?) : DialogInterface.OnCancelListener {

        private val mRef: WeakReference<DialogInterface.OnCancelListener?>

        init {
            mRef = WeakReference(real)
        }

        override fun onCancel(dialog: DialogInterface) {
            val real = mRef.get()
            real?.onCancel(dialog)
        }
    }

    class WeakOnDismissListener(real: DialogInterface.OnDismissListener?) : DialogInterface.OnDismissListener {

        private val mRef: WeakReference<DialogInterface.OnDismissListener?>

        init {
            mRef = WeakReference(real)
        }

        override fun onDismiss(dialog: DialogInterface) {
            val real = mRef.get()
            real?.onDismiss(dialog)
        }
    }

    class WeakOnShowListener(real: OnShowListener?) : OnShowListener {

        private val mRef: WeakReference<OnShowListener?>

        init {
            mRef = WeakReference(real)
        }

        override fun onShow(dialog: DialogInterface) {
            val real = mRef.get()
            real?.onShow(dialog)
        }
    }
}
```

## 参考资料

- [反射解决DialogFragment内存泄露](https://juejin.cn/post/7012569192251523080)
- [Message 引发的 DialogFragment 内存泄漏分析与解决方案](https://juejin.cn/post/6844904191912050702)
- [DialogFragment内存泄漏原理及解决方案](https://medium.com/@wanxiao1994/dialogfragment%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E5%8E%9F%E7%90%86%E5%8F%8A%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88-fd5ba6d1595b)