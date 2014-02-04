-- Data ecosante
--->for hbase
drop table SANTE_DEP
create table SANTE_DEP( idDep varchar(5), depName varchar(10), nbHopitaux varchar(10), nbPharmacies  varchar(10));
---> script de chargement departement_ctl.txt et fichier de donnees departement.csv
-- sqlldr onk/onk@xe control=sante_ctl.txt

--Data ecosante
--> for tdb
drop table POP_DEP
create table POP_DEP( idDep varchar(5), depName varchar(10), popMoyenne varchar(10), natalite  varchar(10));
---> script de chargement departement_ctl.txt et fichier de donnees departement.csv
-- sqlldr onk/onk@xe control=pop_ctl.txt

-- data villorama
--> for neo4j
drop table NEAR_TO_BIG_CITY
create table NEAR_TO_BIG_CITY(com1 varchar(20), com2 varchar(20), distance number);
insert into NEAR_TO_BIG_CITY values ('Plan-de-Cuques','Marseille',9);
insert into NEAR_TO_BIG_CITY values ('Allauch' , 'Marseille', 9.6);
insert into NEAR_TO_BIG_CITY values ('Septèmes-les-Vallons' ,'Marseille', 11.4);
insert into NEAR_TO_BIG_CITY values ('La Penne-sur-Huveaune' ,'Marseille', 11.4);
insert into NEAR_TO_BIG_CITY values ('Le Rove' ,'Marseille',13);
insert into NEAR_TO_BIG_CITY values ('Les Pennes-Mirabeau' ,'Marseille', 13.17);
insert into NEAR_TO_BIG_CITY values ('Montrouge' ,'Paris', 5);
insert into NEAR_TO_BIG_CITY values ('Gentilly' ,'Paris', 5.1);
insert into NEAR_TO_BIG_CITY values ('Saint-Ouen' ,'Paris', 5.3);
insert into NEAR_TO_BIG_CITY values ('Levallois-Perret' ,'Paris', 5.4);
insert into NEAR_TO_BIG_CITY values ('Le Pré-Saint-Gervais' ,'Paris', 5.4);
insert into NEAR_TO_BIG_CITY values ('Clichy' ,'Paris', 5.5);
insert into NEAR_TO_BIG_CITY values ( 'Malakoff' ,'Paris',5.6);
insert into NEAR_TO_BIG_CITY values ('Le Kremlin-Bicêtre' ,'Paris', 5.6);
insert into NEAR_TO_BIG_CITY values ('Bagnolet' ,'Paris', 5.7);
insert into NEAR_TO_BIG_CITY values ('Vanves' ,'Paris', 5.8);
insert into NEAR_TO_BIG_CITY values ('Villeurbanne' ,'Lyon', 3.1);
insert into NEAR_TO_BIG_CITY values ('Caluire-et-Cuire' ,'Lyon', 4.0);
insert into NEAR_TO_BIG_CITY values ('La Mulatière' ,'Lyon', 4.1);
insert into NEAR_TO_BIG_CITY values ('Sainte-Foy-lès-Lyon' ,'Lyon', 4.1);
insert into NEAR_TO_BIG_CITY values ('Tassin-la-Demi-Lune' ,'Lyon', 4.8);
insert into NEAR_TO_BIG_CITY values ('Écully' ,'Lyon', 5.3);
insert into NEAR_TO_BIG_CITY values ('Champagne-au-Mont-d\'Or' ,'Lyon', 5.6);
insert into NEAR_TO_BIG_CITY values ('Oullins' ,'Lyon', 5.6);
insert into NEAR_TO_BIG_CITY values ('Saint-Fons' ,'Lyon', 5.6);
insert into NEAR_TO_BIG_CITY values ('Bron' ,'Lyon', 6.0);
insert into NEAR_TO_BIG_CITY values ('Le Bouscat' ,'Bordeaux', 3.4);
insert into NEAR_TO_BIG_CITY values ('Talence' ,'Bordeaux', 3.4);
insert into NEAR_TO_BIG_CITY values ('Bègles' ,'Bordeaux', 4.1);
insert into NEAR_TO_BIG_CITY values ('Floirac' ,'Bordeaux', 4.2);
insert into NEAR_TO_BIG_CITY values ('Cenon' ,'Bordeaux', 4.3);
insert into NEAR_TO_BIG_CITY values ('Mérignac' ,'Bordeaux', 5.2);
insert into NEAR_TO_BIG_CITY values ('Pessac' ,'Bordeaux', 5.5);
insert into NEAR_TO_BIG_CITY values ('Bruges' ,'Bordeaux', 5.6);
insert into NEAR_TO_BIG_CITY values ('Lormont' ,'Bordeaux', 6.5);
insert into NEAR_TO_BIG_CITY values ('Villenave-d\'Ornon' ,'Bordeaux', 6.5);
insert into NEAR_TO_BIG_CITY values ('Castelnau-le-Lez' ,'Montpellier', 3.4);
insert into NEAR_TO_BIG_CITY values ('Clapiers' ,'Montpellier', 5.2);
insert into NEAR_TO_BIG_CITY values ('Lattes' ,'Montpellier', 5.4);
insert into NEAR_TO_BIG_CITY values ('Juvignac' ,'Montpellier', 5.4);
insert into NEAR_TO_BIG_CITY values ('Saint-Jean-de-Védas' ,'Montpellier', 5.7);
insert into NEAR_TO_BIG_CITY values ('Jacou' ,'Montpellier', 6.2);
insert into NEAR_TO_BIG_CITY values ('Lavérune' ,'Montpellier', 6.4);
insert into NEAR_TO_BIG_CITY values ('Le Crès' ,'Montpellier', 6.4);
insert into NEAR_TO_BIG_CITY values ('Montferrier-sur-Lez' ,'Montpellier', 6.6);
insert into NEAR_TO_BIG_CITY values ('Grabels' ,'Montpellier', 7.3);

