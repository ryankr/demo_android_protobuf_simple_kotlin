package com.elyeproj.demosimpleprotobufkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import tutorial.Dataformat
import tutorial.Dataformat.Person
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    companion object {
        private var TAG = MainActivity.javaClass.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txt_main.setOnClickListener {
            protobuf()
        }
    }

    private fun intToFourByteArray(value: Int): ByteArray {
        return ByteBuffer.allocate(4).putInt(value).array()
    }

    private fun fourByteToInt(bytes: ByteArray): Int {
        return ByteBuffer.wrap(bytes).int
    }

    private fun protobuf() {
        val responseStream = ByteArrayOutputStream()
        val header = Dataformat.Header.newBuilder().apply {
            transactionId = 333
            msg = "hello world"
        }.build().writeTo(responseStream)
        val headerSize = responseStream.size()

        Log.d(TAG, "header: ${header.toString()}")
        Log.d(TAG, "headerSize: ${headerSize}")

        val body = Dataformat.Person.newBuilder().apply {
            name = "ryan"
            id = 2021
            email = "ryan.see@kakaopaycorp.com"
            phone = "+821012349876"
        }.build().writeTo(responseStream)

        val packet = ByteArrayOutputStream()
        packet.write(intToFourByteArray(headerSize))
        responseStream.writeTo(packet)

        packet.use {
            val input = ByteArrayInputStream(it.toByteArray())

            val headerSize: Int = ByteArray(4).run {
                input.read(this, 0, 4)
                fourByteToInt(this)
            }
            val packetHeader = ByteArray(headerSize).also { buf ->
                input.read(buf, 0, headerSize)
            }
            val header = Dataformat.Header.parseFrom(packetHeader)
            val body = Dataformat.Person.parseFrom(input)
            Log.d(TAG, "header: ${header.toString()}")
            Log.d(TAG, "body: ${body.toString()}")
        }
    }

    private fun showResult(result: Person) {
        txt_main.text = result.toString()
    }
}
