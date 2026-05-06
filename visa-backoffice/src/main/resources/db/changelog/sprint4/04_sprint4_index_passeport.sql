--liquibase formatted sql

--changeset sprint4:idx-passeport-numero labels:sprint4 comment:Index sur passeport.numero pour la recherche publique par passeport
CREATE INDEX IF NOT EXISTS idx_passeport_numero ON passeport(numero);
