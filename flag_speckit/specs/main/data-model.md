# Data Model

This document defines the data model for the feature flag system, based on the requirements in `spec.md`.

## Entities

### 1. FeatureFlag

Represents a single, toggleable feature.

**Fields**:
- `id` (UUID, Primary Key): Unique identifier for the flag.
- `name` (String, Unique): A human-readable, unique name for the flag (e.g., `new-checkout-flow`).
- `description` (String): A brief description of what the feature does.
- `enabled` (Boolean): A global kill-switch for the flag. If `false`, all evaluations for this flag will be `false`.
- `created_at` (Timestamp): Timestamp of when the flag was created.
- `updated_at` (Timestamp): Timestamp of the last update.

**Relationships**:
- Has many `Rule`s.

### 2. Rule

A targeting rule that determines if a flag is enabled for a given workspace.

**Fields**:
- `id` (UUID, Primary Key): Unique identifier for the rule.
- `flag_id` (UUID, Foreign Key): The feature flag this rule belongs to.
- `type` (Enum): The type of rule. Can be one of:
    - `GLOBAL_TOGGLE`: A simple on/off for everyone (uses the `enabled` field in `FeatureFlag`).
    - `PERCENTAGE_ROLLOUT`: A percentage-based rollout.
    - `ATTRIBUTE_BASED`: Targeting based on workspace attributes.
- `percentage` (Integer, 0-100): The percentage of workspaces for which the flag is enabled (only for `PERCENTAGE_ROLLOUT` type).
- `attribute_name` (String): The name of the attribute to check (e.g., `plan`).
- `attribute_value` (String): The value of the attribute to match (e.g., `premium`).
- `created_at` (Timestamp): Timestamp of when the rule was created.

**Relationships**:
- Belongs to one `FeatureFlag`.

### 3. Workspace

Represents a customer workspace, the primary entity for flag evaluation.

**Fields**:
- `id` (UUID, Primary Key): Unique identifier for the workspace.
- `name` (String): The name of the workspace.

**Relationships**:
- Has many `Attribute`s.

### 4. Attribute

A key-value pair associated with a workspace, used for targeting.

**Fields**:
- `id` (UUID, Primary Key): Unique identifier for the attribute.
- `workspace_id` (UUID, Foreign Key): The workspace this attribute belongs to.
- `key` (String): The attribute key (e.g., `plan`).
- `value` (String): The attribute value (e.g., `premium`).

**Relationships**:
- Belongs to one `Workspace`.

### 5. AuditLog

Records changes to feature flags for auditing purposes.

**Fields**:
- `id` (UUID, Primary Key): Unique identifier for the log entry.
- `flag_id` (UUID, Foreign Key): The feature flag that was changed.
- `user` (String): The user who made the change.
- `action` (String): The action taken (e.g., `CREATE`, `UPDATE`, `DELETE`).
- `details` (JSONB): A JSON object containing the details of the change.
- `timestamp` (Timestamp): Timestamp of when the change occurred.

**Relationships**:
- Belongs to one `FeatureFlag`.

## ERD (Entity-Relationship Diagram)

```mermaid
erdDiagram
    FeatureFlag ||--o{ Rule : has
    FeatureFlag ||--o{ AuditLog : has
    Workspace ||--o{ Attribute : has

    FeatureFlag {
        UUID id PK
        String name
        String description
        Boolean enabled
    }

    Rule {
        UUID id PK
        UUID flag_id FK
        Enum type
        Integer percentage
        String attribute_name
        String attribute_value
    }

    Workspace {
        UUID id PK
        String name
    }

    Attribute {
        UUID id PK
        UUID workspace_id FK
        String key
        String value
    }

    AuditLog {
        UUID id PK
        UUID flag_id FK
        String user
        String action
        JSONB details
        Timestamp timestamp
    }
```
