package com.connexin.ice.wrapper.service

import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import javax.xml.transform.stream.StreamSource

class StrSource(private val content: String) : StreamSource() {

    override fun getReader(): Reader {
        return StringReader(content)
    }

    override fun setInputStream(inputStream: InputStream?) {
        throw UnsupportedOperationException("setInputStream is not supported")
    }

    override fun getInputStream(): InputStream? {
        return null
    }

    override fun setReader(reader: Reader?) {
        throw UnsupportedOperationException("setReader is not supported")
    }

    override fun toString(): String {
        return content
    }
}