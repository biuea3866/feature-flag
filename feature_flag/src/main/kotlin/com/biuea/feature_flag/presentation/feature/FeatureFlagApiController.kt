package com.biuea.feature_flag.presentation.feature

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.service.FeatureFlagService
import com.biuea.feature_flag.presentation.feature.request.ActivateFeatureFlagRequest
import com.biuea.feature_flag.presentation.feature.request.RegisterFeatureFlagAlgorithmRequest
import com.biuea.feature_flag.presentation.feature.request.RegisterFeatureFlagRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.Int

@RestController
@RequestMapping("/feature")
class FeatureFlagApiController(private val featureFlagService: FeatureFlagService) {
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterFeatureFlagRequest): ResponseEntity<Boolean> {
        featureFlagService.registerFeatureFlag(feature = request.feature)

        return ResponseEntity.ok(true)
    }

    @PatchMapping("/group/{featureFlagGroupId}")
    fun activate(
        @RequestBody request: ActivateFeatureFlagRequest,
        @PathVariable featureFlagGroupId: Long,
    ): ResponseEntity<Boolean> {
        featureFlagService.activateFeatureFlag(
            featureFlagGroupId = featureFlagGroupId,
            status = request.status
        )

        return ResponseEntity.ok(true)
    }

    @DeleteMapping("/group/{featureFlagGroupId}")
    fun delete(@PathVariable featureFlagGroupId: Long): ResponseEntity<Boolean> {
        featureFlagService.deleteFeatureFlagGroup(featureFlagGroupId)

        return ResponseEntity.ok(true)
    }

    @PostMapping("/group/register")
    fun registerFeatureFlagAlgorithmOption(
        @RequestBody request: RegisterFeatureFlagAlgorithmRequest,
    ): ResponseEntity<Boolean> {
        featureFlagService.decideFeatureFlagGroup(
            status = request.status,
            feature = request.feature,
            algorithm = request.algorithm,
            specifics = request.specifics?: emptyList(),
            percentage = request.percentage,
            absolute = request.absolute,
        )

        return ResponseEntity.ok(true)
    }
}
