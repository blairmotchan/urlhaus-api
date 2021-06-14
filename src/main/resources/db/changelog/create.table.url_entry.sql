--liquibase formatted sql
--changeset blairmotchan:create-url_entry logicalFilePath:create.table.url_entry.sql

DROP SCHEMA IF EXISTS  url_haus;

CREATE SCHEMA IF NOT EXISTS url_haus;

CREATE TABLE url_haus.url_entry
(
    id            INTEGER,
    date_added    TIMESTAMP NOT NULL,
    url           VARCHAR   NOT NULL,
    url_status    VARCHAR   NOT NULL,
    threat        VARCHAR   NOT NULL,
    tags          JSONB     NOT NULL,
    url_haus_link VARCHAR   NOT NULL,
    reporter      VARCHAR   NOT NULL
);

CREATE INDEX url_entry_date_added ON url_haus.url_entry (date_added);
CREATE INDEX url_entry_url ON url_haus.url_entry (url);
CREATE INDEX url_entry_url_status ON url_haus.url_entry (url_status);
CREATE INDEX url_entry_threat ON url_haus.url_entry (threat);
CREATE INDEX url_entry_tags ON url_haus.url_entry (tags);

