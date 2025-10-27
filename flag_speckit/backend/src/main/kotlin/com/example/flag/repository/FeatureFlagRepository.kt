package com.example.flag.repository

import com.example.flag.domain.FeatureFlag
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FeatureFlagRepository : JpaRepository<FeatureFlag, UUID> {
    fun findByName(name: String): FeatureFlag?
}
