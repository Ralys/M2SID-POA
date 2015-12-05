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
  ID                   int not null,
  REF_PRODUIT          varchar(255) not null,
  PRIX                 decimal(9,2) not null,
  QTE                  int not null,
  PROVIDER             varchar(255) not null,
  ACHETEUR             varchar(255) not null,
  primary key (ID)
);

create table AVIS (
  NOM_EMETTEUR varchar(255) not null,
  NOM_DESTINATAIRE varchar(255) not null,
  AVIS int not null,
  PRIMARY KEY (NOM_EMETTEUR, NOM_DESTINATAIRE)
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
  (2, 1, 'La ligne verte',strftime('%s', 'now')+0,5.5),
  (3, 1, 'Forest gump',strftime('%s', 'now')+0,5.5),
  (4, 1, 'Les visiteurs',strftime('%s', 'now')+0,5.5),
  (5, 1, 'Dikkenek',strftime('%s', 'now')+70,5.5),
  (6, 1, 'Thor : Ragnarok',strftime('%s', 'now')+140,5.5),
  (7, 1, 'Deadpool',strftime('%s', 'now')+170,5.5),
  (8, 1, 'Captain America : Civil War',strftime('%s', 'now')+0,5.5),
  (9, 1, 'X-men : Apocalypse',strftime('%s', 'now')+0,5.5),
  (10, 1, 'Spectre',strftime('%s', 'now')+0,5.5),
  (11, 2, 'CS:GO',strftime('%s', 'now')+0,5.5),
  (12, 2, 'Overwatch',strftime('%s', 'now')+0,5.5),
  (13, 2, 'Hearthstone',strftime('%s', 'now')+0,5.5),
  (14, 2, 'Heroes of the Storm',strftime('%s', 'now')+0,5.5),
  (15, 2, 'GTA 5',strftime('%s', 'now')+0,5.5),
  (16, 2, 'LOL',strftime('%s', 'now')+0,5.5),
  (17, 2, 'Call of Duty',strftime('%s', 'now')+0,5.5),
  (18, 2, 'Star Wars : Battlefront',strftime('%s', 'now')+0,5.5),
  (19, 2, 'The Last of Us',strftime('%s', 'now')+60,5.5),
  (20, 2, 'Fallout 4',strftime('%s', 'now')+120,5.5),
  (21, 3, 'Iphone 5',strftime('%s', 'now')+0,5.5),
  (22, 3, 'Nexus 5',strftime('%s', 'now')+0,5.5),
  (23, 3, 'Sony Xperia',strftime('%s', 'now')+0,5.5),
  (24, 3, 'Nexus 6',strftime('%s', 'now')+0,5.5),
  (25, 3, 'Iphone 6',strftime('%s', 'now')+0,5.5),
  (26, 3, 'HTC One',strftime('%s', 'now')+0,5.5),
  (27, 3, 'Archos 50 Diamond',strftime('%s', 'now')+0,5.5),
  (28, 3, 'LG G4',strftime('%s', 'now')+0,5.5),
  (29, 3, 'Nexus 7',strftime('%s', 'now')+100,5.5),
  (30, 3, 'Iphone 7',strftime('%s', 'now')+110,5.5),
  (31, 4, 'Vivitar V8119 BLANC',strftime('%s', 'now')+0,5.5),
  (32, 4, 'Monster High 91048',strftime('%s', 'now')+0,5.5),
  (33, 4, 'Monster High 46048',strftime('%s', 'now')+0,5.5),
  (34, 4, 'Lexibook MINION 1.3MP',strftime('%s', 'now')+0,5.5),
  (35, 4, 'Canon SX710 HS RED',strftime('%s', 'now')+0,5.5),
  (36, 4, 'Nikon COOLPIX L21 NOIR',strftime('%s', 'now')+0,5.5),
  (37, 4, 'Kodak FZ51 NOIR',strftime('%s', 'now')+0,5.5),
  (38, 4, 'Canon IXUS 160 NOIr',strftime('%s', 'now')+0,5.5),
  (39, 4, 'Vivitar VS425 BLEU',strftime('%s', 'now')+10,5.5),
  (40, 4, 'Olympus TG 860 Blanc',strftime('%s', 'now')+20,5.5),
  (41, 5, 'Johnny Halliday : De l Amour',strftime('%s', 'now')+0,5.5),
  (42, 5, 'Kendji Girac - Ensemble',strftime('%s', 'now')+0,5.5),
  (43, 5, 'Mylene Farmer : Interstellaires',strftime('%s', 'now')+0,5.5),
  (44, 5, 'One Direction : Made in the A.M',strftime('%s', 'now')+0,5.5),
  (45, 5, 'SCH : A7',strftime('%s', 'now')+0,5.5),
  (46, 5, 'Justin Bieber : Purpose',strftime('%s', 'now')+0,5.5),
  (47, 5, 'Louane : Chambre 12',strftime('%s', 'now')+0,5.5),
  (48, 5, 'ZAZ : Sur la route',strftime('%s', 'now')+0,5.5),
  (49, 5, 'Marina Kaye : Fearless',strftime('%s', 'now')+50,5.5),
  (50, 5, 'Etienne Daho : L homme qui marche',strftime('%s', 'now')+70,5.5);

INSERT INTO `categorie` (`ID_CATEGORIE`, `NOM_CATEGORIE`) VALUES
  (1, 'DVD'),
  (2, 'Jeux-vidéo'),
  (3, 'Téléphone'),
  (4, 'Appareil photo'),
  (5, 'CD');

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