SET SERVEROUTPUT ON;

CREATE OR REPLACE TRIGGER SERIAL_N_CAR
AFTER INSERT ON CAR
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo CAR con SERIAL_N: ' || :NEW.SERIAL_N);
END;
/

CREATE OR REPLACE TRIGGER SERIAL_N_CAR_MATERIAL
AFTER INSERT ON CAR_MATERIAL
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo CAR_MATERIAL con SERIAL_N: ' || :NEW.SERIAL_N);
END;
/

CREATE OR REPLACE TRIGGER CIF_CLIENT_CLIENT
AFTER INSERT ON CLIENT
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo CLIENT con CIF_CLIENT: ' || :NEW.CIF_CLIENT);
END;
/

CREATE OR REPLACE TRIGGER N_CONTRATO_CONTRATO
AFTER INSERT ON CONTRATO
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo CONTRATO con N_CONTRATO: ' || :NEW.N_CONTRATO);
END;
/

CREATE OR REPLACE TRIGGER N_EMPLEADO_EMPLEADO
AFTER INSERT ON EMPLEADO
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo EMPLEADO con N_EMPLEADO: ' || :NEW.N_EMPLEADO);
END;
/

CREATE OR REPLACE TRIGGER ID_GASTO_GASTO
AFTER INSERT ON GASTO
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo GASTO con ID_GASTO: ' || :NEW.ID_GASTO);
END;
/

CREATE OR REPLACE TRIGGER INGRESO_ID_INGRESO
AFTER INSERT ON INGRESO
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo INGRESO con INGRESO_ID: ' || :NEW.INGRESO_ID);
END;
/

CREATE OR REPLACE TRIGGER MATERIAL_ID_MATERIAL
AFTER INSERT ON MATERIAL
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo MATERIAL con MATERIAL_ID: ' || :NEW.MATERIAL_ID);
END;
/

CREATE OR REPLACE TRIGGER ORDER_ID_ORDER_
AFTER INSERT ON ORDER_
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo ORDER_ con ORDER_ID: ' || :NEW.ORDER_ID);
END;
/

CREATE OR REPLACE TRIGGER PURCHASE_ID_PURCHASE
AFTER INSERT ON PURCHASE
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo PURCHASE con PURCHASE_ID: ' || :NEW.PURCHASE_ID);
END;
/

CREATE OR REPLACE TRIGGER ORDER_ID_SUPPLIER
AFTER INSERT ON SUPPLIER
FOR EACH ROW
BEGIN
   DBMS_OUTPUT.PUT_LINE('Se ha creado un nuevo SUPPLIER con CIF_SUPP: ' || :NEW.CIF_SUPP);
END;
/