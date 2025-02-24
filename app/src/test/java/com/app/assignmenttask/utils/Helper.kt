package com.app.assignmenttask.utils

import java.io.InputStreamReader

object Helper {

    /**
     * Reads the contents of a file from the resources folder and returns it as a String.
     *
     * @param fileName The name of the file to read.
     * @return The content of the file as a String.
     * @throws NullPointerException if the file is not found in the resources.
     */
    fun readFileResource(fileName: String): String {
        val inputStream = Helper::class.java.getResourceAsStream(fileName)
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream, "UTF-8")
        reader.readLines().forEach {
            builder.append(it)
        }
        return builder.toString()
    }
}