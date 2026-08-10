ALTER TABLE users
    ADD COLUMN oauth_provider VARCHAR(30) NULL,
    ADD COLUMN oauth_provider_id VARCHAR(255) NULL,
    ADD CONSTRAINT uk_users_oauth_identity UNIQUE (oauth_provider, oauth_provider_id);
