package com.biuea.feature_flag.domain.feature.entity

import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagEntity
import java.time.ZonedDateTime

class FeatureFlag(
    private val _id: Long,
    private val _feature: Feature,
    private var _status: FeatureFlagStatus,
    private var _updatedAt: ZonedDateTime,
    private val _createdAt: ZonedDateTime,
) {
    val id get() = this._id
    val feature get() = this._feature
    val status get() = this._status
    val updatedAt get() = this._updatedAt
    val createdAt get() = this._createdAt

    fun checkActivation() {
        if (this._status != FeatureFlagStatus.ACTIVE) {
            throw IllegalStateException("cannot available feature")
        }
    }

    fun isActive(): Boolean {
        return this._status == FeatureFlagStatus.ACTIVE
    }

    fun activate() {
        this._status = FeatureFlagStatus.ACTIVE
        this._updatedAt = ZonedDateTime.now()
    }

    fun inactivate() {
        this._status = FeatureFlagStatus.INACTIVE
        this._updatedAt = ZonedDateTime.now()
    }

    companion object {
        fun create(
            feature: Feature,
            status: FeatureFlagStatus
        ): FeatureFlag {
            return FeatureFlag(
                _id = 0,
                _feature = feature,
                _status = status,
                _createdAt = ZonedDateTime.now(),
                _updatedAt = ZonedDateTime.now(),
            )
        }
    }
}

enum class Feature {
    AI_SCREENING,
    APPLICANT_EVALUATOR,
    LOOP_INTERVIEW,
}

enum class FeatureFlagStatus {
    ACTIVE,
    INACTIVE,
}