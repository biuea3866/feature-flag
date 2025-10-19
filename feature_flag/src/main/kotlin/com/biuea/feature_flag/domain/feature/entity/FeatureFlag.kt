package com.biuea.feature_flag.domain.feature.entity

import java.time.ZonedDateTime

class FeatureFlag(
    private val _id: Long,
    private val _feature: Feature,
    private var _updatedAt: ZonedDateTime,
    private val _createdAt: ZonedDateTime,
) {
    val id get() = this._id
    val feature get() = this._feature
    val updatedAt get() = this._updatedAt
    val createdAt get() = this._createdAt

    fun isMatchFeature(feature: Feature): Boolean {
        return this._feature == feature
    }

    companion object {
        fun create(feature: Feature): FeatureFlag {
            return FeatureFlag(
                _id = 0,
                _feature = feature,
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