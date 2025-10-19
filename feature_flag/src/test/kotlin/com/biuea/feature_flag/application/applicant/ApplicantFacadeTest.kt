package com.biuea.feature_flag.application.applicant

import com.biuea.feature_flag.domain.applicant.ApplicantService
import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.feature.service.FeatureFlagService
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.time.ZonedDateTime

class ApplicantFacadeTest : DescribeSpec({
    describe("ApplicantFacade 클래스") {
        val featureFlagService = mockk<FeatureFlagService>()
        val applicantService = spyk(ApplicantService())
        val applicantFacade = ApplicantFacade(featureFlagService, applicantService)

        context("execute 메서드 호출 시") {
            it("AI_SCREENING 기능이 활성화되어 있으면 aiScreeningFeature 메서드를 호출한다") {
                // given
                val workspaceId = 1
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )

                every { featureFlagService.fetchFeatureFlags(workspaceId) } returns featureFlags

                // when
                applicantFacade.execute(workspaceId)

                // then
                verify { applicantService.aiScreeningFeature() }
                verify(exactly = 0) { applicantService.applicantEvaluatorFeature() }
            }

            it("APPLICANT_EVALUATOR 기능이 활성화되어 있으면 applicantEvaluatorFeature 메서드를 호출한다") {
                // given
                val workspaceId = 1
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.APPLICANT_EVALUATOR,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )

                every { featureFlagService.fetchFeatureFlags(workspaceId) } returns featureFlags

                // when
                applicantFacade.execute(workspaceId)

                // then
                verify(exactly = 0) { applicantService.aiScreeningFeature() }
                verify { applicantService.applicantEvaluatorFeature() }
            }

            it("활성화된 기능이 없으면 commonBusiness 메서드를 호출한다") {
                // given
                val workspaceId = 1
                val featureFlags = emptyList<FeatureFlag>()

                every { featureFlagService.fetchFeatureFlags(workspaceId) } returns featureFlags

                // when
                applicantFacade.execute(workspaceId)

                // then
                verify(exactly = 0) { applicantService.aiScreeningFeature() }
                verify(exactly = 0) { applicantService.applicantEvaluatorFeature() }
                verify(atLeast = 1) { applicantService.commonBusiness() }
            }

            it("AI_SCREENING과 APPLICANT_EVALUATOR 기능이 모두 활성화되어 있으면 AI_SCREENING이 우선 적용된다") {
                // given
                val workspaceId = 1
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    ),
                    FeatureFlag(
                        _id = 2L,
                        _feature = Feature.APPLICANT_EVALUATOR,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )

                every { featureFlagService.fetchFeatureFlags(workspaceId) } returns featureFlags

                // when
                applicantFacade.execute(workspaceId)

                // then
                verify { applicantService.aiScreeningFeature() }
                verify(exactly = 0) { applicantService.applicantEvaluatorFeature() }
            }
        }
    }
})
