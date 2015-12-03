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

/*==============================================================*/
/* Table : CATEGORIE                                            */
/*==============================================================*/
create table CATEGORIE
(
   ID_CATEGORIE         int not null,
   NOM_CATEGORIE        longtext not null,
   primary key (ID_CATEGORIE)
);

/*==============================================================*/
/* Table : POSSEDE                                              */
/*==============================================================*/
create table POSSEDE
(
   ID_TAG               int not null,
   REF_PRODUIT          longtext not null,
   primary key (ID_TAG, REF_PRODUIT)
);

/*==============================================================*/
/* Table : PRODUIT                                              */
/*==============================================================*/
create table PRODUIT
(
   REF_PRODUIT          longtext not null,
   ID_CATEGORIE         int not null,
   NOM_PRODUIT          longtext not null,
   DATE_SORTIE          timestamp,
   primary key (REF_PRODUIT)
);

/*==============================================================*/
/* Table : TAGS                                                 */
/*==============================================================*/
create table TAGS
(
   ID_TAG               int not null,
   LABEL_TAG            longtext not null,
   primary key (ID_TAG)
);

/*==============================================================*/
/* Table : VENTE                                                */
/*==============================================================*/
create table VENTE
(
   ID                   int not null,
   REF_PRODUIT          longtext not null,
   PRIX                 decimal(9,2) not null,
   QTE                  int not null,
   PROVIDER             varchar(255) not null,
   ACHETEUR             varchar(255) not null,
   primary key (ID)
);

create table AVIS (
  NOM_EMETTEUR longtext not null,
  NOM_DESTINATAIRE longtext not null,
  AVIS int not null,
  PRIMARY KEY (NOM_EMETTEUR, NOM_DESTINATAIRE)
);