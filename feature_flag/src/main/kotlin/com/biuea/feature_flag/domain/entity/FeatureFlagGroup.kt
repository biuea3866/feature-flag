package com.biuea.feature_flag.domain.entity

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class FeatureFlagGroup(
    private val _id: Long,
    private val _name: String,
    private val _featureFlag: FeatureFlag,
    private var _workspaceIds: List<Int>,
) {
    private lateinit var _algorithm: FeatureFlagGroupAlgorithm

    fun decideGroup() {
        this.checkAlgorithmInitialized()
        this._workspaceIds = this._algorithm.decideGroup()
    }

    fun changeAlgorithm(algorithm: FeatureFlagGroupAlgorithm) {
        this._algorithm = algorithm
    }

    fun checkActivation() {
        this._featureFlag.checkActivation()
    }

    fun isAvailable(): Boolean {
        return this._featureFlag.isActive()
    }

    fun isAppliedTargetTo(workspaceId: Int): Boolean {
        return this._workspaceIds.contains(workspaceId)
    }

    private fun checkAlgorithmInitialized() {
        if (!this::_algorithm.isInitialized) {
            throw IllegalStateException("Algorithm is not initialized")
        }
    }

    companion object {
        fun create(
            name: String,
            featureFlag: FeatureFlag,
            algorithm: FeatureFlagGroupAlgorithm
        ): FeatureFlagGroup {
            return FeatureFlagGroup(
                _id = 0L,
                _name = name,
                _featureFlag = featureFlag,
                _workspaceIds = emptyList()
            ).apply {
                this.changeAlgorithm(algorithm)
                this.decideGroup()
            }
        }
    }
}