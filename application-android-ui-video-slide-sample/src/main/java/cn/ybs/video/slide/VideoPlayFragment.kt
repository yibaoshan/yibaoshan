package cn.ybs.video.slide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shuyu.gsyvideoplayer.listener.GSYStateUiListener
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

class VideoPlayFragment : VideoBaseFragment(), GSYStateUiListener {

    // 视频播放状态
    private var videoState: Int = 0

    // 视频测试链接
    private val videoUrls = listOf(
        "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209104902N3v5Vpxuvb.mp4", "https://stream7.iqilu.com/10339/upload_transcode/202002/09/20200209105011F0zPoYzHry.mp4", "https://stream7.iqilu.com/10339/article/202002/16/3be2e4ef4aa21bfe7493064a7415c34d.mp4", "https://stream7.iqilu.com/10339/upload_transcode/202002/16/20200216050645YIMfjPq5Nw.mp4"
    )

    // 播放器视图
    private lateinit var videoView: GSYVideoPlayer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoView = view.findViewById(R.id.video_player)
    }

    override fun onResume() {
        super.onResume()
        videoView.setUp(videoUrls.random(), true, "")
        videoView.gsyStateUiListener = this
        videoView.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        videoView.release()
    }

    fun getVideoState(): Int = videoState

    override fun onStateChanged(state: Int) {
        videoState = state
        if (state == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE || state == GSYVideoView.CURRENT_STATE_ERROR) {
            videoView.release()
            // 视频结束后，是否需要继续自动进入匹配状态
            if (getPageState()?.isContinuePlayOrMatch() == true) getPageController()?.switchToVideoMatchPage()
        }
    }

}
