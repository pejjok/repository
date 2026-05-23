package com.horlach.repository.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "work_files")
class WorkFile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @OneToOne
    @JoinColumn(name = "work_id", nullable = true)
    var work: ScientificWork,

    @Column(nullable = false)
    var fileName: String,

    @Column(nullable = false)
    var originalName: String,

    @Column(nullable = true)
    var fileSize: Long,

    @CreationTimestamp
    var uploadedAt: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorkFile) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}