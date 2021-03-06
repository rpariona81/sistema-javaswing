-- -----------------------------------------------------
-- Table categoria
-- -----------------------------------------------------
CREATE TABLE categoria (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(255) NULL,
  activo INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id));

CREATE UNIQUE INDEX nombre_categoria ON categoria (nombre ASC);

INSERT INTO CATEGORIA(NOMBRE,DESCRIPCION) 
VALUES('Vehículos','Accesorios y repuestos para vehículos');
SELECT * FROM CATEGORIA;

-- -----------------------------------------------------
-- Table articulo
-- -----------------------------------------------------
CREATE TABLE articulo (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  categoria_id INT NOT NULL,
  codigo VARCHAR(100) NULL,
  nombre VARCHAR(100) NOT NULL,
  precio_venta DECIMAL(11,2) NOT NULL,
  stock INT NOT NULL,
  descripcion VARCHAR(255) NULL,
  imagen VARCHAR(50) NULL,
  activo INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  CONSTRAINT fk_articulo_categoria
    FOREIGN KEY (categoria_id)
    REFERENCES categoria (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX nombre_articulo ON articulo (nombre ASC);

CREATE INDEX fk_articulo_categoria_idx ON articulo (categoria_id ASC);


-- -----------------------------------------------------
-- Table rol
-- -----------------------------------------------------
CREATE TABLE rol (
  id INT NOT NULL GENERATED ALWAYS AS IDENTITY ,
  nombre VARCHAR(20) NOT NULL,
  descripcion VARCHAR(255) NULL,
  PRIMARY KEY (id));

CREATE UNIQUE INDEX nombre_rol ON rol (nombre ASC);


-- -----------------------------------------------------
-- Table usuario
-- -----------------------------------------------------
CREATE TABLE usuario (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  rol_id INT NOT NULL,
  nombre VARCHAR(70) NOT NULL,
  tipo_documento VARCHAR(20) NULL,
  num_documento VARCHAR(20) NULL,
  direccion VARCHAR(70) NULL,
  telefono VARCHAR(15) NULL,
  email VARCHAR(50) NOT NULL,
  clave VARCHAR(128) NOT NULL,
  activo INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  CONSTRAINT fk_usuario_rol
    FOREIGN KEY (rol_id)
    REFERENCES rol (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX nombre_usuario ON usuario (nombre ASC);

CREATE UNIQUE INDEX email_usuario ON usuario (email ASC);

CREATE INDEX fk_usuario_rol_idx ON usuario (rol_id ASC);


-- -----------------------------------------------------
-- Table persona
-- -----------------------------------------------------
CREATE TABLE persona (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  tipo_persona VARCHAR(20) NOT NULL,
  nombre VARCHAR(70) NOT NULL,
  tipo_documento VARCHAR(20) NULL,
  num_documento VARCHAR(20) NULL,
  direccion VARCHAR(70) NULL,
  telefono VARCHAR(15) NULL,
  email VARCHAR(50) NULL,
  activo INT NOT NULL DEFAULT 1,
  PRIMARY KEY (id));

CREATE UNIQUE INDEX nombre_PERSONA ON persona (nombre ASC);

CREATE UNIQUE INDEX email_PERSONA ON persona (email ASC);


-- -----------------------------------------------------
-- Table ingreso
-- -----------------------------------------------------
CREATE TABLE ingreso (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ,
  persona_id INT NOT NULL,
  usuario_id INT NOT NULL,
  tipo_comprobante VARCHAR(20) NOT NULL,
  serie_comprobante VARCHAR(7) NULL,
  num_comprobante VARCHAR(10) NOT NULL,
  fecha TIMESTAMP,
  impuesto DECIMAL(4,2) NOT NULL,
  total DECIMAL(11,2) NOT NULL,
  estado VARCHAR(20),
  PRIMARY KEY (id),
  CONSTRAINT fk_ingreso_persona
    FOREIGN KEY (persona_id)
    REFERENCES persona (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT fk_ingreso_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE INDEX fk_ingreso_persona_idx ON ingreso (persona_id ASC);

CREATE INDEX fk_ingreso_usuario_idx ON ingreso (usuario_id ASC);


-- -----------------------------------------------------
-- Table detalle_ingreso
-- -----------------------------------------------------
CREATE TABLE detalle_ingreso (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  ingreso_id INT NOT NULL,
  articulo_id INT NOT NULL,
  cantidad INT NOT NULL,
  precio DECIMAL(11,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_detalle_ingreso_ingreso
    FOREIGN KEY (ingreso_id)
    REFERENCES ingreso (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT fk_detalle_ingreso_articulo
    FOREIGN KEY (articulo_id)
    REFERENCES articulo (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE INDEX fk_detalle_ingreso_ingreso_idx ON detalle_ingreso (ingreso_id ASC);

CREATE INDEX fk_detalle_ingreso_articulo_idx ON detalle_ingreso (articulo_id ASC);


-- -----------------------------------------------------
-- Table venta
-- -----------------------------------------------------
CREATE TABLE venta (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ,
  persona_id INT NOT NULL,
  usuario_id INT NOT NULL,
  tipo_comprobante VARCHAR(20) NOT NULL,
  serie_comprobante VARCHAR(7) NULL,
  num_comprobante VARCHAR(10) NOT NULL,
  fecha TIMESTAMP,
  impuesto DECIMAL(4,2) NOT NULL,
  total DECIMAL(11,2) NOT NULL,
  estado VARCHAR(20),
  PRIMARY KEY (id),
  CONSTRAINT fk_venta_persona
    FOREIGN KEY (persona_id)
    REFERENCES persona (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT fk_venta_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuario (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE INDEX fk_ingreso_persona_id ON venta (persona_id ASC);

CREATE INDEX fk_venta_usuario_id ON venta (usuario_id ASC);


-- -----------------------------------------------------
-- Table detalle_venta
-- -----------------------------------------------------
CREATE TABLE detalle_venta (
  id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY ,
  venta_id INT NOT NULL,
  articulo_id INT NOT NULL,
  cantidad INT NOT NULL,
  precio DECIMAL(11,2) NOT NULL,
  descuento DECIMAL(11,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_detalle_venta_venta
    FOREIGN KEY (venta_id)
    REFERENCES venta (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT fk_detalle_venta_articulo
    FOREIGN KEY (articulo_id)
    REFERENCES articulo (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE INDEX fk_detalle_venta_venta_id ON detalle_venta (venta_id ASC);

CREATE INDEX fk_detalle_venta_articulo_id ON detalle_venta (articulo_id ASC);


/*trigger para actualizar stock en cada ingreso*/
CREATE TRIGGER tr_updStockIngreso  
AFTER INSERT ON detalle_ingreso 
REFERENCING NEW AS N
FOR EACH ROW 
	UPDATE articulo SET articulo.stock = articulo.stock + N.cantidad
	WHERE articulo.id = N.articulo_id;


/*trigger para actualizar stock en cada ingreso*/
/*aplicado en la tabla DETALLE_INGRESO*/
CREATE TRIGGER tr_updStockIngresoAnular  
AFTER UPDATE ON ingreso 
REFERENCING NEW AS N
FOR EACH ROW 
	UPDATE ARTICULO t
	SET t.stock = t.stock - di.cantidad
	FROM ARTICULO a INNER JOIN DETALLE_INGRESO di
	ON di.ARTICULO_ID = a.ID AND di.INGRESO_ID = N.id


/*trigger para actualizar stock cuando se ANULAR un ingreso*/
/*aplicado en la tabla INGRESO*/
/**   PERO NO FUNCIONA   */
DROP TRIGGER tr_updStockIngresoAnular ;
CREATE TRIGGER tr_updStockIngresoAnular  
AFTER UPDATE ON ingreso 
REFERENCING NEW AS N OLD AS O
FOR EACH ROW 
	UPDATE ARTICULO 
	SET ARTICULO.stock = ARTICULO.stock - di.cantidad
	FROM ARTICULO a JOIN DETALLE_INGRESO di
	ON di.ARTICULO_ID = a.ID AND di.INGRESO_ID = N.ID 
 	WHERE ARTICULO.ID = a.ID

/*query que funciona*/
UPDATE ARTICULO 
	SET ARTICULO.stock = ARTICULO.stock - di.cantidad
	FROM ARTICULO a INNER JOIN DETALLE_INGRESO di
	ON di.ARTICULO_ID = a.ID AND di.INGRESO_ID = 3 WHERE ARTICULO.ID = a.ID 
	