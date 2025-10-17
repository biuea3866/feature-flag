package com.biuea.feature_flag.domain.entity

import java.time.ZonedDateTime

class FeatureFlagGroup(
    private val id: Long,
    val featureFlag: FeatureFlag,
    private var specifics: List<Int>,
    private var percentage: Int?,
    private var absolute: Int?,
    private var updatedAt: ZonedDateTime,
    private val createdAt: ZonedDateTime,
) {
    private lateinit var algorithm: FeatureFlagAlgorithm

    fun containsWorkspace(workspaceId: Int): Boolean {
        this.checkAlgorithmInitialized()
        return this.algorithm.isEnable(workspaceId)
    }

    fun changeAlgorithm(algorithm: FeatureFlagAlgorithm) {
        this.algorithm = algorithm
        this.updatedAt = ZonedDateTime.now()
    }

    fun checkActivation() {
        this.featureFlag.checkActivation()
    }

    fun isAvailable(): Boolean {
        return this.featureFlag.isActive()
    }

    private fun checkAlgorithmInitialized() {
        if (!this::algorithm.isInitialized) {
            throw IllegalStateException("Algorithm is not initialized")
        }
    }

    companion object {
        fun create(
            featureFlag: FeatureFlag,
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
                algorithm = algorithmOption,
                specifics = specifics,
                percentage = percentage,
                absolute = absolute,
            )

            return FeatureFlagGroup(
                id = 0L,
                featureFlag = featureFlag,
                specifics = specifics,
                absolute = absolute,
                percentage = percentage,
                createdAt = ZonedDateTime.now(),
                updatedAt = ZonedDateTime.now(),
            ).apply {
                this.changeAlgorithm(algorithm)
            }
        }

        const val MAX_SPECIFIC_SIZE: Int = 10000
    }
}

fun Collection<FeatureFlagGroup>.associatedByFeatureFlag(): Map<FeatureFlag, FeatureFlagGroup> {
    return this.associateBy { it.featureFlag }
}