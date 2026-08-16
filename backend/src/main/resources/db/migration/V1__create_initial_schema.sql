CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    role VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL,
    must_change_password BOOLEAN NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);


CREATE TABLE artifacts (
    id BIGSERIAL PRIMARY KEY,

    artifact_code VARCHAR(150) NOT NULL,
    artifact_type VARCHAR(255),
    form_no VARCHAR(255),
    inventory_no VARCHAR(255),
    study_no VARCHAR(255),
    bag_no VARCHAR(255),
    box_no VARCHAR(255),
    depth VARCHAR(255),
    box VARCHAR(255),
    find_location VARCHAR(255),
    locality VARCHAR(255),
    sector VARCHAR(255),

    find_date DATE,
    find_year INTEGER,

    area VARCHAR(255),
    artifact_form VARCHAR(255),
    decoration_type VARCHAR(255),
    paste_structure VARCHAR(255),
    firing VARCHAR(255),
    technique VARCHAR(255),
    temper VARCHAR(255),
    temper_amount VARCHAR(255),
    slip_structure VARCHAR(255),
    angle VARCHAR(255),
    period VARCHAR(255),
    kind VARCHAR(255),
    munsell VARCHAR(255),
    diameter VARCHAR(255),
    weight VARCHAR(255),
    length VARCHAR(255),
    width VARCHAR(255),
    thickness VARCHAR(255),
    drawing_no VARCHAR(255),
    preserved_part VARCHAR(255),
    material VARCHAR(255),
    production_place VARCHAR(255),

    description TEXT,
    bibliography TEXT,

    visibility VARCHAR(30) NOT NULL,

    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    deleted BOOLEAN NOT NULL,
    deleted_at TIMESTAMP,
    deleted_by BIGINT,

    CONSTRAINT uk_artifact_code UNIQUE (artifact_code),

    CONSTRAINT fk_artifacts_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT fk_artifacts_updated_by
        FOREIGN KEY (updated_by)
        REFERENCES users(id),

    CONSTRAINT fk_artifacts_deleted_by
        FOREIGN KEY (deleted_by)
        REFERENCES users(id)
);


CREATE TABLE artifact_photos (
    id BIGSERIAL PRIMARY KEY,

    artifact_id BIGINT NOT NULL,

    photo_no VARCHAR(255),
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    storage_path VARCHAR(1000) NOT NULL,

    uploaded_by BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,

    deleted BOOLEAN NOT NULL,
    deleted_by BIGINT,
    deleted_at TIMESTAMP,

    CONSTRAINT fk_artifact_photos_artifact
        FOREIGN KEY (artifact_id)
        REFERENCES artifacts(id),

    CONSTRAINT fk_artifact_photos_uploaded_by
        FOREIGN KEY (uploaded_by)
        REFERENCES users(id),

    CONSTRAINT fk_artifact_photos_deleted_by
        FOREIGN KEY (deleted_by)
        REFERENCES users(id)
);


CREATE TABLE artifact_change_logs (
    id BIGSERIAL PRIMARY KEY,

    artifact_id BIGINT NOT NULL,
    changed_by BIGINT NOT NULL,

    change_type VARCHAR(255) NOT NULL,
    changed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_artifact_change_logs_artifact
        FOREIGN KEY (artifact_id)
        REFERENCES artifacts(id),

    CONSTRAINT fk_artifact_change_logs_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES users(id)
);


CREATE TABLE artifact_field_changes (
    id BIGSERIAL PRIMARY KEY,

    change_log_id BIGINT NOT NULL,

    field_name VARCHAR(255) NOT NULL,
    old_value TEXT,
    new_value TEXT,

    CONSTRAINT fk_artifact_field_changes_change_log
        FOREIGN KEY (change_log_id)
        REFERENCES artifact_change_logs(id)
);


CREATE INDEX idx_artifacts_created_by
    ON artifacts(created_by);

CREATE INDEX idx_artifacts_updated_by
    ON artifacts(updated_by);

CREATE INDEX idx_artifact_photos_artifact_id
    ON artifact_photos(artifact_id);

CREATE INDEX idx_artifact_change_logs_artifact_id
    ON artifact_change_logs(artifact_id);

CREATE INDEX idx_artifact_field_changes_change_log_id
    ON artifact_field_changes(change_log_id);