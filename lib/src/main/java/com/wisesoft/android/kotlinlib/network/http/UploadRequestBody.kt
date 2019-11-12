package com.wisesoft.android.kotlinlib.network.http

import com.wisesoft.android.kotlinlib.utils.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * @Title:  UploadResquestBody
 * @Package  com.wisesoft.android.kotlinlib.api
 * @Description:  构造上传请求body
 * @date Create on 2018/11/7 17:06.
 * @author Create by qinyuanmao
 * @email qinyuanmao.live@gmail.com
 */
class UploadRequestBody(
    private val mRequestBody: RequestBody,
    private val mChannel: Channel<String?>
) : RequestBody() {
    private var mBufferedSink: BufferedSink? = null
    override fun contentType(): MediaType? = mRequestBody.contentType()

    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    override fun writeTo(sink: BufferedSink) {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink))
        }
        try {
            mRequestBody.writeTo(mBufferedSink!!)
            mBufferedSink!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            launch {
                mChannel.send(e.message)
                mChannel.close()
            }
        }
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWritten = 0L
            var contentLength = 0L
            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                launch {
                    mChannel.send(((bytesWritten.toDouble() / contentLength.toDouble()) * 100).toInt().toString())
                }
                if (((bytesWritten.toDouble() / contentLength.toDouble()) * 100).toInt().toString() == "100") {
                    mChannel.close()
                }
            }
        }
    }
}