BEGIN;
DELETE FROM vehicle_images vi
USING vehicles v
WHERE vi.vehicle_id = v.id
  AND v.vin LIKE 'VEL20%';

DELETE FROM vehicles
WHERE vin LIKE 'VEL20%';

WITH seed_customer AS (
  INSERT INTO customers (
    first_name,
    last_name,
    email,
    password,
    phone_number,
    is_active,
    role,
    version,
    created_at,
    updated_at
  )
  VALUES (
    'Velox',
    'Seller',
    'seed-20@veloxauto.local',
    'seed-password',
    '+381601010101',
    true,
    'SALES',
    0,
    NOW(),
    NOW()
  )
  ON CONFLICT (email) DO UPDATE SET updated_at = NOW()
  RETURNING id
),
creator AS (
  SELECT id FROM seed_customer
  UNION ALL
  SELECT id FROM customers WHERE email = 'seed-20@veloxauto.local' LIMIT 1
)
INSERT INTO vehicles (
  created_by, make, model, year, vin, price, mileage, color,
  transmission, fuel_type, engine_capacity, description, status,
  is_active, exchange, version, created_at, updated_at
)
SELECT
  c.id,
  v.make,
  v.model,
  v.year,
  v.vin,
  v.price::numeric(12,2),
  v.mileage,
  v.color,
  v.transmission,
  v.fuel_type,
  v.engine_capacity,
  v.description,
  v.status,
  v.is_active,
  v.exchange,
  0,
  NOW(),
  NOW()
FROM creator c
CROSS JOIN (
  VALUES
    ('Volkswagen',    'Golf 7',    2019, 'VEL2000000000001', 11250.00, 134000, 'White',  'MANUAL',    'DIESEL',   1598, 'Clean hatchback with full service history.',                     'AVAILABLE', true,  false),
    ('BMW',           '320d',      2018, 'VEL2000000000002', 16900.00, 121500, 'Black',  'AUTOMATIC', 'DIESEL',   1995, 'Sporty sedan with great highway efficiency.',                    'AVAILABLE', true,  true),
    ('Audi',          'A4',        2020, 'VEL2000000000003', 22400.00,  78500, 'Gray',   'AUTOMATIC', 'PETROL',   1984, 'Comfort-focused premium sedan in excellent condition.',          'AVAILABLE', true,  false),
    ('Mercedes-Benz', 'C 220',     2017, 'VEL2000000000004', 19800.00, 142300, 'Silver', 'AUTOMATIC', 'DIESEL',   2143, 'Reliable executive sedan with strong maintenance record.',       'RESERVED',  true,  false),
    ('Skoda',         'Octavia',   2021, 'VEL2000000000005', 17990.00,  55200, 'Blue',   'MANUAL',    'PETROL',   1498, 'Practical family car with large trunk and modern features.',     'AVAILABLE', true,  false),
    ('Peugeot',       '3008',      2020, 'VEL2000000000006', 18990.00,  86500, 'Blue',   'AUTOMATIC', 'PETROL',   1598, 'Well-kept SUV with a high-quality interior.',                    'AVAILABLE', true,  false),
    ('Renault',       'Megane',    2019, 'VEL2000000000007', 11990.00, 105700, 'Red',    'MANUAL',    'DIESEL',   1461, 'Balanced daily driver with low running costs.',                  'AVAILABLE', true,  true),
    ('Toyota',        'Corolla',   2022, 'VEL2000000000008', 21500.00,  42100, 'White',  'CVT',       'HYBRID',   1798, 'Efficient hybrid model with smooth urban performance.',          'AVAILABLE', true,  false),
    ('Honda',         'Civic',     2018, 'VEL2000000000009', 13950.00, 128900, 'Silver', 'MANUAL',    'PETROL',   1799, 'Known for reliability and responsive handling.',                 'AVAILABLE', true,  false),
    ('Hyundai',       'Tucson',    2021, 'VEL2000000000010', 23900.00,  49800, 'Gray',   'AUTOMATIC', 'DIESEL',   1998, 'Comfortable midsize SUV with modern driver assists.',            'AVAILABLE', true,  true),
    ('Kia',           'Sportage',  2020, 'VEL2000000000011', 20990.00,  67600, 'Green',  'AUTOMATIC', 'DIESEL',   1598, 'Popular SUV with clean interior and strong road presence.',      'AVAILABLE', true,  false),
    ('Ford',          'Focus',     2017, 'VEL2000000000012',  9950.00, 154400, 'Black',  'MANUAL',    'PETROL',    999, 'Compact hatch ideal for city and highway commuting.',            'SOLD',      false, false),
    ('Nissan',        'Qashqai',   2019, 'VEL2000000000013', 17400.00,  90200, 'Bronze', 'AUTOMATIC', 'PETROL',   1332, 'Versatile crossover with excellent visibility and comfort.',     'AVAILABLE', true,  false),
    ('Mazda',         'CX-5',      2018, 'VEL2000000000014', 18490.00,  99800, 'Red',    'AUTOMATIC', 'DIESEL',   2191, 'Driver-focused SUV with premium feel.',                          'AVAILABLE', true,  false),
    ('Opel',          'Astra',     2016, 'VEL2000000000015',  8450.00, 171300, 'White',  'MANUAL',    'DIESEL',   1598, 'Budget-friendly hatchback with dependable mechanics.',           'INACTIVE',  false, false),
    ('Fiat',          'Tipo',      2020, 'VEL2000000000016', 12200.00,  81200, 'Gray',   'MANUAL',    'PETROL',   1368, 'Spacious compact sedan suitable for everyday family use.',       'AVAILABLE', true,  false),
    ('Volvo',         'XC60',      2019, 'VEL2000000000017', 29900.00,  68900, 'Blue',   'AUTOMATIC', 'HYBRID',   1969, 'Premium SUV with advanced safety package and comfort.',          'AVAILABLE', true,  true),
    ('Tesla',         'Model 3',   2021, 'VEL2000000000018', 32900.00,  54400, 'White',  'AUTOMATIC', 'ELECTRIC',     0, 'Long-range EV with minimalist interior and strong acceleration.', 'AVAILABLE', true,  false),
    ('SEAT',          'Leon',      2018, 'VEL2000000000019', 12990.00, 117400, 'Black',  'MANUAL',    'PETROL',   1395, 'Stylish hatch with balanced comfort and performance.',           'RESERVED',  true,  false),
    ('Dacia',         'Duster',    2022, 'VEL2000000000020', 16950.00,  38400, 'Orange', 'MANUAL',    'PETROL',   1333, 'Affordable SUV with practical setup and good ground clearance.', 'AVAILABLE', true,  true)
) AS v(
  make, model, year, vin, price, mileage, color, transmission,
  fuel_type, engine_capacity, description, status, is_active, exchange
);

COMMIT;
