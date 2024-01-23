DECLARE
  Nombre VARCHAR2(100);
  CURSOR modelos IS
    SELECT NAME FROM CAR;
BEGIN
  OPEN modelos;
  FETCH modelos INTO Nombre;

  WHILE modelos%FOUND
  LOOP
    DBMS_OUTPUT.PUT_LINE(Nombre);

    FETCH modelos INTO Nombre;
  END LOOP;

  CLOSE modelos;
END;
/
