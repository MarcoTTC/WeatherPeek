package br.com.marcottc.weatherpeek.model.dto.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

// Daily precipitation in One Call 4.0 is documented inconsistently: the migration guide keeps the
// 3.0 shape (a plain number holding the daily total in mm, e.g. "rain": 12.57), while the 4.0 field
// list describes an object (e.g. "rain": { "1h": 12.57 }). Accepting both keeps a doc/API mismatch
// from failing the parse of the whole response.
class PrecipitationDeserializer : JsonDeserializer<Double> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Double = when {
        json.isJsonPrimitive && json.asJsonPrimitive.isNumber -> json.asDouble
        json.isJsonObject -> json.asJsonObject.entrySet()
            .firstOrNull { it.value.isJsonPrimitive && it.value.asJsonPrimitive.isNumber }
            ?.value?.asDouble ?: 0.0
        else -> 0.0
    }
}
