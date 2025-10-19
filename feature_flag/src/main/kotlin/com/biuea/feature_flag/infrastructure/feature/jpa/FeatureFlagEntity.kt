package com.biuea.feature_flag.infrastructure.feature.jpa

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.ZonedDateTime

@Table(name = "feature_flag")
@Entity
class FeatureFlagEntity(
    @Column(name = "feature")
    @Enumerated(EnumType.STRING)
    val feature: Feature,
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    val status: FeatureFlagStatus,
    @Column(name = "created_at")
    val createdAt: ZonedDateTime,
    @Column(name = "updated_at")
    val updatedAt:ZonedDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
}

internal fun FeatureFlagEntity.toDomain(): FeatureFlag {
    return FeatureFlag(
        _id = this.id,
        _feature = this.feature,
        _status = this.status,
        _createdAt = this.createdAt,
        _updatedAt = this.updatedAt,
    )
}

internal fun FeatureFlag.toEntity(): FeatureFlagEntity {
    return FeatureFlagEntity(
        feature = this.feature,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}