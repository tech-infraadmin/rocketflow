package com.rf.taskmodule.utils

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import com.rf.taskmodule.R
import kotlinx.android.synthetic.main.play_video_vertically_activity.*


class PlayVideoVerticallyActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    var mVV: VideoView? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_video_vertically_activity)
        mVV = findViewById(R.id.videoView);
        if(intent.hasExtra("url")){
            val url=intent.getStringExtra("url")
            playVideo(url)
            mVV!!.setOnCompletionListener(this);
            mVV!!.setOnPreparedListener { mp -> mp.isLooping = false }
            mVV!!.setOnTouchListener { v, _ ->
                if ((v as VideoView).isPlaying) (v as VideoView).pause() else (v as VideoView).start()
                true
            }
            if (!playFileRes(url)) return;
            mVV!!.start();
            mVV!!.setOnPreparedListener{ mp->
                val videoWidth: Int = mp.videoWidth
                val videoHeight: Int = mp.videoHeight

                val videoViewWidth = videoView.width
                val videoViewHeight = videoView.height

                val xScale = videoViewWidth.toFloat() / videoWidth
                val yScale = videoViewHeight.toFloat() / videoHeight

                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min

                //For Center Crop use the Math.max to calculate the scale
                //float scale = Math.max(xScale, yScale);
                //For Center Inside use the Math.min scale.
                //I prefer Center Inside so I am using Math.min
                val scale = Math.min(xScale, yScale)

                val scaledWidth = scale * videoWidth
                val scaledHeight = scale * videoHeight

                //Set the new size for the VideoView based on the dimensions of the video

                //Set the new size for the VideoView based on the dimensions of the video
                val layoutParams = videoView.layoutParams
                layoutParams.width = scaledWidth.toInt()
                layoutParams.height = scaledHeight.toInt()
                mVV!!.layoutParams = layoutParams

                progressBar.visibility= View.GONE
            }
        }
    }
    private fun playVideo(url: String?) {
        val m = MediaController(this)
        mVV!!.setMediaController(m)
        val u: Uri = Uri.parse(url)
        mVV!!.setVideoURI(u)
        mVV!!.start()
    }

    private fun playFileRes(videoPath: String?): Boolean {
        return if (videoPath == null || "" == videoPath) {
            stopPlaying()
            false
        } else {
            mVV!!.setVideoURI(Uri.parse(videoPath))
            true
        }
    }

    fun stopPlaying() {
        mVV!!.stopPlayback()
        finish()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        finish()
    }

}