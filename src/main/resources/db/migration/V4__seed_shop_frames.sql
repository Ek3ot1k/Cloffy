INSERT INTO frames (name, image_url, price, description)
SELECT 'Aurora', NULL, 25, 'Сияющая рамка для аватара'
WHERE NOT EXISTS (SELECT 1 FROM frames WHERE name = 'Aurora');

INSERT INTO frames (name, image_url, price, description)
SELECT 'Ocean', NULL, 50, 'Спокойная синяя рамка'
WHERE NOT EXISTS (SELECT 1 FROM frames WHERE name = 'Ocean');

INSERT INTO frames (name, image_url, price, description)
SELECT 'Sunset', NULL, 75, 'Тёплая градиентная рамка'
WHERE NOT EXISTS (SELECT 1 FROM frames WHERE name = 'Sunset');

INSERT INTO frames (name, image_url, price, description)
SELECT 'Neon', NULL, 100, 'Яркая неоновая рамка'
WHERE NOT EXISTS (SELECT 1 FROM frames WHERE name = 'Neon');
