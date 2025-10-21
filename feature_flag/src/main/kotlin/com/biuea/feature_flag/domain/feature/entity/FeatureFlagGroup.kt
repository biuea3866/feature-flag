package com.biuea.feature_flag.domain.feature.entity

import java.time.ZonedDateTime

class FeatureFlagGroup(
    private val _id: Long,
    private var _featureFlag: FeatureFlag,
    private var _status: FeatureFlagStatus,
    private var _specifics: List<Int>,
    private var _percentage: Int?,
    private var _absolute: Int?,
    private var _updatedAt: ZonedDateTime,
    private val _createdAt: ZonedDateTime,
) {
    val id get() = this._id
    val featureFlag get() = this._featureFlag
    val status get() = this._status
    val specifics get() = this._specifics
    val percentage get() = this._percentage
    val absolute get() = this._absolute
    val updatedAt get() = this._updatedAt
    val createdAt get() = this._createdAt

    private lateinit var _algorithm: FeatureFlagAlgorithm
    val algorithm get() = this._algorithm

    fun containsWorkspace(workspaceId: Int): Boolean {
        this.checkAlgorithmInitialized()
        return this._algorithm.isEnabled(workspaceId)
    }

    fun applyAlgorithm(algorithm: FeatureFlagAlgorithm) {
        this._algorithm = algorithm
    }

    fun checkActivation() {
        if (this.isActivation().not()) {
            throw IllegalStateException("FeatureFlagGroup is inactive")
        }
    }

    fun isActivation(): Boolean {
        return this._status == FeatureFlagStatus.ACTIVE
    }

    fun isEnabled(workspaceId: Int): Boolean {
        return this.isActivation() && this.containsWorkspace(workspaceId)
    }

    fun isEnabled(feature: Feature): Boolean {
        return this._featureFlag.isMatchFeature(feature)
    }

    fun activate() {
        this._status = FeatureFlagStatus.ACTIVE
        this._updatedAt = ZonedDateTime.now()
    }

    fun inactivate() {
        this._status = FeatureFlagStatus.INACTIVE
        this._updatedAt = ZonedDateTime.now()
    }

    private fun checkAlgorithmInitialized() {
        if (!this::_algorithm.isInitialized) {
            throw IllegalStateException("Algorithm is not initialized")
        }
    }

    companion object {
        fun create(
            featureFlag: FeatureFlag,
            status: FeatureFlagStatus,
            algorithmOption: FeatureFlagAlgorithmOption,
            specifics: List<Int>,
            absolute: Int?,
            percentage: Int?,
        ): FeatureFlagGroup {
            when (algorithmOption) {
                FeatureFlagAlgorithmOption.SPECIFIC -> {
                    if (specifics.isEmpty() || specifics.size > MAX_SPECIFIC_SIZE) {
                        throw IllegalArgumentException("workspaceIds is required for SPECIFIC option")
                    }
                }
                FeatureFlagAlgorithmOption.PERCENT -> {
                    if (percentage == null) {
                        throw IllegalArgumentException("workspaceCount and percentage are required for PERCENT option")
                    }
                }
                FeatureFlagAlgorithmOption.ABSOLUTE -> {
                    if (absolute == null) {
                        throw IllegalArgumentException("absoluteCount is required for ABSOLUTE option")
                    }
                }
            }

            val algorithm = FeatureFlagAlgorithmDecider.decide(
                algorithmOption = algorithmOption,
                specifics = specifics,
                percentage = percentage,
                absolute = absolute,
            )

            return FeatureFlagGroup(
                _id = 0L,
                _featureFlag = featureFlag,
                _status = status,
                _specifics = specifics,
                _absolute = absolute,
                _percentage = percentage,
                _createdAt = ZonedDateTime.now(),
                _updatedAt = ZonedDateTime.now(),
            ).apply {
                this.applyAlgorithm(algorithm)
            }
        }

        const val MAX_SPECIFIC_SIZE: Int = 10000
    }
}

enum class FeatureFlagStatus {
    ACTIVE,
    INACTIVE,
}

fun Collection<FeatureFlagGroup>.associatedByFeatureFlag(): Map<FeatureFlag, FeatureFlagGroup> {
    return this.associateBy { it.featureFlag }
}

fun Collection<FeatureFlagGroup>.isEnabled(feature: Feature): Boolean {
    return this.any { it.featureFlag.isMatchFeature(feature) && it.isActivation() }
}