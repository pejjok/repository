package com.horlach.repository.services

import com.horlach.repository.domain.WorkType
import java.util.UUID

interface StatisticService {
    fun getGeneralStatistics(): Map<String, Long>
    fun getWorksCountBySpecialty(): Map<UUID, Long>
    fun getWorksCountByYear(): Map<Int, Long>
    fun getWorksCountByType(): Map<WorkType, Long>
}