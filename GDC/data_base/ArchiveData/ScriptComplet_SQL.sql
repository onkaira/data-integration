
-- informations site Insee (possiblement en rdf)

drop table cog;
create table cog (
cdc varchar2(1), cl varchar2(1), codeReg varchar2(5), codeDep varchar2(3), codeCommune varchar2(3), ar varchar2(3), ct varchar2(3), tncc varchar2(1), artmaj varchar2(5), ncc varchar2(50), artmin varchar2(5), nccenr varchar2(50));


--cdc redecoupage de la commune en canton valeurs possibles 0 (non decoupe), 1 (fraction cantonale), 2 (non precisé)
-- chef lieu : commune est chef lieu de region, dept, arrondissement, canton, valeurs possibles 0 (non chef lieu), 
-- 1 (canton), 2 (arrondissement), 3 (departement), 4 (region)
-- code region
-- code departement
-- code commune 
-- ar arrrondissement 
-- ct canton
-- tncc typologie de nom de commune en clair
-- artmaj article majuscule
-- ncc nom commune en clair
-- artmin article minuscule
-- nccenr nom commune en clair typologie riche

---> script de chargement cog_ctl.txt et fichier de donnees cog.csv
-- sqlldr user/user@venus/master.info control=cog_ctl.txt

drop table cog_r ;
create table cog_r as select codeDep||codeCommune as codeInsee, cdc,  cl, codeReg, codeDep, codeCommune, ar, ct, artmaj, ncc, nccenr from cog;

alter table cog_r add constraint cog_r_pk primary key (codeInsee);


drop table region;
create table region (
region varchar2(5) constraint region_pk primary key, cheflieu varchar2(5), tncc varchar2(1), ncc varchar2(30), nccenr varchar2(30));

---> script de chargement region_ctl.txt et fichier de donnees region.csv
-- sqlldr user/user@venus/master.info control=region_ctl.txt

drop table departement;
create table departement (
region varchar2(5), departement varchar2(3) constraint departement_pk primary key, cheflieu varchar2(5), tncc varchar2(1), ncc varchar2(30), nccenr varchar2(30));

---> script de chargement departement_ctl.txt et fichier de donnees departement.csv
-- sqlldr user/user@venus/master.info control=departement_ctl.txt


drop table arrondissement_municipal;
create table arrondissement_municipal (codeInsee varchar2(5) constraint am_pk primary key, narm varchar2(50), codeCommune varchar2(5));

insert into arrondissement_municipal values ('75101', 'Paris 1er' ,'75056');
insert into arrondissement_municipal values ('75102', 'Paris 2nd' ,'75056');
insert into arrondissement_municipal values ('75103', 'Paris 3eme' ,'75056');
insert into arrondissement_municipal values ('75104', 'Paris 4eme' ,'75056');
insert into arrondissement_municipal values ('75105', 'Paris 5eme' ,'75056');
insert into arrondissement_municipal values ('75106', 'Paris 6eme' ,'75056');
insert into arrondissement_municipal values ('75107', 'Paris 7eme' ,'75056');
insert into arrondissement_municipal values ('75108', 'Paris 8eme' ,'75056');
insert into arrondissement_municipal values ('75109', 'Paris 9eme' ,'75056');
insert into arrondissement_municipal values ('75110', 'Paris 10eme' ,'75056');
insert into arrondissement_municipal values ('75111', 'Paris 11eme' ,'75056');
insert into arrondissement_municipal values ('75112', 'Paris 12eme' ,'75056');
insert into arrondissement_municipal values ('75113', 'Paris 13eme' ,'75056');
insert into arrondissement_municipal values ('75114', 'Paris 14eme' ,'75056');
insert into arrondissement_municipal values ('75115', 'Paris 15eme' ,'75056');
insert into arrondissement_municipal values ('75116', 'Paris 16eme' ,'75056');
insert into arrondissement_municipal values ('75117', 'Paris 17eme' ,'75056');
insert into arrondissement_municipal values ('75118', 'Paris 18eme' ,'75056');
insert into arrondissement_municipal values ('75119', 'Paris 19eme' ,'75056');
insert into arrondissement_municipal values ('75120', 'Paris 20eme' ,'75056');

insert into arrondissement_municipal values ('69381', 'Lyon 1er' ,'69123');
insert into arrondissement_municipal values ('69382', 'Lyon 2nd' ,'69123');
insert into arrondissement_municipal values ('69383', 'Lyon 3eme' ,'69123');
insert into arrondissement_municipal values ('69384', 'Lyon 4eme' ,'69123');
insert into arrondissement_municipal values ('69385', 'Lyon 5eme' ,'69123');
insert into arrondissement_municipal values ('69386', 'Lyon 6eme' ,'69123');
insert into arrondissement_municipal values ('69387', 'Lyon 7eme' ,'69123');
insert into arrondissement_municipal values ('69388', 'Lyon 8eme' ,'69123');
insert into arrondissement_municipal values ('69389', 'Lyon 9eme' ,'69123');

insert into arrondissement_municipal values ('13201', 'Marseille 1er' ,'13055');
insert into arrondissement_municipal values ('13202', 'Marseille 2nd' ,'13055');
insert into arrondissement_municipal values ('13203', 'Marseille 3eme' ,'13055');
insert into arrondissement_municipal values ('13204', 'Marseille 4eme' ,'13055');
insert into arrondissement_municipal values ('13205', 'Marseille 5eme' ,'13055');
insert into arrondissement_municipal values ('13206', 'Marseille 6eme' ,'13055');
insert into arrondissement_municipal values ('13207', 'Marseille 7eme' ,'13055');
insert into arrondissement_municipal values ('13208', 'Marseille 8eme' ,'13055');
insert into arrondissement_municipal values ('13209', 'Marseille 9eme' ,'13055');
insert into arrondissement_municipal values ('13210', 'Marseille 10eme' ,'13055');
insert into arrondissement_municipal values ('13211', 'Marseille 11eme' ,'13055');
insert into arrondissement_municipal values ('13212', 'Marseille 12me' ,'13055');
insert into arrondissement_municipal values ('13213', 'Marseille 13eme' ,'13055');
insert into arrondissement_municipal values ('13214', 'Marseille 14eme' ,'13055');
insert into arrondissement_municipal values ('13215', 'Marseille 15eme' ,'13055');
insert into arrondissement_municipal values ('13216', 'Marseille 16eme' ,'13055');

drop table localite;
create table localite as
select codeinsee, 'commune' as typeLocalite from cog_r
union 
select codeinsee, 'arrondissement_municipal' as typeLocalite from arrondissement_municipal;

 



-- informations site impot du gouvernement 

drop sequence impot_cpt;
create sequence impot_cpt;


drop table impot;
create table IMPOT (CodeImp number, CodeInsee varchar(8), nbreRedevable number, PatrimoineM number, ImpotMoyen number, annee number);

---> script de chargement impot_ctl.txt et fichiers de donnees isf_2002.csv isf_2008.csv etc changer le nom du fichier a charger 
-- dans script de chargement
-- sqlldr user/user@venus/master.info control=impot_ctl.txt



-- exemple ajout annee 
update impot set annee=2002 where annee is null;


-- requete de test avec jointure externe

select l.codeinsee as localite, i.codeinsee as impot from localite l, impot i where i.codeinsee = l.codeinsee(+) and l.codeinsee is null;




