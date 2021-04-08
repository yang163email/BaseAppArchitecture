package com.yan.myapplication.manager

import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.parcelsanta.base.ktx.common.AppContext

/**
 * @author : yan
 * @date   : 2020/10/21 9:56
 * @desc   : SoundPoolHelper
 */
object SoundPoolHelper : SoundPool.OnLoadCompleteListener {

    private val TAG = "SoundPoolHelper"

    private var soundPool: SoundPool? = null
    private var soundId = -1
    private var loadComplete = false

    init {
        initialize()
    }

    private fun initialize() {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.setOnLoadCompleteListener(this)

            val resId = -1
            val soundId = soundPool?.load(AppContext, resId, 1)
            Log.d(TAG, "setAudioSource: soundId=$soundId")
        }
    }

    fun play() {
        initialize()
        if (loadComplete) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    override fun onLoadComplete(soundPool: SoundPool?, sampleId: Int, status: Int) {
        soundId = sampleId
        loadComplete = true
    }

    fun release() {
        val sp = soundPool ?: return
        sp.autoPause()
        sp.unload(soundId)
        sp.release()
        soundPool = null
        loadComplete = false
    }
}