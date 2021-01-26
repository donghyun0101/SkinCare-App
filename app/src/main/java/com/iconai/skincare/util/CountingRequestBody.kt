package com.iconai.skincare.util

import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.NonNull
import java.io.IOException;
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import org.json.JSONObject

class CountingRequestBody(private val delegate: RequestBody, private val listener: Listener) : RequestBody() {
    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        try {
            return delegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(@NonNull sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink: BufferedSink = countingSink.buffer()
        delegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }
    
    internal inner class CountingSink(delegate: Sink?) : ForwardingSink(delegate!!) {
        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(@NonNull source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            listener.onRequestProgress(bytesWritten, contentLength())
        }
    }

    interface Listener {
        fun onRequestProgress(bytesWritten: Long, contentLength: Long)
    }
}