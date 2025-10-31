# WiilRainProjectChallengueNasa
will rain challengue

## 📸 Screenshots

**Propuesta**

<table>
  <tr>
     <td></td>

  </tr>
  <tr>
    <td><img src="screenshots/img.png" width=270 height=auto alt="Pantallas Propuestas"></td>

  </tr>
 </table>


## 🌦️ Conceptos Clave de las Métricas

| Métrica | Qué representa | Qué mide exactamente | Ejemplo de interpretación |
|----------|----------------|----------------------|-----------------------------|
| 🌧️ `rain_probability` | Probabilidad de lluvia | Porcentaje (%) de días con lluvia o eventos de lluvia | `70% → alta probabilidad de lluvia` |
| 🌡️ `temperature_average` | Temperatura promedio diaria | Valor medio (°C) de las temperaturas registradas en el día | `25°C → día cálido` |
| 💨 `wind_speed_average` | Velocidad promedio del viento | Valor medio (km/h) del viento medido durante el día | `20 km/h → viento moderado` |

---

### 💡 En resumen

- 🌧️ **`rain_probability`** → mide **probabilidad** (valor porcentual)
- 🌡️ **`temperature_average`** → mide **promedio** (valor continuo en °C)
- 💨 **`wind_speed_average`** → mide **promedio** (valor continuo en km/h)

---

### 🧩 Tipo de variable y justificación

| Métrica | Tipo de variable | Qué representa | Justificación del nombre |
|----------|------------------|----------------|----------------------------|
| 🌧️ `rain_probability` | Probabilística (0–100 %) | Chance de que ocurra lluvia | No es una magnitud promedio, sino una probabilidad de evento |
| 🌡️ `temperature_average` | Continua (°C) | Promedio diario | Variable física promedio |
| 💨 `wind_speed_average` | Continua (km/h) | Promedio diario | Variable física promedio |

---

### ✅ Conclusión

No usamos `rain_average` porque la lluvia no es un fenómeno promedio:  
es **binario o probabilístico** (“llueve o no llueve”).  
El valor entre `0–100 %` refleja la **frecuencia o probabilidad** del evento, no la cantidad promedio.



