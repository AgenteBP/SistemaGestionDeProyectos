-- ============================================================
-- SCRIPT DE CARGA INICIAL - Sistema de Gestión de Proyectos
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE comments;
TRUNCATE TABLE steps;
TRUNCATE TABLE tasks;
TRUNCATE TABLE project_users;
TRUNCATE TABLE projects;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- USUARIOS (5 usuarios activos)
-- ============================================================
INSERT INTO users (id_user, name, email, active) VALUES
(1, 'Braian Paez',  'braian@gmail.com',  TRUE),
(2, 'María López',   'maria@gmail.com',   TRUE),
(3, 'Juan Pérez',    'juan@gmail.com',    TRUE),
(4, 'Lucía Fernández','lucia@gmail.com',  TRUE),
(5, 'Carlos Ruiz',   'carlos@gmail.com',  TRUE);
(6, 'Ana Martinez',  'ana@gmail.com',   TRUE); 
-- Agregamos un usuario activo para testear pero sin asignar a ningún proyecto

-- ============================================================
-- PROYECTOS (3 proyectos activos)
-- Regla: un solo owner por proyecto
-- ============================================================
INSERT INTO projects (id_project, name_project, description, id_owner, active) VALUES
(1, 'Sistema de Ventas',  'Desarrollo del módulo de ventas y facturación electrónica', 1, TRUE),
(2, 'Sistema de RRHH',    'Gestión de recursos humanos, liquidación de sueldos y legajos', 2, TRUE),
(3, 'Portal Web Empresa', 'Rediseño y desarrollo del sitio web corporativo',             3, TRUE);

-- ============================================================
-- ASIGNACIÓN DE USUARIOS A PROYECTOS (project_users)
-- Regla: un usuario puede estar en más de un proyecto
-- El owner NO se registra en project_users (ya es owner)
-- ============================================================
INSERT INTO project_users (id_project_user, id_project, id_user, active) VALUES
-- Sistema de Ventas (owner: Braian=1) → miembros: María=2, Juan=3
(1, 1, 2, TRUE),
(2, 1, 3, TRUE),
-- Sistema de RRHH (owner: María=2) → miembros: Braian=1, Lucía=4
(3, 2, 1, TRUE),
(4, 2, 4, TRUE),
-- Portal Web Empresa (owner: Juan=3) → miembros: María=2, Carlos=5
(5, 3, 2, TRUE),
(6, 3, 5, TRUE);

-- ============================================================
-- TAREAS
-- Regla: creador y asignado deben pertenecer al proyecto y ser distintos
-- Regla: fechas obligatorias según estado
-- ============================================================

-- ── PROYECTO 1: Sistema de Ventas ──────────────────────────
-- creadores/asignados válidos: Braian(1), María(2), Juan(3)

-- Tarea 1 · COMPLETADA (todos sus pasos estarán en FINALIZADO)
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (1, 'Diseño de base de datos', 'Modelado del esquema relacional del sistema de ventas',
        '2026-04-01', '2026-04-10', 'COMPLETADA', 1, 2, 1, TRUE);

-- Tarea 2 · INICIADA
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (2, 'Desarrollo del módulo de facturación', 'Implementar la lógica de generación de facturas',
        '2026-04-11', NULL, 'INICIADA', 1, 3, 1, TRUE);

-- Tarea 3 · PENDIENTE (sin fecha de inicio)
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (3, 'Pruebas de integración', 'Realizar pruebas end-to-end del módulo de ventas',
        NULL, NULL, 'PENDIENTE', 2, 1, 1, TRUE);

-- Tarea 4 · PENDIENTE (sin pasos — permitido por las consignas)
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (4, 'Documentar APIs de ventas', 'Generar documentación Swagger para todos los endpoints',
        NULL, NULL, 'PENDIENTE', 3, 2, 1, TRUE);

