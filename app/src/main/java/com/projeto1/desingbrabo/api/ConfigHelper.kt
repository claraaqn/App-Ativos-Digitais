package com.projeto1.desingbrabo.api

import android.content.Context
import java.util.Properties

object ConfigHelper {
    private var properties: Properties? = null

    fun initialize(context: Context) {
        properties = Properties().apply {
            context.assets.open("local.properties").use { inputStream ->
                load(inputStream)
            }
        }
    }

    fun getProperty(key: String): String {
        return properties?.getProperty(key)
            ?: throw IllegalStateException("Property '$key' not found in local.properties")
    }
}