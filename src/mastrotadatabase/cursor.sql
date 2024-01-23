DECLARE @Nombre VARCHAR(100)

DECLARE modelos CURSOR FOR
SELECT NAME FROM CAR

OPEN modelos

FETCH NEXT FROM modelos INTO @Nombre

WHILE @@FETCH_STATUS = 0
BEGIN
    PRINT @Nombre
    FETCH NEXT FROM modelos INTO @Nombre
END

CLOSE modelos
DEALLOCATE modelos