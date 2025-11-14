package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


/**
 * Created on octubre.
 * year 2025 .
 */

object DateUtils {

//    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    //region Resumen
    /*
    *   | Característica                            | `ofInstant()`         | `atZone(...).toLocalDateTime()` |
        | ----------------------------------------- | --------------------- | ------------------------------- |
        | Simplicidad                               | ✅ Más corta y directa | 🔸 Un paso más                  |
        | Flexibilidad (zonas horarias)             | 🔸 Limitada           | ✅ Más control                   |
        | Resultado (hora local)                    | Igual                 | Igual                           |
        | Recomendado para formato UI local         | ✅ Sí                  | ✅ Sí                            |
        | Recomendado para manejar time zones o UTC | 🔸 No                 | ✅ Sí                            |
        | Compatibilidad con versiones anteriores   | 🔸 No (Java 8+)       | 🔸 No (Java 8+)                 |
    *
    *
    * 🔹 Usa .atZone(...).toLocalDateTime() cuando:

        Estás trabajando con zonas horarias explícitamente (ZonedDateTime).

        Planeas mostrar o comparar fechas en distintas zonas (UTC, America/Lima, etc.).

        Quieres dejar claro que estás “atando” el Instant a una zona antes de quitarla.
    * */
    //endregion

    private val defaultFormatter: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    }
    private val dateFormatYMD1: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    }
//    val inputDateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
//    val outputFormat = SimpleDateFormat("dd 'de' MMMM", Locale("es"))
    private val formatDayMonthSelect: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("dd-MM", Locale.getDefault())
    }
    private val formatDayMonth: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale.getDefault())
    }

    private val formatDayMonthYear: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
    }

    //dd-MM-YYYY to "dd 'de' MMMM yyyy"
    fun formatDayMonthYear(input: String): String {
        val parts = input.split("/")
        if (parts.size != 3) throw IllegalArgumentException("Input must be in the format 'dd/MM/YYYY'")

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
//        val currentYear = LocalDate.now().year

        val localDate = LocalDate.of(year, month, day)
        return localDate.format(formatDayMonthYear)
    }

    fun formatDayMonth(input: String): String {
        val parts = input.split("-")
        if (parts.size != 2) throw IllegalArgumentException("Input must be in the format 'dd-MM'")

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val currentYear = LocalDate.now().year

        val localDate = LocalDate.of(currentYear, month, day)
        return localDate.format(formatDayMonth)
    }

    fun formatetDayMonthSelect(date: Date): String {
        val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        return localDateTime.format(formatDayMonth)
    }

    fun format(date: Date): String {
        val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        return localDateTime.format(defaultFormatter)
    }
    fun formatYMD(date: Date): String {
        val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
        return localDateTime.format(dateFormatYMD1)
    }

    fun formatDateModern(date: Date?, pattern: String): String {
        if (date == null) return ""
//        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        val localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return localDateTime.format(defaultFormatter)
    }

    //region Usando SimpleDateFormat para compatibilidad con versiones anteriores
    fun formatDate(date: Date?, pattern: String): String {
        if (date == null) return ""

        return try {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            "" // Evita que la app crashee si el formato es inválido
        }
    }
    fun formateDateEnglish(date: Date?, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return dateFormat.format(date)
    }

    fun formateDateContry(date: Date?, format: String, locale: Locale): String {
        val dateFormat = SimpleDateFormat(format, locale)
        return dateFormat.format(date)
    }
    //endregion
}
