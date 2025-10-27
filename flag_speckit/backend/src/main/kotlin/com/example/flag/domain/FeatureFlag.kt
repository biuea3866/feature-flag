package com.example.flag.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
data class FeatureFlag(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String?,
    var enabled: Boolean = false,

    @OneToMany(mappedBy = "featureFlag", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = Rule::class)
    var rules: List<Rule> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FeatureFlag

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String {
        return "FeatureFlag(id=$id, name='$name', description=$description, enabled=$enabled)"
    }
}