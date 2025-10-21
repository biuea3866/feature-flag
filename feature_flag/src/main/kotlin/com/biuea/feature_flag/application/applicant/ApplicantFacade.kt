package com.biuea.feature_flag.application.applicant

import com.biuea.feature_flag.domain.applicant.ApplicantService
import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.isEnabled
import com.biuea.feature_flag.domain.feature.service.FeatureFlagService
import org.springframework.stereotype.Component

@Component
class ApplicantFacade(
    private val featureFlagService: FeatureFlagService,
    private val applicantService: ApplicantService
) {
    fun execute(workspaceId: Int) {
        featureFlagService.fetchFeatureFlagGroups(workspaceId)
            .forEach {
                when {
                    it.isEnabled(Feature.AI_SCREENING) -> applicantService.aiScreeningFeature()
                    it.isEnabled(Feature.APPLICANT_EVALUATOR) -> applicantService.applicantEvaluatorFeature()
                    else -> applicantService.commonBusiness()
                }
            }
    }
}