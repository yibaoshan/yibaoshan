package cn.ybs.video.slide

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.Random

class VideoMatchFragment : VideoBaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_match, container, false)
    }

    override fun onResume() {
        super.onResume()
        matching()
    }

    fun matching() {
        Handler(Looper.getMainLooper()).postDelayed({ getPageController()?.switchToVideoPlayPage() }, Random().nextInt(5) * 1000L + 3000)
    }

}
