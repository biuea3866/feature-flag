package com.biuea.feature_flag.infrastructure.feature.jpa

import com.biuea.feature_flag.domain.feature.entity.AbsoluteAlgorithm
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmDecider
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.feature.entity.PercentAlgorithm
import com.biuea.feature_flag.domain.feature.entity.SpecificAlgorithm
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Table(name = "feature_flag_group")
@Entity
class FeatureFlagGroupEntity(
    @Column(name = "feature_flag_id")
    val featureFlagId: Long,
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: FeatureFlagStatus,
    @Convert(converter = SpecificsConverter::class)
    @Column(name = "specifics")
    val specifics: List<Int>,
    @Column(name = "percentage")
    val percentage: Int?,
    @Column(name = "absolute")
    val absolute: Int?,
    @Column(name = "algorithm_option")
    @Enumerated(EnumType.STRING)
    val algorithmOption: FeatureFlagAlgorithmOption,
    @Column(name = "created_at")
    val createdAt: ZonedDateTime,
    @Column(name = "updated_at")
    val updatedAt:ZonedDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
}

@Converter
private class SpecificsConverter : AttributeConverter<List<Int>, String?> {
    override fun convertToDatabaseColumn(attribute: List<Int>): String {
        return attribute.joinToString(",")
    }

    override fun convertToEntityAttribute(dbData: String?): List<Int> {
        if (dbData.isNullOrEmpty()) {
            return emptyList()
        }
        return dbData.split(",").map { it.toInt() }
    }
}

internal fun FeatureFlagGroupEntity.toDomain(featureFlag: FeatureFlagEntity): FeatureFlagGroup {
    return FeatureFlagGroup(
        _id = this.id,
        _status = this.status,
        _featureFlag = featureFlag.toDomain(),
        _specifics = this.specifics,
        _absolute = this.absolute,
        _percentage = this.percentage,
        _createdAt = this.createdAt,
        _updatedAt = this.updatedAt,
    ).apply {
        val algorithm = FeatureFlagAlgorithmDecider.decide(
            algorithm = this@toDomain.algorithmOption,
            specifics = specifics,
            percentage = percentage,
            absolute = absolute
        )
        applyAlgorithm(algorithm)
    }
}

internal fun FeatureFlagGroup.toEntity(): FeatureFlagGroupEntity {
    return FeatureFlagGroupEntity(
        status = this.status,
        featureFlagId = this.featureFlag.id,
        specifics = this.specifics,
        percentage = this.percentage,
        absolute = this.absolute,
        algorithmOption = when (this.algorithm) {
            is SpecificAlgorithm -> FeatureFlagAlgorithmOption.SPECIFIC
            is PercentAlgorithm -> FeatureFlagAlgorithmOption.PERCENT
            is AbsoluteAlgorithm -> FeatureFlagAlgorithmOption.ABSOLUTE
            else -> throw IllegalStateException("Unknown algorithm type")
        },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    ).apply { this.id = this@toEntity.id }
}