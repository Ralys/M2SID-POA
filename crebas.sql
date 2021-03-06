/*==============================================================*/
/* Nom de SGBD :  MySQL 5.0                                     */
/* Date de création :  01/12/2015 09:57:26                      */
/*==============================================================*/


drop table if exists CATEGORIE;

drop table if exists POSSEDE;

drop table if exists PRODUIT;

drop table if exists TAGS;

drop table if exists VENTE;

drop table if exists AVIS;

drop table if exists FOURNISSEUR_PRODUIT;

drop table if exists SOLDE;

drop table if exists VENDEUR;

drop table if exists PRIX_FOURNISSEUR;

drop table if exists STOCK;

drop table if exists NEGOCIATION;




/*==============================================================*/
/* Table : PRIX_FOURNISSEUR                                     */
/*==============================================================*/
create table PRIX_FOURNISSEUR
(
  REF_PRODUIT          varchar(255) not null,
  ID_FOURNISSEUR     int not null,
  PRIX_UNITAIRE        float(9,2) not null,
  primary key (REF_PRODUIT, ID_FOURNISSEUR)
);



/*==============================================================*/
/* Table : STOCK                                                */
/*==============================================================*/
create table STOCK
(
  REF_PRODUIT          varchar(255) not null,
  VENDEUR_NAME         varchar(255) not null,
  PRIX_UNITAIRE        float(9,2) not null,
  PRIX_LIMITE        float(9,2) not null,
  QTE                  int not null,
  primary key (REF_PRODUIT, VENDEUR_NAME)
);


/*==============================================================*/
/* Table : VENDEUR                                              */
/*==============================================================*/
create table VENDEUR
(
  VENDEUR_NAME         varchar(255) not null,
  primary key (VENDEUR_NAME)
);

/*==============================================================*/
/* Table : SOLDE                                                */
/*==============================================================*/
create table SOLDE
(
  ID                   int not null,
  VENDEUR              varchar(255) not null,
  DATE_START          bigint,
  DATE_END          bigint,
  primary key (ID)
);

/*==============================================================*/
/* Table : CATEGORIE                                            */
/*==============================================================*/
create table CATEGORIE
(
  ID_CATEGORIE         int not null,
  NOM_CATEGORIE        varchar(255) not null,
  primary key (ID_CATEGORIE)
);

/*==============================================================*/
/* Table : POSSEDE                                              */
/*==============================================================*/
create table POSSEDE
(
  ID_TAG               int not null,
  REF_PRODUIT          varchar(255) not null,
  primary key (ID_TAG, REF_PRODUIT)
);

/*==============================================================*/
/* Table : PRODUIT                                              */
/*==============================================================*/
create table PRODUIT
(
  REF_PRODUIT          varchar(255) not null,
  ID_CATEGORIE         int not null,
  NOM_PRODUIT          varchar(255) not null,
  DATE_SORTIE          bigint,
  PRIX_CREATION        float not null,
  primary key (REF_PRODUIT)
);

/*==============================================================*/
/* Table : TAGS                                                 */
/*==============================================================*/
create table TAGS
(
  ID_TAG               int not null,
  LABEL_TAG            varchar(255) not null,
  primary key (ID_TAG)
);

/*==============================================================*/
/* Table : VENTE                                                */
/*==============================================================*/
create table VENTE
(
  ID                   INTEGER PRIMARY KEY AUTOINCREMENT ,
  REF_PRODUIT          varchar(255) not null,
  PRIX                 decimal(9,2) not null,
  QTE                  int not null,
  PROVIDER             varchar(255) not null,
  ACHETEUR             varchar(255) not null,
  DATE_VENTE           BIGINT
);

create table AVIS (
  ID_AVIS INTEGER PRIMARY KEY AUTOINCREMENT,
  NOM_EMETTEUR varchar(255) not null,
  NOM_DESTINATAIRE varchar(255) not null,
  AVIS int not null
);

create table FOURNISSEUR_PRODUIT
(
  REF_PRODUIT          varchar(255) not null,
  ID_FOURNISSEUR       int not null,
  primary key (REF_PRODUIT,ID_FOURNISSEUR)
);

create table NEGOCIATION
(
  ID_NEGOCIATION       INTEGER PRIMARY KEY,
  COMPORTEMENT_CLIENT  varchar(255) not null,
  SUCCESS              int not null,
  NB_NEGOCIATIONS      int not null
);

