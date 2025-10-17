package com.biuea.feature_flag.domain.entity

import java.time.ZonedDateTime

class FeatureFlag(
    private val id: Long,
    private val feature: Feature,
    private var status: FeatureFlagStatus,
    private var updatedAt: ZonedDateTime,
    private val createdAt: ZonedDateTime,
) {
    fun checkActivation() {
        if (this.status != FeatureFlagStatus.ACTIVE) {
            throw IllegalStateException("cannot available feature")
        }
    }

    fun isActive(): Boolean {
        return this.status == FeatureFlagStatus.ACTIVE
    }

    fun activate() {
        this.status = FeatureFlagStatus.ACTIVE
        this.updatedAt = ZonedDateTime.now()
    }

    fun inactivate() {
        this.status = FeatureFlagStatus.INACTIVE
        this.updatedAt = ZonedDateTime.now()
    }

    companion object {
        fun create(
            feature: Feature,
            status: FeatureFlagStatus
        ): FeatureFlag {
            return FeatureFlag(
                id = 0,
                feature = feature,
                status = status,
                createdAt = ZonedDateTime.now(),
                updatedAt =ZonedDateTime.now(),
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