package com.horlach.repository.controllers

import com.horlach.repository.domain.WorkType
import com.horlach.repository.services.StatisticService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/statistic")
class StatisticController(
    private val statisticService: StatisticService
) {

    @GetMapping("/general")
    fun getGeneralStatistics(): ResponseEntity<Map<String, Long>> {
        return ResponseEntity.ok(statisticService.getGeneralStatistics())
    }

    @GetMapping("/by-specialty")
    fun getWorksCountBySpecialty(): ResponseEntity<Map<UUID, Long>> {
        return ResponseEntity.ok(statisticService.getWorksCountBySpecialty())
    }

    @GetMapping("/by-year")
    fun getWorksCountByYear(): ResponseEntity<Map<Int, Long>> {
        return ResponseEntity.ok(statisticService.getWorksCountByYear())
    }

    @GetMapping("/by-type")
    fun getWorksCountByMonth(): ResponseEntity<Map<WorkType, Long>> {
        return ResponseEntity.ok(statisticService.getWorksCountByType())
    }
}
