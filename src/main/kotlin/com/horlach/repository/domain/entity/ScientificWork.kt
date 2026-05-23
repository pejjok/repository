package com.horlach.repository.domain.entity

import com.horlach.repository.domain.ModerationStatus
import com.horlach.repository.domain.WorkType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "scientific_works")
class ScientificWork(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var annotation: String,

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    var student: User,

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    var supervisor: User,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var workType: WorkType,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var moderationStatus: ModerationStatus = ModerationStatus.PENDING,

    @Column(nullable = true)
    var publicationYear: Int?,

    @Column(nullable = true)
    var publishedAt: Instant?,

    @OneToOne(mappedBy = "work", cascade = [CascadeType.ALL], orphanRemoval = true)
    var file: WorkFile?,

    @OneToMany(mappedBy = "work", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<WorkComment> = mutableListOf(),

    @CreationTimestamp
    var createdAt: Instant,

    @UpdateTimestamp
    var updatedAt: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScientificWork) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}