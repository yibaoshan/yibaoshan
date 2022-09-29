package com.android.blog.android.graphics.demo.v4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blog.R;

/**
 * Created on 2022/8/23
 */
public class MeasureTestActivity extends AppCompatActivity {

    private static final int BASE_CNT = 0;
    private static final int BASE_FRAME = 5;
    private static final int BASE_LINEAR = 10;
    private static final int BASE_SCROLL = 15;

    public static final int CNT_FRAME_LAYOUT = BASE_CNT + 1;
    public static final int CNT_LINEAR_LAYOUT = BASE_CNT + 2;
    public static final int CNT_FRAME_DIALOG = BASE_CNT + 3;

    public static final int SPEC_FRAME_MATCH = BASE_FRAME + 1;
    public static final int SPEC_FRAME_WRAP = BASE_FRAME + 2;
    public static final int SPEC_FRAME_DP100 = BASE_FRAME + 3;

    public static final int SPEC_LINEAR_MATCH = BASE_LINEAR + 1;
    public static final int SPEC_LINEAR_WRAP = BASE_LINEAR + 2;
    public static final int SPEC_LINEAR_DP100 = BASE_LINEAR + 3;

    public static final int SPEC_SCROLL_MATCH = BASE_SCROLL + 1;
    public static final int SPEC_SCROLL_WRAP = BASE_SCROLL + 2;
    public static final int SPEC_SCROLL_DP100 = BASE_SCROLL + 3;

