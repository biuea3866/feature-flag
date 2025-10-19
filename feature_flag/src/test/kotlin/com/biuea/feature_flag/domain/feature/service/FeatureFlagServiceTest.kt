package com.biuea.feature_flag.domain.feature.service

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagGroupRepository
import com.biuea.feature_flag.domain.feature.repository.FeatureFlagRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime

class FeatureFlagServiceTest : DescribeSpec({
    describe("FeatureFlagService 클래스") {
        val featureFlagRepository = mockk<FeatureFlagRepository>()
        val featureFlagGroupRepository = mockk<FeatureFlagGroupRepository>()
        val featureFlagService = FeatureFlagService(featureFlagRepository, featureFlagGroupRepository)

        context("registerFeatureFlag 메서드 호출 시") {
            it("이미 존재하는 Feature에 대해 예외가 발생한다") {
                // given
                val feature = Feature.AI_SCREENING
                val status = FeatureFlagStatus.ACTIVE

                every { featureFlagRepository.getFeatureFlagOrNullBy(feature) } returns FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = status,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagService.registerFeatureFlag(feature, status)
                }
            }

            it("존재하지 않는 Feature에 대해 새로운 FeatureFlag를 생성하고 저장한다") {
                // given
                val feature = Feature.AI_SCREENING
                val status = FeatureFlagStatus.ACTIVE
                val createdFeatureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = status,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                every { featureFlagRepository.getFeatureFlagOrNullBy(feature) } returns null
                every { featureFlagRepository.save(any()) } returns createdFeatureFlag

                // when
                val result = featureFlagService.registerFeatureFlag(feature, status)

                // then
                result shouldBe createdFeatureFlag
                verify { featureFlagRepository.save(any()) }
            }
        }

        context("activateFeatureFlag 메서드 호출 시") {
            it("FeatureFlag를 활성화하고 저장한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = mockk<FeatureFlag>(relaxed = true)

                every { featureFlagRepository.getFeatureFlagBy(feature) } returns featureFlag
                every { featureFlagRepository.save(featureFlag) } returns featureFlag

                // when
                featureFlagService.activateFeatureFlag(feature, FeatureFlagStatus.ACTIVE)

                // then
                verify { featureFlag.activate() }
                verify { featureFlagRepository.save(featureFlag) }
            }

            it("FeatureFlag를 비활성화하고 저장한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = mockk<FeatureFlag>(relaxed = true)

                every { featureFlagRepository.getFeatureFlagBy(feature) } returns featureFlag
                every { featureFlagRepository.save(featureFlag) } returns featureFlag

                // when
                featureFlagService.activateFeatureFlag(feature, FeatureFlagStatus.INACTIVE)

                // then
                verify { featureFlag.inactivate() }
                verify { featureFlagRepository.save(featureFlag) }
            }
        }

        context("fetchFeatureFlagGroupMap 메서드 호출 시") {
            it("FeatureFlagGroup 목록을 Map으로 변환하여 반환한다") {
                // given
                val featureFlag1 = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlag2 = FeatureFlag(
                    _id = 2L,
                    _feature = Feature.APPLICANT_EVALUATOR,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup1 = FeatureFlagGroup.create(
                    featureFlag = featureFlag1,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                val featureFlagGroup2 = FeatureFlagGroup.create(
                    featureFlag = featureFlag2,
                    algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                    specifics = emptyList(),
                    absolute = null,
                    percentage = 50
                )

                val featureFlagGroups = listOf(featureFlagGroup1, featureFlagGroup2)

                every { featureFlagGroupRepository.getFeatureFlagGroups() } returns featureFlagGroups

                // when
                val result = featureFlagService.fetchFeatureFlagGroupMap()

                // then
                result.size shouldBe 2
                result[featureFlag1] shouldBe featureFlagGroup1
                result[featureFlag2] shouldBe featureFlagGroup2
            }
        }

        context("isEnabled 메서드 호출 시") {
            it("FeatureFlagGroup이 없으면 false를 반환한다") {
                // given
                val feature = Feature.AI_SCREENING
                val workspaceId = 1

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns null

                // when
                val result = featureFlagService.isEnabled(feature, workspaceId)

                // then
                result shouldBe false
            }

            it("FeatureFlagGroup이 비활성화 상태면 false를 반환한다") {
                // given
                val feature = Feature.AI_SCREENING
                val workspaceId = 1
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when
                val result = featureFlagService.isEnabled(feature, workspaceId)

                // then
                result shouldBe false
            }

            it("FeatureFlagGroup이 활성화 상태이고 워크스페이스가 포함되면 true를 반환한다") {
                // given
                val feature = Feature.AI_SCREENING
                val workspaceId = 1
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = listOf(1, 2, 3),
                    absolute = null,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when
                val result = featureFlagService.isEnabled(feature, workspaceId)

                // then
                result shouldBe true
            }

            it("FeatureFlagGroup이 활성화 상태이지만 워크스페이스가 포함되지 않으면 false를 반환한다") {
                // given
                val feature = Feature.AI_SCREENING
                val workspaceId = 4
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = listOf(1, 2, 3),
                    absolute = null,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when
                val result = featureFlagService.isEnabled(feature, workspaceId)

                // then
                result shouldBe false
            }
        }

        context("fetchFeatureFlags 메서드 호출 시") {
            it("워크스페이스에 활성화된 FeatureFlag 목록을 반환한다") {
                // given
                val workspaceId = 1
                val featureFlag1 = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlag2 = FeatureFlag(
                    _id = 2L,
                    _feature = Feature.APPLICANT_EVALUATOR,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlag3 = FeatureFlag(
                    _id = 3L,
                    _feature = Feature.LOOP_INTERVIEW,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup1 = FeatureFlagGroup.create(
                    featureFlag = featureFlag1,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = listOf(1, 2, 3),
                    absolute = null,
                    percentage = null
                )

                val featureFlagGroup2 = FeatureFlagGroup.create(
                    featureFlag = featureFlag2,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = listOf(4, 5, 6),
                    absolute = null,
                    percentage = null
                )

                val featureFlagGroup3 = FeatureFlagGroup.create(
                    featureFlag = featureFlag3,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = listOf(1, 2, 3),
                    absolute = null,
                    percentage = null
                )

                val featureFlagGroups = listOf(featureFlagGroup1, featureFlagGroup2, featureFlagGroup3)

                every { featureFlagGroupRepository.getFeatureFlagGroups() } returns featureFlagGroups

                // when
                val result = featureFlagService.fetchFeatureFlags(workspaceId)

                // then
                result.size shouldBe 1
                result shouldBe listOf(featureFlag1)
            }
        }

        context("fetchAvailableFeatureFlagGroup 메서드 호출 시") {
            it("활성화된 FeatureFlagGroup이 없으면 예외가 발생한다") {
                // given
                val feature = Feature.AI_SCREENING

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns null

                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagService.fetchAvailableFeatureFlagGroup(feature)
                }
            }

            it("FeatureFlagGroup이 비활성화 상태면 예외가 발생한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagService.fetchAvailableFeatureFlagGroup(feature)
                }
            }

            it("활성화된 FeatureFlagGroup이 있으면 반환한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when
                val result = featureFlagService.fetchAvailableFeatureFlagGroup(feature)

                // then
                result shouldBe featureFlagGroup
            }
        }

        context("decideFeatureFlagGroup 메서드 호출 시") {
            it("이미 존재하는 Feature에 대해 예외가 발생한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns featureFlagGroup

                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagService.decideFeatureFlagGroup(
                        feature = feature,
                        algorithm = FeatureFlagAlgorithmOption.ABSOLUTE,
                        specifics = emptyList(),
                        percentage = null,
                        absolute = 100
                    )
                }
            }

            it("존재하지 않는 Feature에 대해 새로운 FeatureFlagGroup을 생성하고 저장한다") {
                // given
                val feature = Feature.AI_SCREENING
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = feature,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )

                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(feature) } returns null
                every { featureFlagRepository.getFeatureFlagBy(feature) } returns featureFlag
                every { featureFlagGroupRepository.save(any()) } returns featureFlagGroup

                // when
                featureFlagService.decideFeatureFlagGroup(
                    feature = feature,
                    algorithm = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    percentage = null,
                    absolute = 100
                )

                // then
                verify { featureFlagGroupRepository.save(any()) }
            }
        }

        context("changeFeatureFlagGroupAlgorithm 메서드 호출 시") {
            it("존재하지 않는 FeatureFlagGroup에 대해 예외가 발생한다") {
                // given
                val featureFlagGroupId = 1L

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(featureFlagGroupId) } returns null

                // when & then
                shouldThrow<NoSuchElementException> {
                    featureFlagService.changeFeatureFlagGroupAlgorithm(
                        featureFlagGroupId = featureFlagGroupId,
                        algorithm = FeatureFlagAlgorithmOption.ABSOLUTE,
                        specifics = emptyList(),
                        percentage = null,
                        absolute = 100
                    )
                }
            }

            it("존재하는 FeatureFlagGroup의 알고리즘을 변경하고 저장한다") {
                // given
                val featureFlagGroupId = 1L
                val featureFlagGroup = mockk<FeatureFlagGroup>(relaxed = true)

                every { featureFlagGroupRepository.getFeatureFlagGroupOrNullBy(featureFlagGroupId) } returns featureFlagGroup
                every { featureFlagGroupRepository.save(featureFlagGroup) } returns featureFlagGroup

                // when
                featureFlagService.changeFeatureFlagGroupAlgorithm(
                    featureFlagGroupId = featureFlagGroupId,
                    algorithm = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    percentage = null,
                    absolute = 100
                )

                // then
                verify { 
                    featureFlagGroup.changeAlgorithm(
                        algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                        specifics = emptyList(),
                        percentage = null,
                        absolute = 100
                    ) 
                }
                verify { featureFlagGroupRepository.save(featureFlagGroup) }
            }
        }
    }
})