INSERT INTO `produit` (`REF_PRODUIT`, `ID_CATEGORIE`, `NOM_PRODUIT`,`DATE_SORTIE`,`PRIX_CREATION`) VALUES
  (1, 1, 'Le roi lion',strftime('%s', 'now')+0,5.5),
  (2, 1, 'La ligne verte',strftime('%s', 'now')+0,7.5),
  (3, 1, 'Forest gump',strftime('%s', 'now')+0,8.5),
  (4, 1, 'Les visiteurs',strftime('%s', 'now')+0,6.5),
  (5, 1, 'Dikkenek',strftime('%s', 'now')+70,5.5),
  (6, 1, 'Thor : Ragnarok',strftime('%s', 'now')+140,9.5),
  (7, 1, 'Deadpool',strftime('%s', 'now')+170,10.5),
  (8, 1, 'Captain America : Civil War',strftime('%s', 'now')+0,8.5),
  (9, 1, 'X-men : Apocalypse',strftime('%s', 'now')+0,8.5),
  (10, 1, 'Spectre',strftime('%s', 'now')+0,9.5),
  (11, 2, 'CS:GO',strftime('%s', 'now')+0,14.9),
  (12, 2, 'Overwatch',strftime('%s', 'now')+0,40.0),
  (13, 2, 'Hearthstone',strftime('%s', 'now')+0,2.9),
  (14, 2, 'Heroes of the Storm',strftime('%s', 'now')+0,15.5),
  (15, 2, 'GTA 5',strftime('%s', 'now')+0,40.5),
  (16, 2, 'LOL',strftime('%s', 'now')+0,20.5),
  (17, 2, 'Call of Duty',strftime('%s', 'now')+0,50.0),
  (18, 2, 'Star Wars : Battlefront',strftime('%s', 'now')+0,40.0),
  (19, 2, 'The Last of Us',strftime('%s', 'now')+60,35.5),
  (20, 2, 'Fallout 4',strftime('%s', 'now')+120,40.5),
  (21, 3, 'Iphone 5',strftime('%s', 'now')+0,300.5),
  (22, 3, 'Nexus 5',strftime('%s', 'now')+0,200.0),
  (23, 3, 'Sony Xperia',strftime('%s', 'now')+0,150.0),
  (24, 3, 'Nexus 6',strftime('%s', 'now')+0,400.0),
  (25, 3, 'Iphone 6',strftime('%s', 'now')+0,500.0),
  (26, 3, 'HTC One',strftime('%s', 'now')+0,200.0),
  (27, 3, 'Archos 50 Diamond',strftime('%s', 'now')+0,200.0),
  (28, 3, 'LG G4',strftime('%s', 'now')+0,250.0),
  (29, 3, 'Nexus 7',strftime('%s', 'now')+100,400.5),
  (30, 3, 'Iphone 7',strftime('%s', 'now')+110,400.5),
  (31, 4, 'Vivitar V8119 BLANC',strftime('%s', 'now')+0,50.0),
  (32, 4, 'Monster High 91048',strftime('%s', 'now')+0,20.5),
  (33, 4, 'Monster High 46048',strftime('%s', 'now')+0,40.5),
  (34, 4, 'Lexibook MINION 1.3MP',strftime('%s', 'now')+0,30.5),
  (35, 4, 'Canon SX710 HS RED',strftime('%s', 'now')+0,15.5),
  (36, 4, 'Nikon COOLPIX L21 NOIR',strftime('%s', 'now')+0,30.5),
  (37, 4, 'Kodak FZ51 NOIR',strftime('%s', 'now')+0,40.5),
  (38, 4, 'Canon IXUS 160 NOIr',strftime('%s', 'now')+0,30.5),
  (39, 4, 'Vivitar VS425 BLEU',strftime('%s', 'now')+10,25.5),
  (40, 4, 'Olympus TG 860 Blanc',strftime('%s', 'now')+20,21.5),
  (41, 5, 'Johnny Halliday : De l Amour',strftime('%s', 'now')+0,5.5),
  (42, 5, 'Kendji Girac - Ensemble',strftime('%s', 'now')+0,8.5),
  (43, 5, 'Mylene Farmer : Interstellaires',strftime('%s', 'now')+0,10.5),
  (44, 5, 'One Direction : Made in the A.M',strftime('%s', 'now')+0,6.5),
  (45, 5, 'SCH : A7',strftime('%s', 'now')+0,7.5),
  (46, 5, 'Justin Bieber : Purpose',strftime('%s', 'now')+0,1.5),
  (47, 5, 'Louane : Chambre 12',strftime('%s', 'now')+0,10.5),
  (48, 5, 'ZAZ : Sur la route',strftime('%s', 'now')+0,9.5),
  (49, 5, 'Marina Kaye : Fearless',strftime('%s', 'now')+50,5.5),
  (50, 5, 'Etienne Daho : L homme qui marche',strftime('%s', 'now')+70,6.5);

