--liquibase formatted sql

--changeset sprint4:demande-qr-token labels:sprint4 comment:Token public (QR) par demande
ALTER TABLE demande
    ADD COLUMN IF NOT EXISTS qr_token UUID;

UPDATE demande
SET qr_token = (
    substr(md5(random()::text || clock_timestamp()::text), 1, 8) || '-' ||
    substr(md5(random()::text || clock_timestamp()::text), 9, 4) || '-' ||
    substr(md5(random()::text || clock_timestamp()::text), 13, 4) || '-' ||
    substr(md5(random()::text || clock_timestamp()::text), 17, 4) || '-' ||
    substr(md5(random()::text || clock_timestamp()::text), 21, 12)
)::uuid
WHERE qr_token IS NULL;

ALTER TABLE demande
    ALTER COLUMN qr_token SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_demande_qr_token ON demande(qr_token);