-- ── PROYECTO 2: Sistema de RRHH ────────────────────────────
-- creadores/asignados válidos: María(2), Braian(1), Lucía(4)

-- Tarea 5 · COMPLETADA
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (5, 'Relevamiento de requisitos', 'Reunión con RRHH para relevamiento funcional',
        '2026-03-15', '2026-03-20', 'COMPLETADA', 2, 1, 2, TRUE);

-- Tarea 6 · INICIADA
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (6, 'Implementar control de accesos', 'Gestión de roles y permisos del sistema de RRHH',
        '2026-04-05', NULL, 'INICIADA', 2, 4, 2, TRUE);

-- Tarea 7 · PENDIENTE
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (7, 'Módulo de liquidación de sueldos', 'Cálculo y generación de recibos de sueldo',
        NULL, NULL, 'PENDIENTE', 4, 1, 2, TRUE);

-- ── PROYECTO 3: Portal Web Empresa ─────────────────────────
-- creadores/asignados válidos: Juan(3), María(2), Carlos(5)

-- Tarea 8 · INICIADA (con fecha de inicio)
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (8, 'Diseño de wireframes', 'Crear maquetas de las pantallas principales del portal',
        '2026-04-20', NULL, 'INICIADA', 3, 5, 3, TRUE);

-- Tarea 9 · PENDIENTE (sin pasos — permitido)
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (9, 'Desarrollo frontend', 'Implementar las vistas en React según los wireframes aprobados',
        NULL, NULL, 'PENDIENTE', 5, 2, 3, TRUE);

-- Tarea 10 · COMPLETADA
INSERT INTO tasks (id_task, name_task, description, start_date, end_date, status_task, id_created_by, id_assigned_user, id_project, active)
VALUES (10, 'Configuración del servidor', 'Configurar VPS, dominio y certificado SSL',
        '2026-03-01', '2026-03-10', 'COMPLETADA', 3, 2, 3, TRUE);

-- ============================================================
-- PASOS
-- Regla: paso solo pertenece a una tarea
-- Regla: tarea COMPLETADA → todos sus pasos deben estar en FINALIZADO
-- Regla: tarea INICIADA → pasos pueden ser PENDIENTE, INICIADO o FINALIZADO
-- Regla: tarea PENDIENTE → pasos solo PENDIENTE
-- ============================================================

-- ── Pasos de Tarea 1 (COMPLETADA → todos FINALIZADO) ───────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(1, 'Identificar entidades principales',       'FINALIZADO', 1, TRUE),
(2, 'Crear diagrama entidad-relación',          'FINALIZADO', 1, TRUE),
(3, 'Validar esquema con el equipo',            'FINALIZADO', 1, TRUE);

-- ── Pasos de Tarea 2 (INICIADA → mix de estados) ───────────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(4, 'Definir estructura de factura',            'FINALIZADO', 2, TRUE),
(5, 'Implementar endpoint de generación',       'INICIADO',   2, TRUE),
(6, 'Implementar endpoint de anulación',        'PENDIENTE',  2, TRUE);

-- ── Pasos de Tarea 3 (PENDIENTE → todos PENDIENTE) ─────────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(7, 'Preparar casos de prueba',                 'PENDIENTE',  3, TRUE),
(8, 'Ejecutar pruebas de integración',          'PENDIENTE',  3, TRUE);

-- Tarea 4 no tiene pasos (permitido por las consignas)

-- ── Pasos de Tarea 5 (COMPLETADA → todos FINALIZADO) ───────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(9,  'Preparar cuestionario de entrevista',     'FINALIZADO', 5, TRUE),
(10, 'Realizar reunión con stakeholders',        'FINALIZADO', 5, TRUE),
(11, 'Documentar requisitos relevados',          'FINALIZADO', 5, TRUE);

