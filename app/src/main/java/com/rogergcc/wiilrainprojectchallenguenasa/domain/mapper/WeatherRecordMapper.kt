package com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper

import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherYearRecord
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherRecordMapper {
    fun map(dataset: WeatherDataset): List<WeatherYearRecord> =
        dataset.yearly_data.map { yearRecord ->
            WeatherYearRecord(
                year = yearRecord.year,
                precip_mm = yearRecord.precip_mm,
                temp_c = yearRecord.temp_c,
                wind_kmh = yearRecord.wind_kmh,
                cloud_fraction = yearRecord.cloud_fraction,
            )
        }
}
