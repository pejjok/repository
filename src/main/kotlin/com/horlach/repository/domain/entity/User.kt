package com.horlach.repository.domain.entity

import com.horlach.repository.domain.UserRole
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
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.util.UUID
import java.time.Instant


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    var role: UserRole,

    @Column(nullable = false)
    var firstName: String,

        @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var middleName: String,

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group?,

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    var specialty: Specialty?,

    @OneToMany(mappedBy = "student", cascade = [CascadeType.ALL], orphanRemoval = true)
    val submittedWorks: List<ScientificWork> = mutableListOf(),

    @OneToMany(mappedBy = "supervisor")
    val assignedWorks: List<ScientificWork> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: List<WorkComment> = mutableListOf(),

    @CreationTimestamp
    var createdAt: Instant,

    @UpdateTimestamp
    var updatedAt: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}