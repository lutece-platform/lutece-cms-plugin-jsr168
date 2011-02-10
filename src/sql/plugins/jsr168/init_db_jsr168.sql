-- Table structure for table 'portlet_jsr168'

CREATE TABLE portlet_jsr168 (
  id_portlet INT DEFAULT '0' NOT NULL,
  jsr168Name varchar(100) NOT NULL,
  PRIMARY KEY (id_portlet),
);

CREATE INDEX index_portlet_jsr168 ON portlet_jsr168 (id_portlet);