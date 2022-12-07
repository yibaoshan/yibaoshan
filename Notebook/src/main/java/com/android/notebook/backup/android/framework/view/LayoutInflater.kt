package com.android.notebook.backup.android.framework.view

class LayoutInflater {

    /**
     * LayoutInflater布局填充器
     * PhoneWindow.setContentView()
     *
     * LayoutInflater创建流程：
     * 1. 创建Activity
     * 2. 调用Activity.attach方法
     * 3. attach方法中实例化LayoutInflater
     *
     * xml解析流程：
     * 1. 首先根据布局文件，生成对应布局的XmlPullParser解析器对象；
     * 2. 对于单个View的解析而言，一个View的实例化依赖Context上下文对象和attr的属性集，而设计者正是通过将上下文对象和属性集作为参数，通过 反射 注入到View的构造器中对单个View进行创建；
     * 3. 对于整个xml文件的解析而言，整个流程依然通过典型的递归思想，对布局文件中的xml文件进行遍历解析，自底至顶对View依次进行创建，最终完成了整个View树的创建。
     *
     * 拦截机制和解耦策略：
     * 考虑到 性能优化 和 可扩展性，设计者为LayoutInflater设计了一个LayoutInflater.Factory接口
     * 该接口设计得非常巧妙：在xml解析过程中，开发者可以通过配置该接口对View的创建过程进行拦截：通过new的方式创建控件以避免大量地使用反射
     * 亦或者 额外配置特殊标签的解析逻辑以创建特殊组件
     *
     * 注意点：
     * 1. 无论是哪种方式获取到的LayoutInflater,都是通过ContextImpl.getSystemService()获取的，并且在Activity等组件的生命周期内保持单例；
     * 2. 即使是Activity.setContentView()函数,本质上也还是通过LayoutInflater.inflate()函数对布局进行解析和创建。
     * 3. 拦截机制setFactory可以用来做换肤
     * */

    /*
    public final class ActivityThread {

        // 每当`Activity`被创建
        private Activity performLaunchActivity() {
            // ....
            // 3.将 ContextImpl 也注入到 activity中
            activity.attach(appContext, ....);
            // ....
        }
    }

    public class Activity extends ContextThemeWrapper {

        final void attach(Context context, ...) {
            // ...
            // 初始化 PhoneWindow
            // window构造方法中又通过 Context 实例化了 LayoutInflater
            PhoneWindow mWindow = new PhoneWindow(this, ....);
        }
    }

    //第一个参数代表所要加载的布局
    //第二个参数是ViewGroup，这个参数需要与第3个参数配合使用
    //attachToRoot如果为true就把布局添加到ViewGroup中
    //若为false则只采用ViewGroup的LayoutParams作为测量的依据却不直接添加到ViewGroup中。
    public View inflate(@LayoutRes int resource, ViewGroup root, boolean attachToRoot) {
      // ...
}
    */


}