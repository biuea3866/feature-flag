package com.example.flag.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
data class Attribute(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne
    val workspace: Workspace,

    val key: String,
    val value: String
)
