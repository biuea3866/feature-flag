package com.example.flag.service

import com.example.flag.domain.AuditLog
import com.example.flag.domain.FeatureFlag
import com.example.flag.repository.AuditLogRepository
import org.springframework.stereotype.Service

@Service
class AuditService(private val auditLogRepository: AuditLogRepository) {

    fun logChange(featureFlag: FeatureFlag, user: String, action: String, details: Map<String, Any?>) {
        val auditLog = AuditLog(
            featureFlag = featureFlag,
            user = user,
            action = action,
            details = details
        )
        auditLogRepository.save(auditLog)
    }
}