-- ── Pasos de Tarea 6 (INICIADA → mix de estados) ───────────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(12, 'Definir roles del sistema',               'FINALIZADO', 6, TRUE),
(13, 'Implementar middleware de autenticación', 'INICIADO',   6, TRUE),
(14, 'Probar control de accesos',               'PENDIENTE',  6, TRUE);

-- ── Pasos de Tarea 7 (PENDIENTE → todos PENDIENTE) ─────────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(15, 'Relevar fórmulas de liquidación',         'PENDIENTE',  7, TRUE),
(16, 'Implementar motor de cálculo',            'PENDIENTE',  7, TRUE);

-- ── Pasos de Tarea 8 (INICIADA → mix de estados) ───────────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(17, 'Diseñar pantalla de inicio',              'FINALIZADO', 8, TRUE),
(18, 'Diseñar pantalla de contacto',            'INICIADO',   8, TRUE),
(19, 'Revisar diseños con el cliente',          'PENDIENTE',  8, TRUE);

-- Tarea 9 no tiene pasos (permitido)

-- ── Pasos de Tarea 10 (COMPLETADA → todos FINALIZADO) ──────
INSERT INTO steps (id_step, name_step, status_step, id_task, active) VALUES
(20, 'Contratar y configurar VPS',              'FINALIZADO', 10, TRUE),
(21, 'Configurar dominio DNS',                  'FINALIZADO', 10, TRUE),
(22, 'Instalar certificado SSL',                'FINALIZADO', 10, TRUE);

-- ============================================================
-- COMENTARIOS
-- Regla: el autor debe ser un usuario válido del sistema
-- ============================================================

-- Comentarios en Tarea 1
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(1,  'Esquema aprobado. Se puede continuar con la implementación.',
     '2026-04-08 10:30:00', 1, 1, TRUE),
(2,  'Agregué índices a las tablas de alta rotación para optimizar consultas.',
     '2026-04-09 14:00:00', 1, 2, TRUE);

-- Comentarios en Tarea 2
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(3,  'El endpoint de generación está en progreso. Falta validar decimales.',
     '2026-04-15 09:00:00', 2, 3, TRUE),
(4,  'Revisar el comportamiento cuando el monto es cero.',
     '2026-04-16 11:15:00', 2, 1, TRUE);

-- Comentarios en Tarea 3
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(5,  'Los casos de prueba deben incluir escenarios de error de red.',
     '2026-04-20 08:45:00', 3, 2, TRUE);

-- Comentarios en Tarea 5
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(6,  'El cliente confirmó los requisitos. Documento adjunto en el drive.',
     '2026-03-19 16:30:00', 5, 1, TRUE);

-- Comentarios en Tarea 6
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(7,  'El middleware de autenticación está siendo implementado con JWT.',
     '2026-04-10 13:00:00', 6, 4, TRUE),
(8,  'Confirmar si se usa refresh token o token de sesión simple.',
     '2026-04-11 09:30:00', 6, 2, TRUE);

-- Comentarios en Tarea 8
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(9,  'La pantalla de inicio fue aprobada por el cliente.',
     '2026-04-22 17:00:00', 8, 3, TRUE),
(10, 'Revisar tipografías según la guía de marca antes de continuar.',
     '2026-04-23 10:00:00', 8, 5, TRUE);

-- Comentarios en Tarea 10
INSERT INTO comments (id_comment, content, created_at, id_task, id_user, active) VALUES
(11, 'Servidor configurado y certificado SSL instalado correctamente.',
     '2026-03-09 18:00:00', 10, 2, TRUE);

-- ============================================================
-- FIN DEL SCRIPT
-- Resumen:
--   · 5 usuarios
--   · 3 proyectos (cada uno con 1 owner y 2 miembros)
--   · 10 tareas (3 COMPLETADAS, 3 INICIADAS, 4 PENDIENTES)
--   · 22 pasos distribuidos en 8 tareas (2 tareas sin pasos)
--   · 11 comentarios en 7 tareas distintas
-- ============================================================
