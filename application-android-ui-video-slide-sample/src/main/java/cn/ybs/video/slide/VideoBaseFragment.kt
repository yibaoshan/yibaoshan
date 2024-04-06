package cn.ybs.video.slide

import androidx.fragment.app.Fragment
import cn.ybs.video.slide.interfaces.IPageController
import cn.ybs.video.slide.interfaces.IPageState
import java.lang.ref.WeakReference

abstract class VideoBaseFragment : Fragment() {

    private var pageStateRef: WeakReference<IPageState>? = null

    private var pageControllerRef: WeakReference<IPageController>? = null

    fun getPageState(): IPageState? {
        return pageStateRef?.get()
    }

    fun setPageState(videoPlayState: IPageState) {
        pageStateRef = WeakReference(videoPlayState)
    }

    fun getPageController(): IPageController? {
        return pageControllerRef?.get()
    }

    fun setPageController(videoMatchPageController: IPageController) {
        pageControllerRef = WeakReference(videoMatchPageController)
    }

}