package com.biuea.feature_flag.infrastructure.feature

import com.biuea.feature_flag.domain.feature.entity.Feature
import com.biuea.feature_flag.domain.feature.entity.FeatureFlag
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagAlgorithmOption
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagGroup
import com.biuea.feature_flag.domain.feature.entity.FeatureFlagStatus
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagEntity
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagGroupEntity
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagGroupJpaRepository
import com.biuea.feature_flag.infrastructure.feature.jpa.FeatureFlagJpaRepository
import com.biuea.feature_flag.infrastructure.feature.jpa.toDomain
import com.biuea.feature_flag.infrastructure.feature.jpa.toEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime

class FeatureFlagAdaptorTest : DescribeSpec({
    describe("FeatureFlagAdaptor 클래스") {
        val featureFlagJpaRepository = mockk<FeatureFlagJpaRepository>()
        val featureFlagGroupJpaRepository = mockk<FeatureFlagGroupJpaRepository>()
        val featureFlagAdaptor = FeatureFlagAdaptor(featureFlagJpaRepository, featureFlagGroupJpaRepository)

        context("FeatureFlagRepository 구현") {
            context("save 메서드 호출 시") {
                it("FeatureFlag 엔티티를 저장하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val featureFlag = FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _status = FeatureFlagStatus.ACTIVE,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )

                    val featureFlagEntity = featureFlag.toEntity()

                    every { featureFlagJpaRepository.save(any()) } returns featureFlagEntity

                    // when
                    val result = featureFlagAdaptor.save(featureFlag)

                    // then
                    result.feature shouldBe featureFlag.feature
                    result.status shouldBe featureFlag.status
                    verify { featureFlagJpaRepository.save(any()) }
                }
            }

            context("getFeatureFlags 메서드 호출 시") {
                it("모든 FeatureFlag 엔티티를 조회하고 도메인 객체 목록으로 변환하여 반환한다") {
                    // given
                    val featureFlagEntity1 = FeatureFlagEntity(
                        feature = Feature.AI_SCREENING,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = 1L }

                    val featureFlagEntity2 = FeatureFlagEntity(
                        feature = Feature.APPLICANT_EVALUATOR,
                        status = FeatureFlagStatus.INACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = 2L }

                    every { featureFlagJpaRepository.findAll() } returns listOf(featureFlagEntity1, featureFlagEntity2)

                    // when
                    val result = featureFlagAdaptor.getFeatureFlags()

                    // then
                    result.size shouldBe 2
                    result[0].id shouldBe featureFlagEntity1.id
                    result[0].feature shouldBe featureFlagEntity1.feature
                    result[1].id shouldBe featureFlagEntity2.id
                    result[1].feature shouldBe featureFlagEntity2.feature
                }
            }

            context("getFeatureFlagBy 메서드 호출 시") {
                it("Feature로 FeatureFlag 엔티티를 조회하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING
                    val featureFlagEntity = FeatureFlagEntity(
                        feature = feature,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = 1L }

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns featureFlagEntity

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagBy(feature)

                    // then
                    result.id shouldBe featureFlagEntity.id
                    result.feature shouldBe feature
                }

                it("Feature에 해당하는 FeatureFlag 엔티티가 없으면 예외가 발생한다") {
                    // given
                    val feature = Feature.AI_SCREENING

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns null

                    // when & then
                    shouldThrow<NoSuchElementException> {
                        featureFlagAdaptor.getFeatureFlagBy(feature)
                    }
                }
            }

            context("getFeatureFlagOrNullBy 메서드 호출 시") {
                it("Feature로 FeatureFlag 엔티티를 조회하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING
                    val featureFlagEntity = FeatureFlagEntity(
                        feature = feature,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = 1L }

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns featureFlagEntity

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagOrNullBy(feature)

                    // then
                    result?.id shouldBe featureFlagEntity.id
                    result?.feature shouldBe feature
                }

                it("Feature에 해당하는 FeatureFlag 엔티티가 없으면 null을 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns null

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagOrNullBy(feature)

                    // then
                    result shouldBe null
                }
            }
        }

        context("FeatureFlagGroupRepository 구현") {
            context("save 메서드 호출 시") {
                it("FeatureFlagGroup 엔티티를 저장하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val featureFlag = FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
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

                    val featureFlagEntity = featureFlag.toEntity()
                    val featureFlagGroupEntity = featureFlagGroup.toEntity()

                    every { featureFlagGroupJpaRepository.save(any()) } returns featureFlagGroupEntity
                    every { featureFlagJpaRepository.findByIdOrNull(featureFlagGroupEntity.featureFlagId) } returns featureFlagEntity

                    // when
                    val result = featureFlagAdaptor.save(featureFlagGroup)

                    // then
                    result.id shouldBe featureFlagGroup.id
                    result.specifics shouldBe featureFlagGroup.specifics
                    verify { featureFlagGroupJpaRepository.save(any()) }
                }
            }

            context("getFeatureFlagGroupOrNullBy(id) 메서드 호출 시") {
                it("ID로 FeatureFlagGroup 엔티티를 조회하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val id = 1L
                    val featureFlagId = 2L

                    val featureFlagEntity = FeatureFlagEntity(
                        feature = Feature.AI_SCREENING,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { this.id = featureFlagId }

                    val featureFlagGroupEntity = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId,
                        specifics = listOf(1, 2, 3),
                        percentage = null,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { this.id = id }

                    every { featureFlagGroupJpaRepository.findByIdOrNull(id) } returns featureFlagGroupEntity
                    every { featureFlagJpaRepository.findByIdOrNull(featureFlagId) } returns featureFlagEntity

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(id)

                    // then
                    result?.id shouldBe id
                    result?.featureFlag?.id shouldBe featureFlagId
                }

                it("ID에 해당하는 FeatureFlagGroup 엔티티가 없으면 null을 반환한다") {
                    // given
                    val id = 1L

                    every { featureFlagGroupJpaRepository.findByIdOrNull(id) } returns null

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(id)

                    // then
                    result shouldBe null
                }

                it("FeatureFlagGroup 엔티티는 있지만 FeatureFlag 엔티티가 없으면 null을 반환한다") {
                    // given
                    val id = 1L
                    val featureFlagId = 2L

                    val featureFlagGroupEntity = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId,
                        specifics = listOf(1, 2, 3),
                        percentage = null,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { this.id = id }

                    every { featureFlagGroupJpaRepository.findByIdOrNull(id) } returns featureFlagGroupEntity
                    every { featureFlagJpaRepository.findByIdOrNull(featureFlagId) } returns null

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(id)

                    // then
                    result shouldBe null
                }
            }

            context("getFeatureFlagGroupOrNullBy(feature) 메서드 호출 시") {
                it("Feature로 FeatureFlagGroup 엔티티를 조회하고 도메인 객체로 변환하여 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING
                    val featureFlagId = 1L
                    val featureFlagGroupId = 2L

                    val featureFlagEntity = FeatureFlagEntity(
                        feature = feature,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagId }

                    val featureFlagGroupEntity = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId,
                        specifics = listOf(1, 2, 3),
                        percentage = null,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagGroupId }

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns featureFlagEntity
                    every { featureFlagGroupJpaRepository.findByFeatureFlagId(featureFlagId) } returns featureFlagGroupEntity

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(feature)

                    // then
                    result?.id shouldBe featureFlagGroupId
                    result?.featureFlag?.id shouldBe featureFlagId
                    result?.featureFlag?.feature shouldBe feature
                }

                it("Feature에 해당하는 FeatureFlag 엔티티가 없으면 null을 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns null

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(feature)

                    // then
                    result shouldBe null
                }

                it("FeatureFlag 엔티티는 있지만 FeatureFlagGroup 엔티티가 없으면 null을 반환한다") {
                    // given
                    val feature = Feature.AI_SCREENING
                    val featureFlagId = 1L

                    val featureFlagEntity = FeatureFlagEntity(
                        feature = feature,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagId }

                    every { featureFlagJpaRepository.findByFeatureIs(feature) } returns featureFlagEntity
                    every { featureFlagGroupJpaRepository.findByFeatureFlagId(featureFlagId) } returns null

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroupOrNullBy(feature)

                    // then
                    result shouldBe null
                }
            }

            context("getFeatureFlagGroups 메서드 호출 시") {
                it("모든 FeatureFlagGroup 엔티티를 조회하고 도메인 객체 목록으로 변환하여 반환한다") {
                    // given
                    val featureFlagId1 = 1L
                    val featureFlagId2 = 2L
                    val featureFlagGroupId1 = 3L
                    val featureFlagGroupId2 = 4L

                    val featureFlagEntity1 = FeatureFlagEntity(
                        feature = Feature.AI_SCREENING,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagId1 }

                    val featureFlagEntity2 = FeatureFlagEntity(
                        feature = Feature.APPLICANT_EVALUATOR,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagId2 }

                    val featureFlagGroupEntity1 = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId1,
                        specifics = listOf(1, 2, 3),
                        percentage = null,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagGroupId1 }

                    val featureFlagGroupEntity2 = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId2,
                        specifics = emptyList(),
                        percentage = 50,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagGroupId2 }

                    every { featureFlagJpaRepository.findAll() } returns listOf(featureFlagEntity1, featureFlagEntity2)
                    every { featureFlagGroupJpaRepository.findAll() } returns listOf(featureFlagGroupEntity1, featureFlagGroupEntity2)

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroups()

                    // then
                    result.size shouldBe 2
                    result[0].id shouldBe featureFlagGroupId1
                    result[0].featureFlag.id shouldBe featureFlagId1
                    result[1].id shouldBe featureFlagGroupId2
                    result[1].featureFlag.id shouldBe featureFlagId2
                }

                it("FeatureFlagGroup 엔티티는 있지만 연결된 FeatureFlag 엔티티가 없는 경우 해당 그룹은 제외한다") {
                    // given
                    val featureFlagId1 = 1L
                    val featureFlagGroupId1 = 3L
                    val featureFlagGroupId2 = 4L

                    val featureFlagEntity1 = FeatureFlagEntity(
                        feature = Feature.AI_SCREENING,
                        status = FeatureFlagStatus.ACTIVE,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagId1 }

                    val featureFlagGroupEntity1 = FeatureFlagGroupEntity(
                        featureFlagId = featureFlagId1,
                        specifics = listOf(1, 2, 3),
                        percentage = null,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagGroupId1 }

                    val featureFlagGroupEntity2 = FeatureFlagGroupEntity(
                        featureFlagId = 999L, // 존재하지 않는 featureFlagId
                        specifics = emptyList(),
                        percentage = 50,
                        absolute = null,
                        algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                        updatedAt = ZonedDateTime.now(),
                        createdAt = ZonedDateTime.now()
                    ).apply { id = featureFlagGroupId2 }

                    every { featureFlagJpaRepository.findAll() } returns listOf(featureFlagEntity1)
                    every { featureFlagGroupJpaRepository.findAll() } returns listOf(featureFlagGroupEntity1, featureFlagGroupEntity2)

                    // when
                    val result = featureFlagAdaptor.getFeatureFlagGroups()

                    // then
                    result.size shouldBe 1
                    result[0].id shouldBe featureFlagGroupId1
                    result[0].featureFlag.id shouldBe featureFlagId1
                }
            }
        }
    }
})
