package com.example.flag.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
data class AuditLog(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "flag_id")
    val featureFlag: FeatureFlag,

    @Column(name = "`user`")
    val user: String,
    val action: String,

    @JdbcTypeCode(SqlTypes.JSON)
    val details: Map<String, Any?>
)