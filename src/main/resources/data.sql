-- Este script se ejecuta al iniciar si la tabla está vacía
INSERT INTO usuarios (username, password, rol, activo)
SELECT 'admin', 'admin123', 'ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');