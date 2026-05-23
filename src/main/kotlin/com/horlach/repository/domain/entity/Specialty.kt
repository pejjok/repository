package com.horlach.repository.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "specialties")
class Specialty(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Column(nullable = false, unique = true)
    var code: String,

    @Column(nullable = false)
    var name: String,

    @OneToMany(mappedBy = "specialty")
    val groups: List<Group> = mutableListOf(),

    @OneToMany(mappedBy = "specialty")
    val users: List<User> = mutableListOf()
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Specialty) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}