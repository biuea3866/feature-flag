package com.biuea.feature_flag.domain.feature.entity

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.ZonedDateTime

class FeatureFlagGroupTest : DescribeSpec({
    describe("FeatureFlagGroup 엔티티") {
        val featureFlag = FeatureFlag(
            _id = 1L,
            _feature = Feature.AI_SCREENING,
            _updatedAt = ZonedDateTime.now(),
            _createdAt = ZonedDateTime.now()
        )
        
        context("create 메서드로 생성할 때") {
            context("SPECIFIC 알고리즘 옵션으로 생성 시") {
                it("specifics가 비어있으면 예외가 발생한다") {
                    // when & then
                    shouldThrow<IllegalArgumentException> {
                        FeatureFlagGroup.create(
                            featureFlag = featureFlag,
                            algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                            specifics = emptyList(),
                            absolute = null,
                            percentage = null
                        )
                    }
                }
                
                it("specifics 크기가 MAX_SPECIFIC_SIZE를 초과하면 예외가 발생한다") {
                    // given
                    val specifics = List(FeatureFlagGroup.MAX_SPECIFIC_SIZE + 1) { it }
                    
                    // when & then
                    shouldThrow<IllegalArgumentException> {
                        FeatureFlagGroup.create(
                            featureFlag = featureFlag,
                            algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                            specifics = specifics,
                            absolute = null,
                            percentage = null
                        )
                    }
                }
                
                it("올바른 specifics가 주어지면 FeatureFlagGroup이 생성된다") {
                    // given
                    val specifics = listOf(1, 2, 3)
                    
                    // when
                    val featureFlagGroup = FeatureFlagGroup.create(
                        featureFlag = featureFlag,
                        algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                        specifics = specifics,
                        absolute = null,
                        percentage = null
                    )
                    
                    // then
                    featureFlagGroup.featureFlag shouldBe featureFlag
                    featureFlagGroup.specifics shouldBe specifics
                    featureFlagGroup.absolute shouldBe null
                    featureFlagGroup.percentage shouldBe null
                }
            }
            
            context("PERCENT 알고리즘 옵션으로 생성 시") {
                it("percentage가 null이면 예외가 발생한다") {
                    // when & then
                    shouldThrow<IllegalArgumentException> {
                        FeatureFlagGroup.create(
                            featureFlag = featureFlag,
                            algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                            specifics = emptyList(),
                            absolute = null,
                            percentage = null
                        )
                    }
                }
                
                it("올바른 percentage가 주어지면 FeatureFlagGroup이 생성된다") {
                    // given
                    val percentage = 50
                    
                    // when
                    val featureFlagGroup = FeatureFlagGroup.create(
                        featureFlag = featureFlag,
                        algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                        specifics = emptyList(),
                        absolute = null,
                        percentage = percentage
                    )
                    
                    // then
                    featureFlagGroup.featureFlag shouldBe featureFlag
                    featureFlagGroup.specifics shouldBe emptyList()
                    featureFlagGroup.absolute shouldBe null
                    featureFlagGroup.percentage shouldBe percentage
                }
            }
            
            context("ABSOLUTE 알고리즘 옵션으로 생성 시") {
                it("absolute가 null이면 예외가 발생한다") {
                    // when & then
                    shouldThrow<IllegalArgumentException> {
                        FeatureFlagGroup.create(
                            featureFlag = featureFlag,
                            algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                            specifics = emptyList(),
                            absolute = null,
                            percentage = null
                        )
                    }
                }
                
                it("올바른 absolute가 주어지면 FeatureFlagGroup이 생성된다") {
                    // given
                    val absolute = 100
                    
                    // when
                    val featureFlagGroup = FeatureFlagGroup.create(
                        featureFlag = featureFlag,
                        algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                        specifics = emptyList(),
                        absolute = absolute,
                        percentage = null
                    )
                    
                    // then
                    featureFlagGroup.featureFlag shouldBe featureFlag
                    featureFlagGroup.specifics shouldBe emptyList()
                    featureFlagGroup.absolute shouldBe absolute
                    featureFlagGroup.percentage shouldBe null
                }
            }
        }
        
        context("containsWorkspace 메서드 호출 시") {
            it("알고리즘이 초기화되지 않았으면 예외가 발생한다") {
                // given
                val featureFlagGroup = FeatureFlagGroup(
                    _id = 1L,
                    _featureFlag = featureFlag,
                    _specifics = emptyList(),
                    _percentage = null,
                    _absolute = null,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagGroup.containsWorkspace(1)
                }
            }
            
            it("알고리즘이 초기화되었으면 알고리즘의 isEnable 결과를 반환한다") {
                // given
                val specifics = listOf(1, 2, 3)
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = specifics,
                    absolute = null,
                    percentage = null
                )
                
                // when & then
                featureFlagGroup.containsWorkspace(2) shouldBe true
                featureFlagGroup.containsWorkspace(4) shouldBe false
            }
        }
        
        context("changeAlgorithm 메서드 호출 시") {
            it("알고리즘과 관련 속성이 변경된다") {
                // given
                val initialSpecifics = listOf(1, 2, 3)
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = featureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.SPECIFIC,
                    specifics = initialSpecifics,
                    absolute = null,
                    percentage = null
                )
                
                val newPercentage = 50
                
                // when
                featureFlagGroup.changeAlgorithm(
                    algorithmOption = FeatureFlagAlgorithmOption.PERCENT,
                    specifics = emptyList(),
                    absolute = null,
                    percentage = newPercentage
                )
                
                // then
                featureFlagGroup.specifics shouldBe emptyList()
                featureFlagGroup.percentage shouldBe newPercentage
                featureFlagGroup.absolute shouldBe null
                featureFlagGroup.containsWorkspace(50) shouldBe true
                featureFlagGroup.containsWorkspace(51) shouldBe false
            }
        }
        
        context("isAvailable 메서드 호출 시") {
            it("FeatureFlag가 활성화 상태면 true를 반환한다") {
                // given
                val activeFeatureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = activeFeatureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )
                
                // when & then
                featureFlagGroup.isAvailable() shouldBe true
            }
            
            it("FeatureFlag가 비활성화 상태면 false를 반환한다") {
                // given
                val inactiveFeatureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = inactiveFeatureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )
                
                // when & then
                featureFlagGroup.isAvailable() shouldBe false
            }
        }
        
        context("checkActivation 메서드 호출 시") {
            it("FeatureFlag가 활성화 상태면 예외가 발생하지 않는다") {
                // given
                val activeFeatureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.ACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = activeFeatureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )
                
                // when & then
                featureFlagGroup.checkActivation() // 예외가 발생하지 않아야 함
            }
            
            it("FeatureFlag가 비활성화 상태면 IllegalStateException이 발생한다") {
                // given
                val inactiveFeatureFlag = FeatureFlag(
                    _id = 1L,
                    _feature = Feature.AI_SCREENING,
                    _status = FeatureFlagStatus.INACTIVE,
                    _updatedAt = ZonedDateTime.now(),
                    _createdAt = ZonedDateTime.now()
                )
                
                val featureFlagGroup = FeatureFlagGroup.create(
                    featureFlag = inactiveFeatureFlag,
                    algorithmOption = FeatureFlagAlgorithmOption.ABSOLUTE,
                    specifics = emptyList(),
                    absolute = 100,
                    percentage = null
                )
                
                // when & then
                shouldThrow<IllegalStateException> {
                    featureFlagGroup.checkActivation()
                }
            }
        }
        
        context("associatedByFeatureFlag 확장 함수 사용 시") {
            it("FeatureFlag를 키로 하는 Map을 반환한다") {
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
                
                // when
                val map = featureFlagGroups.associatedByFeatureFlag()
                
                // then
                map.size shouldBe 2
                map[featureFlag1] shouldBe featureFlagGroup1
                map[featureFlag2] shouldBe featureFlagGroup2
            }
        }
    }
})