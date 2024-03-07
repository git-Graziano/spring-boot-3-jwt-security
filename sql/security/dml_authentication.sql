# spn_authority
insert into spn_authority(NAME, DESCRIPTION) values("ADMIN", "Amministratore con privilegi creazione utenti");
insert into spn_authority(NAME, DESCRIPTION) values("EDITOR", "Editore con privilegi di lettura e scrittura");
insert into spn_authority(NAME, DESCRIPTION) values("USER", "Utente con privilegi di sola lettura");

# spn_user
insert into spn_user(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES("ADMIN", "ADMIN", "admin@admin.it", "");