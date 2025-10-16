package com.biuea.feature_flag.domain.entity

class FeatureFlag(
    private val _id: Long,
    private val _feature: Feature,
    private var _status: FeatureFlagStatus
) {
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
    }

    fun inactivate() {
        this._status = FeatureFlagStatus.INACTIVE
    }

    companion object {
        fun create(
            feature: Feature,
            status: FeatureFlagStatus
        ): FeatureFlag {
            return FeatureFlag(
                _id = 0,
                _feature = feature,
                _status = status
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