    public static void startActivity(Context context, int type) {
        if (context == null) return;
        Intent intent = new Intent(context, MeasureTestActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra("type", 0)) {
            case CNT_FRAME_DIALOG:
                /**
                 * Activity 为 Dialog 主题时，onMeasure() 执行次数测试，ViewGroup 为 FrameLayout，宽高均为 match_parent，测试结果：
                 *
                 * 首次加载 Activity 时，执行了6次测量，调用链（宿主手机版本 Android 12，括号内为行数）：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 03. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 04. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 * 05. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 * 06. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 *
                 * 调用 requestLayout 后，执行了3次测量，调用链：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 03. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 *
                 * 将 FrameLayout 宽度改为 width=1080px（屏幕宽度）以后，再次测量 onMeasure() 执行次数，测试结果：
                 *
                 * 首次加载 Activity 时，执行了12次测量，调用链（宿主手机版本 Android 12，括号内为行数）：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 03. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 04. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)//注意行数变化
                 * 05. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)
                 * 06. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)
                 * 07. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)//注意行数变化
                 * 08. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * 09. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * 10. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 * 11. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 * 12. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 *
                 * 调用 requestLayout 后，执行了9次测量，调用链：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 03. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2380)->performMeasure(#3068)
                 * 04. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)//注意行数变化
                 * 05. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)
                 * 06. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2393)->performMeasure(#3068)
                 * 07. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)//注意行数变化
                 * 08. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * 09. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * */
                setTheme(R.style.Theme_AppCompat_Dialog);
            case CNT_FRAME_LAYOUT:
                /**
                 * onMeasure() 执行次数测试，ViewGroup 为 FrameLayout，宽高均为 match_parent，测试结果：
                 *
                 * 首次加载 Activity 时，执行了2次测量，调用链（宿主手机版本 Android 12，括号内为行数）：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 *
                 * 调用 requestLayout 后，执行1次测量，调用链：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * */
                setContentView(R.layout.activity_measure_count_frame_layout);
                break;
            case CNT_LINEAR_LAYOUT:
                /**
                 * onMeasure() 执行次数测试，ViewGroup 为 LinearLayout，宽高均为 match_parent，测试结果和 FrameLayout 相同：
                 *
                 * 首次加载 Activity 时，执行了2次测量，调用链（宿主手机版本 Android 12，括号内为行数）：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * 02. ViewRootImpl.performTraversals(#3082)->performMeasure(#3068)
                 *
                 * 调用 requestLayout 后，执行1次测量，调用链：
                 * 01. ViewRootImpl.performTraversals(#2677)->measureHierarchy(#2407)->performMeasure(#3068)
                 * */
                setContentView(R.layout.activity_measure_count_linear_layout);
                break;
            case SPEC_FRAME_MATCH:
                /**
                 * 第一轮 MeasureSpec 测试，ViewGroup 为 FrameLayout，测试结果：
                 *
                 * 父视图 FrameLayout 宽高均为 match_parent：mode=EXACTLY，size=screenSize
                 *
                 * 自定义View 1 宽高均为 match_parent：mode=EXACTLY size=screenSize
                 *
                 * 自定义View 2 宽高均为 warp_content：mode=AT_MOST size=screenSize
                 *
                 * 自定义View 3 宽高均为 100dp：mode=EXACTLY size=100dp
                 * */
                setContentView(R.layout.activity_measure_spec_frame_match);
                break;
            case SPEC_FRAME_WRAP:
                /**
                 * 第二轮 MeasureSpec 测试，ViewGroup 为 FrameLayout，测试结果：
                 *
                 * 父视图 FrameLayout 宽高均为 warp_content：mode=AT_MOST，size=screenSize
                 *
                 * 自定义 View 1 宽高均为 match_parent，经历了2次测量：
                 * 01. mode=AT_MOST size=screenSize
                 * 02. mode=EXACTLY size=screenSize
                 *
                 * 自定义View 2 宽高均为 warp_content：mode=AT_MOST size=screenSize
                 *
                 * 自定义View 3 宽高均为 100dp：mode=EXACTLY size=100dp
                 * */
                setContentView(R.layout.activity_measure_spec_frame_wrap);
                break;
            case SPEC_FRAME_DP100:
                /**
                 * 第三轮 MeasureSpec 测试，ViewGroup 为 FrameLayout，测试结果：
                 *
                 * 父视图 FrameLayout 宽高均为 100dp：mode=EXACTLY，size=100dp
                 *
                 * 自定义 View 1 宽高均为 match_parent：mode=EXACTLY，size=100dp
                 *
                 * 自定义View 2 宽高均为 warp_content：mode=AT_MOST size=100dp
                 *
                 * 自定义View 3 宽高均为 200dp（大于父视图）：mode=EXACTLY size=200dp
                 *
                 * 在这一轮测试中，我们将第三组 View 的宽高指定为 200dp，大于父视图的宽高，从结果来看，父视图返回的宽高是我们设定的值
                 *
                 * 如果View的宽高值超过实际屏幕大小时，Android会如何处理呢？
                 *
                 * 答案是，和示例相同，无视屏幕实际宽度，申请多大就给多大
                 * */
                setContentView(R.layout.activity_measure_spec_frame_dp100);
                break;
            case SPEC_LINEAR_MATCH:
                /**
                 * 第四轮 MeasureSpec 测试，ViewGroup 为 LinearLayout，测试结果和第一轮相同
                 * */
                setContentView(R.layout.activity_measure_spec_linear_match);
                break;
            case SPEC_LINEAR_WRAP:
                /**
                 * 第五轮 MeasureSpec 测试，ViewGroup 为 LinearLayout，测试结果和第二轮相同
                 * */
                setContentView(R.layout.activity_measure_spec_linear_wrap);
                break;
            case SPEC_LINEAR_DP100:
                /**
                 * 第六轮 MeasureSpec 测试，ViewGroup 为 LinearLayout，测试结果和第三轮相同
                 * */
                setContentView(R.layout.activity_measure_spec_linear_dp100);
                break;
            case SPEC_SCROLL_MATCH:
                /**
                 * 第七轮 MeasureSpec 测试，ViewGroup 为 ScrollView，测试结果：
                 *
                 * 父视图 ScrollView 宽高均为 match_parent：widthMode=EXACTLY，size=screenSize
                 *
                 * 自定义View 1 宽高均为 match_parent：widthMode=EXACTLY，heightMode=UNSPECIFIED，size=screenSize
                 *
                 * 自定义View 2 宽高均为 warp_content：widthMode=AT_MOST，heightMode=UNSPECIFIED，size=screenSize
                 *
                 * 自定义View 3 宽高均为 100dp：widthMode=EXACTLY，widthSize=100dp，heightMode=UNSPECIFIED，heightSize=screenSize//测量高度为屏幕高度
                 * */
                setContentView(R.layout.activity_measure_spec_scroll_match);
                break;
            case SPEC_SCROLL_WRAP:
                /**
                 * 第八轮 MeasureSpec 测试，ViewGroup 为 ScrollView，测试结果：
                 *
                 * 父视图 ScrollView 宽高均为 wrap_content：widthMode=AT_MOST，size=screenSize
                 *
                 * 自定义View 1 宽高均为 match_parent：widthMode=AT_MOST，heightMode=UNSPECIFIED，size=screenSize
                 *
                 * 自定义View 2 宽高均为 warp_content：widthMode=AT_MOST，heightMode=UNSPECIFIED，size=screenSize
                 *
                 * 自定义View 3 宽高均为 100dp：widthMode=EXACTLY，widthSize=100dp，heightMode=UNSPECIFIED，heightSize=screenSize//测量高度为屏幕高度
                 * */
                setContentView(R.layout.activity_measure_spec_scroll_wrap);
                break;
            case SPEC_SCROLL_DP100:
                /**
                 * 第九轮 MeasureSpec 测试，ViewGroup 为 ScrollView，测试结果：
                 *
                 * 父视图 ScrollView 宽高均为 100dp：widthMode=EXACTLY，size=100dp
                 *
                 * 自定义View 1 宽高均为 match_parent：widthMode=EXACTLY，heightMode=UNSPECIFIED，size=100dp//跟随父视图
                 *
                 * 自定义View 2 宽高均为 warp_content：widthMode=AT_MOST，heightMode=UNSPECIFIED，size=100dp
                 *
                 * 自定义View 3 宽高均为 200dp：widthMode=EXACTLY，widthSize=200dp，heightMode=UNSPECIFIED，heightSize=100dp//非常倔强，无视子 View LayoutParams 指定的大小
                 * */
                setContentView(R.layout.activity_measure_spec_scroll_dp100);
                break;
        }
    }

    public void onLayoutRequest(View view) {
        view.getParent().requestLayout();
    }


}
