package com.biuea.feature_flag.domain.feature.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.ZonedDateTime

class FeatureFlagTest : DescribeSpec({
    describe("FeatureFlag 엔티티") {
        context("create 메서드로 생성할 때") {
            it("올바른 상태로 생성된다") {
                // given
                val feature = Feature.AI_SCREENING
                val status = FeatureFlagStatus.ACTIVE
                
                // when
                val featureFlag = FeatureFlag.create(feature, status)
                
                // then
                featureFlag.feature shouldBe feature
                featureFlag.status shouldBe status
                featureFlag.id shouldBe 0
            }
        }
        
        context("활성화 상태 확인 시") {
            it("ACTIVE 상태면 true를 반환한다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when & then
                featureFlag.isActive() shouldBe true
            }
            
            it("INACTIVE 상태면 false를 반환한다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when & then
                featureFlag.isActive() shouldBe false
            }
        }
        
        context("활성화 상태 검증 시") {
            it("ACTIVE 상태면 예외가 발생하지 않는다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when & then
                featureFlag.checkActivation() // 예외가 발생하지 않아야 함
            }
            
            it("INACTIVE 상태면 IllegalStateException이 발생한다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlag.checkActivation()
                }
            }
        }
        
        context("상태 변경 시") {
            it("activate 메서드는 상태를 ACTIVE로 변경한다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when
                featureFlag.activate()
                
                // then
                featureFlag.status shouldBe FeatureFlagStatus.ACTIVE
            }
            
            it("inactivate 메서드는 상태를 INACTIVE로 변경한다") {
                // given
                val featureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when
                featureFlag.inactivate()
                
                // then
                featureFlag.status shouldBe FeatureFlagStatus.INACTIVE
            }
        }
        
        context("isEnabled 확장 함수 사용 시") {
            it("해당 기능이 활성화된 FeatureFlag가 있으면 true를 반환한다") {
                // given
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _status = FeatureFlagStatus.ACTIVE,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    ),
                    FeatureFlag(
                        _id = 2L,
                        _feature = Feature.APPLICANT_EVALUATOR,
                        _status = FeatureFlagStatus.INACTIVE,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )
                
                // when & then
                featureFlags.isEnabled(Feature.AI_SCREENING) shouldBe true
            }
            
            it("해당 기능이 비활성화된 FeatureFlag만 있으면 false를 반환한다") {
                // given
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _status = FeatureFlagStatus.INACTIVE,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )
                
                // when & then
                featureFlags.isEnabled(Feature.AI_SCREENING) shouldBe false
            }
            
            it("해당 기능의 FeatureFlag가 없으면 false를 반환한다") {
                // given
                val featureFlags = listOf(
                    FeatureFlag(
                        _id = 1L,
                        _feature = Feature.AI_SCREENING,
                        _status = FeatureFlagStatus.ACTIVE,
                        _updatedAt = ZonedDateTime.now(),
                        _createdAt = ZonedDateTime.now()
                    )
                )
                
                // when & then
                featureFlags.isEnabled(Feature.LOOP_INTERVIEW) shouldBe false
            }
        }
    }
})