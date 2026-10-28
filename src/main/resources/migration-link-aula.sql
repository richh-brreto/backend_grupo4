-- Execute uma vez em bancos já existentes para habilitar o link compartilhado por contrato.
ALTER TABLE contrato ADD COLUMN link_aula VARCHAR(2048) NULL;
