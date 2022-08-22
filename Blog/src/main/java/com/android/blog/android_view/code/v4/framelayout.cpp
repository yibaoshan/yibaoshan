//
// Created by Bob on 2022/8/22.
//

/frameworks/base/core/java/android/view/View.java
class View {

}

/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup {



}

/frameworks/base/core/java/android/widget/FrameLayout.java
class FrameLayout {

    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < getChildCount(); i++) {
            //调用子view的measure()方法进行测量
            measureChildWithMargins();
        }
    }

}