INSERT INTO `solde` (`ID`,`VENDEUR`,`DATE_START`,`DATE_END`) VALUES
  (1, 'trinity', strftime('%s', 'now'), strftime('%s', 'now')+86400*3);

INSERT INTO `stock` (`REF_PRODUIT`,`VENDEUR_NAME`,`PRIX_UNITAIRE`,`PRIX_LIMITE`,`QTE`) VALUES
  (1, 'w',7,5.5,3),
  (2, 'w',8,5.5,0),
  (2, 'trinity',8,5.5,2);

INSERT INTO `categorie` (`ID_CATEGORIE`, `NOM_CATEGORIE`) VALUES
  (1, 'DVD'),
  (2, 'Jeux-vidéo'),
  (3, 'Téléphone'),
  (4, 'Appareil photo'),
  (5, 'CD');

INSERT INTO `possede` (`ID_TAG`,`REF_PRODUIT`) VALUES
(1,2),
(1,3),
(1,6),
(1,10),
(1,17),
(1,18),
(2,2),
(2,11),
(2,12),
(2,17),
(2,18),
(3,1),
(4,19),
(4,20),
(5,13),
(6,14),
(6,16),
(7,21),
(7,25),
(7,30),
(8,22),
(8,24),
(8,29),
(9,43),
(9,50),
(10,41),
(10,42),
(10,43),
(10,48),
(10,47),
(10,49),
(10,50);

INSERT INTO `tags` (`ID_TAG`,`LABEL_TAG`) VALUES
  (1, "action"),
  (2, "tir"),
  (3, "anime"),
  (4, "aventure"),
  (5, "TCG"),
  (6, "MOBA"),
  (7, "apple"),
  (8, "google"),
  (9, "Classique"),
  (10, "Musique française");

INSERT INTO `FOURNISSEUR_PRODUIT` (`ID_FOURNISSEUR`, `REF_PRODUIT`) VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (1, 4),
  (1, 5),
  (1, 6),
  (1, 7),
  (1, 8),
  (1, 9),
  (1, 10),
  (1, 11),
  (1, 12),
  (1, 13),
  (1, 14),
  (1, 15),
  (1, 16),
  (1, 17),
  (1, 18),
  (1, 19),
  (1, 20),
  (1, 21),
  (1, 22),
  (1, 23),
  (1, 24),
  (1, 25),
  (1, 26),
  (1, 27),
  (1, 28),
  (1, 29),
  (1, 30),
  (1, 41),
  (1, 42),
  (1, 43),
  (1, 44),
  (1, 45),
  (1, 46),
  (1, 47),
  (1, 48),
  (1, 49),
  (1, 50),
  (2, 1),
  (2, 2),
  (2, 3),
  (2, 4),
  (2, 5),
  (2, 6),
  (2, 7),
  (2, 8),
  (2, 9),
  (2, 10),
  (2, 31),
  (2, 32),
  (2, 33),
  (2, 34),
  (2, 35),
  (2, 36),
  (2, 37),
  (2, 38),
  (2, 39),
  (2, 40),
  (2, 21),
  (2, 22),
  (2, 23),
  (2, 24),
  (2, 25),
  (2, 26),
  (2, 27),
  (2, 28),
  (2, 29),
  (2, 30),
  (2, 41),
  (2, 42),
  (2, 43),
  (2, 44),
  (2, 45),
  (2, 46),
  (2, 47),
  (2, 48),
  (2, 49),
  (2, 50),
  (3, 1),
  (3, 2),
  (3, 3),
  (3, 4),
  (3, 5),
  (3, 6),
  (3, 7),
  (3, 8),
  (3, 9),
  (3, 10),
  (3, 31),
  (3, 32),
  (3, 33),
  (3, 34),
  (3, 35),
  (3, 36),
  (3, 37),
  (3, 38),
  (3, 39),
  (3, 30),
  (3, 11),
  (3, 12),
  (3, 13),
  (3, 14),
  (3, 15),
  (3, 16),
  (3, 17),
  (3, 18),
  (3, 19),
  (3, 20),
  (3, 41),
  (3, 42),
  (3, 43),
  (3, 44),
  (3, 45),
  (3, 46),
  (3, 47),
  (3, 48),
  (3, 49),
  (3, 50);

