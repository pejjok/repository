package com.horlach.repository.services.impl

import com.horlach.repository.domain.WorkType
import com.horlach.repository.repositories.GroupRepository
import com.horlach.repository.repositories.ScientificWorkRepository
import com.horlach.repository.repositories.SpecialtyRepository
import com.horlach.repository.repositories.UserRepository
import com.horlach.repository.services.StatisticService
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@Service
class StatisticServiceImpl(
    private val userRepository: UserRepository,
    private val specialtyRepository: SpecialtyRepository,
    private val scientificWorkRepository: ScientificWorkRepository,
    private val groupRepository: GroupRepository
) : StatisticService {

    override fun getGeneralStatistics(): Map<String, Long> {
        return mapOf(
            "usersCount" to userRepository.count(),
            "specialtiesCount" to specialtyRepository.count(),
            "groupsCount" to groupRepository.count(),
            "worksCount" to scientificWorkRepository.count()
        )
    }

    override fun getWorksCountBySpecialty(): Map<UUID, Long> {
        return scientificWorkRepository.findAll()
            .groupBy { it.group.specialty.id!! }
            .mapValues { it.value.size.toLong() }
    }

    override fun getWorksCountByYear(): Map<Int, Long> {
        return scientificWorkRepository.findAll()
            .groupBy { it.publicationYear }
            .mapValues { it.value.size.toLong() }
    }

    override fun getWorksCountByType(): Map<WorkType, Long> {
        return scientificWorkRepository.findAll()
            .groupBy { it.workType }
            .mapValues { it.value.size.toLong() }
    }
}
