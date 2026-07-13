-- ============================================================
--  TicketFlow — Eventos de prueba (ejecutar UNA vez)
--  Base de datos: ticketflow_db
--
--  NO crea usuarios: toma automáticamente un usuario ORGANIZER
--  que ya exista en la BD (o, si no hay, cualquier usuario) como
--  dueño de los eventos. Venues y categorías se referencian por nombre.
-- ============================================================

USE ticketflow_db;

-- ----------------------------------------------------------------
-- 0) Elegir el organizador dueño de los eventos
--    Toma el primer usuario con rol ORGANIZER; si no hay, cualquier usuario.
-- ----------------------------------------------------------------
SET @org := (SELECT u.id FROM users u
             JOIN roles r ON r.id = u.role_id
             WHERE r.name = 'ORGANIZER'
             ORDER BY u.created_at LIMIT 1);
SET @org := COALESCE(@org, (SELECT id FROM users ORDER BY created_at LIMIT 1));
-- Si @org queda NULL es que no tienes NINGÚN usuario: crea uno primero.

-- ----------------------------------------------------------------
-- 1) Venues (lugares) — necesarios para los eventos
-- ----------------------------------------------------------------
INSERT INTO venues (name, address, city, capacity, created_at, updated_at) VALUES
('Estadio Nacional',             'Calle José Díaz s/n, Cercado',        'Lima',     45000, NOW(6), NOW(6)),
('Gran Teatro Nacional',         'Av. Javier Prado Este 2225',          'Lima',      1500, NOW(6), NOW(6)),
('Arena 1 - Costa Verde',        'Circuito de Playas, San Miguel',      'Lima',     12000, NOW(6), NOW(6)),
('Teatro Municipal de Arequipa', 'Calle Mercaderes 239',                'Arequipa',   800, NOW(6), NOW(6));

-- ----------------------------------------------------------------
-- 2) Eventos (categoría/venue por nombre; organizador = @org)
--    Fechas futuras para que aparezcan en el catálogo (status ACTIVE).
-- ----------------------------------------------------------------
INSERT INTO events (title, description, date_time, image_url, status, venue_id, organizer_id, category_id, created_at, updated_at) VALUES
('Festival Rock en Lima 2026',
 'Una noche épica con las mejores bandas de rock nacional e internacional sobre el escenario principal.',
 '2026-08-15 20:00:00', 'https://picsum.photos/seed/rocklima/800/450', 'ACTIVE',
 (SELECT id FROM venues WHERE name = 'Estadio Nacional' LIMIT 1),
 @org,
 (SELECT id FROM categories WHERE name = 'Concierto' LIMIT 1), NOW(6), NOW(6)),

('El Lago de los Cisnes - Ballet',
 'La compañía nacional de ballet presenta la obra maestra de Tchaikovsky en una puesta en escena imperdible.',
 '2026-09-20 19:30:00', 'https://picsum.photos/seed/ballet/800/450', 'ACTIVE',
 (SELECT id FROM venues WHERE name = 'Gran Teatro Nacional' LIMIT 1),
 @org,
 (SELECT id FROM categories WHERE name = 'Teatro' LIMIT 1), NOW(6), NOW(6)),

('Final Copa Apertura 2026',
 'El partido decisivo de la temporada. Vive la pasión del fútbol en vivo con tu equipo.',
 '2026-09-05 15:00:00', 'https://picsum.photos/seed/futbol/800/450', 'ACTIVE',
 (SELECT id FROM venues WHERE name = 'Arena 1 - Costa Verde' LIMIT 1),
 @org,
 (SELECT id FROM categories WHERE name = 'Deportes' LIMIT 1), NOW(6), NOW(6)),

('Noche de Stand-up Comedy',
 'Los mejores comediantes del país en una noche de risas sin parar. Apto para mayores de 16.',
 '2026-07-30 21:00:00', 'https://picsum.photos/seed/standup/800/450', 'ACTIVE',
 (SELECT id FROM venues WHERE name = 'Teatro Municipal de Arequipa' LIMIT 1),
 @org,
 (SELECT id FROM categories WHERE name = 'Stand-up' LIMIT 1), NOW(6), NOW(6)),

('Festival Gastronómico Sabores',
 'Una experiencia culinaria con food trucks, chefs invitados y música en vivo durante todo el día.',
 '2026-10-10 12:00:00', 'https://picsum.photos/seed/gastro/800/450', 'ACTIVE',
 (SELECT id FROM venues WHERE name = 'Arena 1 - Costa Verde' LIMIT 1),
 @org,
 (SELECT id FROM categories WHERE name = 'Festival' LIMIT 1), NOW(6), NOW(6));

-- ----------------------------------------------------------------
-- 3) Tipos de entrada (niveles de precio) por evento
-- ----------------------------------------------------------------
-- Festival Rock en Lima 2026
INSERT INTO ticket_types (event_id, name, price, total_qty, sold_qty, created_at, updated_at) VALUES
((SELECT id FROM events WHERE title = 'Festival Rock en Lima 2026' LIMIT 1), 'General', 80.00, 5000, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Festival Rock en Lima 2026' LIMIT 1), 'VIP',    180.00,  500, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Festival Rock en Lima 2026' LIMIT 1), 'Palco',  350.00,  100, 0, NOW(6), NOW(6));

-- El Lago de los Cisnes - Ballet
INSERT INTO ticket_types (event_id, name, price, total_qty, sold_qty, created_at, updated_at) VALUES
((SELECT id FROM events WHERE title = 'El Lago de los Cisnes - Ballet' LIMIT 1), 'Platea',   120.00, 600, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'El Lago de los Cisnes - Ballet' LIMIT 1), 'Mezanine',  90.00, 500, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'El Lago de los Cisnes - Ballet' LIMIT 1), 'Cazuela',   60.00, 400, 0, NOW(6), NOW(6));

-- Final Copa Apertura 2026
INSERT INTO ticket_types (event_id, name, price, total_qty, sold_qty, created_at, updated_at) VALUES
((SELECT id FROM events WHERE title = 'Final Copa Apertura 2026' LIMIT 1), 'Norte',     50.00, 4000, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Final Copa Apertura 2026' LIMIT 1), 'Occidente', 90.00, 3000, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Final Copa Apertura 2026' LIMIT 1), 'Oriente',   90.00, 3000, 0, NOW(6), NOW(6));

-- Noche de Stand-up Comedy
INSERT INTO ticket_types (event_id, name, price, total_qty, sold_qty, created_at, updated_at) VALUES
((SELECT id FROM events WHERE title = 'Noche de Stand-up Comedy' LIMIT 1), 'General', 45.00, 600, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Noche de Stand-up Comedy' LIMIT 1), 'VIP',     75.00, 120, 0, NOW(6), NOW(6));

-- Festival Gastronómico Sabores
INSERT INTO ticket_types (event_id, name, price, total_qty, sold_qty, created_at, updated_at) VALUES
((SELECT id FROM events WHERE title = 'Festival Gastronómico Sabores' LIMIT 1), 'Entrada General', 35.00, 8000, 0, NOW(6), NOW(6)),
((SELECT id FROM events WHERE title = 'Festival Gastronómico Sabores' LIMIT 1), 'Pase Premium',   120.00, 1000, 0, NOW(6), NOW(6));

-- ============================================================
-- Verificación rápida (opcional):
--   SELECT title, status, date_time FROM events;
--   SELECT e.title, t.name, t.price, t.total_qty FROM ticket_types t JOIN events e ON e.id = t.event_id;
-- ============================================================
