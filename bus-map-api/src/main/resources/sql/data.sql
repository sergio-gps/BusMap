-- Semilla inicial (pensada para BD vacía). Ejecutar una sola vez.

-- Tipos de vehículo
INSERT INTO tipos_vehiculo (tipo_id, nombre) VALUES
  (1, 'autobus'),
  (2, 'taxi');

-- Vehículos
INSERT INTO vehiculos (vehiculo_id, tipo_id) VALUES
  (1, 1),
  (2, 1),
  (3, 2);

-- Líneas
INSERT INTO lineas (linea_id, nombre, origen, destino) VALUES
  (1, 'Línea 27', 'Atocha', 'Moncloa'),
  (2, 'Línea 15', 'Sol', 'Legazpi');

-- Info de vehículos
INSERT INTO vehiculos_info (vehiculo_id, matricula, marca_modelo, capacidad, activo) VALUES
  (1, '1234-ABC', 'Mercedes Citaro', 80, TRUE),
  (2, '5678-DEF', 'MAN Lion''s City', 90, TRUE),
  (3, '0001-GHT', 'Toyota Prius', 4, TRUE);

-- Buses por línea (solo buses)
INSERT INTO buses_linea (vehiculo_id, linea_id) VALUES
  (1, 1),
  (2, 2);

-- Localización (POINT(lon, lat))
INSERT INTO localizacion (id, vehiculo_id, coordenadas, updated_at) VALUES
  (1, 1, point '(-3.7038, 40.4168)', now()),
  (2, 2, point '(-3.7080, 40.4200)', now()),
  (3, 3, point '(-3.6900, 40.4100)', now());

-- Histórico de rutas
INSERT INTO historico_rutas (id, vehiculo_id, punto_origen, destino_buscado, fecha_ruta) VALUES
  (1, 3, point '(-3.7000, 40.4100)', 'Aeropuerto', now() - interval '1 day'),
  (2, 1, point '(-3.7100, 40.4150)', 'Moncloa', now() - interval '2 hours');

-- Usuarios
INSERT INTO usuarios (usuario_id, username) VALUES
  (1, 'admin'),
  (2, 'operador'),
  (3, 'conductor1');

-- Seguridad (nota: contraseñas en texto plano a modo demo)
INSERT INTO seguridad (id, password) VALUES
  (1, 'Admin123!'),
  (2, 'Operador123!'),
  (3, 'Conductor123!');

-- Roles
INSERT INTO roles (rol_id, rol_name) VALUES
  (1, 'ADMIN'),
  (2, 'OPERADOR'),
  (3, 'CONDUCTOR');

-- Permisos
INSERT INTO permisos (permiso_id, permiso_name) VALUES
  (1, 'MANAGE_USERS'),
  (2, 'VIEW_VEHICLES'),
  (3, 'EDIT_VEHICLES');

-- Usuarios ↔ Roles
INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES
  (1, 1),
  (2, 2),
  (3, 3);

-- Roles ↔ Permisos
INSERT INTO roles_permisos (rol_id, permiso_id) VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (2, 2),
  (2, 3),
  (3, 2);