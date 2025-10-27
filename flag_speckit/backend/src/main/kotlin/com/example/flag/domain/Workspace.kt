package com.example.flag.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.util.UUID

@Entity
data class Workspace(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,

    @OneToMany(mappedBy = "workspace", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = Attribute::class)
    var attributes: List<Attribute> = emptyList()
)