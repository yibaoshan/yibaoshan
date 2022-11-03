//
// Created by Bob on 2022/11/3.
//

/*

写在前面，在 Android Framework 中，和 Fragment 相关的类有以下几个：

1. FragmentContainer ，抽象的父类，定义了两个方法，不重要
2.

它们几个都在 /frameworks/base/core/java/android/app/ 目录下

*/

class Fragment {

    int INITIALIZING = 0;     // Not yet created.
    int CREATED = 1;          // Created.
    int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    int STOPPED = 3;          // Fully created, not started.
    int STARTED = 4;          // Created and started, not resumed.
    int RESUMED = 5;          // Created started and resumed.

    FragmentHostCallback mHost; // 内部持有 Context 对象，其本质是宿主 Activity

    void initState() {
        mHost = null;
    }
}

class FragmentManager {

    int mCurState = Fragment.INITIALIZING;

    ArrayList<Fragment> mActive; // 当前 Activity 中，所有 Fragment 对象的集合，无视状态
    ArrayList<Fragment> mAdded; // 当前 Activity 中，未被移除的、能够正常显示的 Fragment 对象集合

    void addFragment(fragment, moveToStateNow) {
        // 注意，根据添加的前后顺序，mAdded 可以理解为 mActive 的子集
        makeActive(fragment); // 先添加到 mActive 集合
        if (!fragment.mDetached) {
            mAdded.add(fragment); // 如果 Fragment 未被移除，再添加到 mAdded 集合中
        }
    }

    void makeActive(f) {
        mActive.add(f);
    }

    void makeInactive(f) {
        f.initState(); // 此调用会清空 Fragment 全部状态，包括 mHost
    }

    void moveToState(f, newState, transit, transitionStyle, keepActive){
        if (f.mState < newState) {

            switch (f.mState) {
                case Fragment.INITIALIZING:
                    f.mHost = mHost;
            }

        } else if (f.mState > newState) {

            switch (f.mState) {
                case Fragment.CREATED:
                    if (newState < Fragment.CREATED) {
                        if (!keepActive) {
                            if (!f.mRetaining) {
                                makeInactive(f);
                            } else {
                                f.mHost = null;
                            }
                        }
                    }
            }
        }
    }

    void saveAllState(){
    }

    // 对应 Activity onCreate()
    void dispatchCreate() {
        moveToState(Fragment.CREATED, 0 , 0, false);
    }

}

class Activity {

    void onSaveInstanceState(){
    }

}