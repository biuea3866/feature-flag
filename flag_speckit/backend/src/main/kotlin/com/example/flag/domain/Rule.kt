package com.example.flag.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class Rule(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "flag_id")
    val featureFlag: FeatureFlag? = null,

    val type: String,
    val percentage: Int?,
    val attributeName: String?,
    val attributeValue: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rule

        if (id != other.id) return false
        if (type != other.type) return false
        if (percentage != other.percentage) return false
        if (attributeName != other.attributeName) return false
        if (attributeValue != other.attributeValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (percentage ?: 0)
        result = 31 * result + (attributeName?.hashCode() ?: 0)
        result = 31 * result + (attributeValue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Rule(id=$id, type='$type', percentage=$percentage, attributeName=$attributeName, attributeValue=$attributeValue)"
    }
}