INSERT INTO `AVIS` (`NOM_EMETTEUR`, `NOM_DESTINATAIRE`, `AVIS`) VALUES
("Bernard","Produit_0",2),
("Julien","Produit_0",2),
("Éléanore","Produit_1",4),
("Maurice","Produit_1",4),
("Paul","Produit_1",2),
("Éric","Produit_2",0),
("Sophie","Produit_2",2),
("Paul","Produit_3",2),
("Victor","Produit_3",4),
("Victor","Produit_3",4),
("Julien","Produit_3",3),
("Paul","Produit_4",4),
("Paul","Produit_5",4),
("Sophie","Produit_5",4),
("Jack","Produit_5",0),
("Lorenzo","Produit_5",2),
("Patrick","Produit_5",3),
("Patrick","Produit_6",1),
("Julien","Produit_6",3),
("Lorenzo","Produit_6",4),
("Xavier","Produit_6",0),
("Éléanore","Produit_7",1),
("Paul","Produit_8",0),
("Dominique","Produit_8",1),
("Paul","Produit_8",0),
("Paul","Produit_9",4),
("Jack","Produit_9",4),
("Dominique","Produit_9",4),
("Xavier","Produit_9",0),
("Christian","Produit_9",1),
("Lorenzo","Produit_11",0),
("Bruno","Produit_11",0),
("Tristan","Produit_11",2),
("Bernard","Produit_11",4),
("Xavier","Produit_12",4),
("Dominique","Produit_12",4),
("Victor","Produit_13",1),
("Tristan","Produit_13",0),
("Fabrice","Produit_14",3),
("Éléanore","Produit_15",1),
("Christian","Produit_15",1),
("Christian","Produit_15",2),
("Lorenzo","Produit_15",3),
("Didier","Produit_16",2),
("Lorenzo","Produit_16",3),
("Christian","Produit_17",0),
("Maurice","Produit_18",4),
("Sophie","Produit_18",0),
("Lorenzo","Produit_18",2),
("Didier","Produit_18",2),
("Sophie","Produit_19",3),
("Didier","Produit_19",0),
("Fabrice","Produit_19",3),
("Maurice","Produit_20",3),
("Maurice","Produit_21",1),
("Jack","Produit_21",0),
("Fabrice","Produit_21",3),
("Maurice","Produit_22",1),
("Bruno","Produit_22",3),
("Bruno","Produit_22",3),
("Dominique","Produit_23",3),
("Sophie","Produit_24",3),
("Victor","Produit_24",4),
("Paul","Produit_25",0),
("Fabrice","Produit_26",3),
("Christian","Produit_26",4),
("Didier","Produit_26",4),
("Dominique","Produit_26",3),
("Éléanore","Produit_27",3),
("Patrick","Produit_27",3),
("Julien","Produit_27",0),
("Bernard","Produit_27",4),
("Éric","Produit_27",2),
("Éléanore","Produit_28",4),
("Sophie","Produit_28",1),
("Fabrice","Produit_28",1),
("Tristan","Produit_29",4),
("Patrick","Produit_29",1),
("Maurice","Produit_30",4),
("Didier","Produit_31",4),
("Tristan","Produit_32",3),
("Bruno","Produit_33",1),
("Julien","Produit_33",1),
("Fabrice","Produit_33",4),
("Jack","Produit_33",4),
("Patrick","Produit_34",2),
("Didier","Produit_34",3),
("Victor","Produit_34",4),
("Éléanore","Produit_35",3),
("Fabrice","Produit_36",1),
("Lorenzo","Produit_36",4),
("Bernard","Produit_36",3),
("Tristan","Produit_37",1),
("Didier","Produit_37",4),
("Maurice","Produit_37",3),
("Patrick","Produit_38",0),
("Julien","Produit_38",3),
("Didier","Produit_39",4),
("Christian","Produit_39",4),
("Fabrice","Produit_40",4),
("Maurice","Produit_40",1),
("Xavier","Produit_41",3),
("Didier","Produit_41",2),
("Dominique","Produit_42",1),
("Paul","Produit_43",1),
("Victor","Produit_44",4),
("Victor","Produit_44",3),
("Lorenzo","Produit_45",3),
("Didier","Produit_46",3),
("Fabrice","Produit_46",1),
("Maurice","Produit_47",0),
("Bruno","Produit_48",4),
("Patrick","Produit_48",0),
("Éric","Produit_49",4),
("Didier","Produit_49",1),
("Dominique","Produit_49",3);
