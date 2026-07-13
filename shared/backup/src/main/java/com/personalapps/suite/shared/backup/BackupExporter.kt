package com.personalapps.suite.shared.backup

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

object BackupExporter {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun <T> exportToJson(data: T, serializer: KSerializer<T>): String {
        return json.encodeToString(serializer, data)
    }

    fun <T> importFromJson(jsonString: String, serializer: KSerializer<T>): T {
        return json.decodeFromString(serializer, jsonString)
    }
}
