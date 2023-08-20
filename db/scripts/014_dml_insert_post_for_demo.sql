INSERT INTO owners(name, user_id) VALUES ('Manager Anton', 1);

INSERT INTO engines(name) VALUES ('3.0L 6');
INSERT INTO cars(name, car_year, odometer, owner_id, engine_id) VALUES ('BMW X5 XDRIVE35D', 2015, 78561, 1, 1);
INSERT INTO files(name, path) VALUES ('2015 BMW X5 XDRIVE35D 001', 'src/main/resources/files/2015 BMW X5 XDRIVE35D/714461ab158c4a388cc88f0253d85fd7_ful.jpg');
INSERT INTO files(name, path) VALUES ('2015 BMW X5 XDRIVE35D 002', 'src/main/resources/files/2015 BMW X5 XDRIVE35D/7c5ae2034fa946df9a9fd7ed08db6669_ful.jpg');
INSERT INTO files(name, path) VALUES ('2015 BMW X5 XDRIVE35D 003', 'src/main/resources/files/2015 BMW X5 XDRIVE35D/dc07105ae64b40f3891b72a914568639_ful.jpg');
INSERT INTO posts(description, price, user_id, car_id) VALUES ('There are no Description for this Post', 18375, 1, 1);
INSERT INTO price_histories(before, after, post_id)  VALUES (18275, 18375, 1);
INSERT INTO post_files(post_id, file_id) VALUES (1, 1);
INSERT INTO post_files(post_id, file_id) VALUES (1, 2);
INSERT INTO post_files(post_id, file_id) VALUES (1, 3);
UPDATE posts SET description = 'I am the owner. Maximum equipment. Panorama, black interior and top, door closers, keyless entry. There are no errors. Fire car. Everything is working. Summer tires are of different widths, winter tires too (if you don’t need to, you can deduct from the cost). Button trunk. Shelf available. All tightened with film, airbrush "Spider". Very decent condition of the car, you will not regret it, it will give a lot of emotions. Mileage native, no one has ever twisted, see the report. No rust, the car has always been serviced according to the regulations, a very decent condition. I DO NOT RESPOND TO TRADING MESSAGES. It is possible to exchange for Pathfinder from 2012 diesel, Prado from 2010, the rest is not interesting.'
WHERE id = 1;

INSERT INTO engines(name) VALUES ('5.0L 8');
INSERT INTO cars(name, car_year, odometer, owner_id, engine_id) VALUES ('LAND ROVER SUPERCHARGED', 2014, 125472, 1, 2);
INSERT INTO files(name, path) VALUES ('2014 LAND ROVER SUPERCHARGED 001', 'src/main/resources/files/2014 LAND ROVER SUPERCHARGED/eb0b76d551ad413d9ee680b8754264f7_ful.jpg');
INSERT INTO files(name, path) VALUES ('2014 LAND ROVER SUPERCHARGED 002', 'src/main/resources/files/2014 LAND ROVER SUPERCHARGED/2d7754b8493f4a47a9fa2d653a674b6f_ful.jpg');
INSERT INTO files(name, path) VALUES ('2014 LAND ROVER SUPERCHARGED 003', 'src/main/resources/files/2014 LAND ROVER SUPERCHARGED/e541a09bc9594912b36204331be9063e_ful.jpg');
INSERT INTO posts(description, price, user_id, car_id) VALUES ('There are no Description for this Post', 16400, 1, 2);
INSERT INTO price_histories(before, after, post_id)  VALUES (0, 16400, 2);
INSERT INTO post_files(post_id, file_id) VALUES (2, 4);
INSERT INTO post_files(post_id, file_id) VALUES (2, 5);
INSERT INTO post_files(post_id, file_id) VALUES (2, 6);
UPDATE posts SET description = '150hp, TERRAIN RESPONSE, automatic transmission TIPTRONIC with the ability to shift gears on the steering wheel, , SURROUND CAMERA SYSTEM (CAMERA CIRCULAR VIEW (Front, side and rear view cameras)) + PARKTRONIK (FRONT AND REAR), EL. TRUNK DRIVE, NAVIGATION, 2 EL. SEATS (8-position) + MEMORY + SEAT HEATING + el. lumbar support for driver and passenger, separate Climate Control WITH AIR CONTROL SENSOR (Auto-Man automatic recirculation), KEYLESSGO (keyless entry), Start / Stop system, Torpedo leather, PREMIUM LEATHER, windshield heating, WEBASTO + timer, Meridean c 7 -INCH TOUCH MONITOR + MP3 + AUX + USB + iPod + 10-CD storage hard drive.'
WHERE id = 2;

INSERT INTO engines(name) VALUES ('1.5L 4');
INSERT INTO cars(name, car_year, odometer, owner_id, engine_id) VALUES ('HONDA ACCORD EXL', 2021, 16000, 1, 3);
INSERT INTO files(name, path) VALUES ('2021 HONDA ACCORD EXL 001', 'src/main/resources/files/2021 HONDA ACCORD EXL/1ff1286c9e294dc18a845cb90f100c3f_hrs.jpg');
INSERT INTO files(name, path) VALUES ('2021 HONDA ACCORD EXL 002', 'src/main/resources/files/2021 HONDA ACCORD EXL/a2a084373a9f4d9bb608859eec65ec05_hrs.jpg');
INSERT INTO files(name, path) VALUES ('2021 HONDA ACCORD EXL 003', 'src/main/resources/files/2021 HONDA ACCORD EXL/b95b4d5997634f7aa1b7f8b2650679c1_hrs.jpg');
INSERT INTO posts(description, price, user_id, car_id) VALUES ('There are no Description for this Post', 21000, 1, 3);
INSERT INTO price_histories(before, after, post_id)  VALUES (0, 21000, 3);
INSERT INTO post_files(post_id, file_id) VALUES (3, 7);
INSERT INTO post_files(post_id, file_id) VALUES (3, 8);
INSERT INTO post_files(post_id, file_id) VALUES (3, 9);
UPDATE posts SET description = 'Trim: Honda Accord 260T CVT Premium
Main options: 2021, climate control, audio, navigation, leather, airbags, ABS, ESP, LED headlights, central locking, power windows, power seats, power mirrors, cruise control, parking sensors, video camera, sunroof, alloy wheels, R18 .
Body color: Lunar Silver Metallic.'
WHERE id = 3;

INSERT INTO engines(name) VALUES ('2.0L 4');
INSERT INTO cars(name, car_year, odometer, owner_id, engine_id) VALUES ('MERCEDES-BENZ GLE 350 4MATIC', 2020, 23400, 1, 4);
INSERT INTO files(name, path) VALUES ('2020 MERCEDES-BENZ GLE 350 4MATIC 001', 'src/main/resources/files/2020 MERCEDES-BENZ GLE 350 4MATIC/96af91d2cf304332a4d2fbe08a18bf0a_ful.jpg');
INSERT INTO files(name, path) VALUES ('2020 MERCEDES-BENZ GLE 350 4MATIC 002', 'src/main/resources/files/2020 MERCEDES-BENZ GLE 350 4MATIC/940a5c2a196a4f7283ce7308a0c6afb5_ful.jpg');
INSERT INTO files(name, path) VALUES ('2020 MERCEDES-BENZ GLE 350 4MATIC 003', 'src/main/resources/files/2020 MERCEDES-BENZ GLE 350 4MATIC/b07d23e54d61423ca7349695b85b6dec_ful.jpg');
INSERT INTO posts(description, price, user_id, car_id) VALUES ('There are no Description for this Post', 35400, 1, 4);
INSERT INTO price_histories(before, after, post_id)  VALUES (0, 35400, 4);
INSERT INTO post_files(post_id, file_id) VALUES (4, 10);
INSERT INTO post_files(post_id, file_id) VALUES (4, 11);
INSERT INTO post_files(post_id, file_id) VALUES (4, 12);
UPDATE posts SET description = 'Owner! Good discount for cash! Selling my favorite car. No accidents.
Minimum mileage is 3 years.
Equipment: 350 4MATIC Sport Plus

Winter, summer on wheels. Winter casting originals + rubber spike Nokia Hakkapelita 10. Various. Summer two sets of different profiles. Double glazing tinted and normal). Film, ceramics.

Under warranty until 2025, 1 owner, maintenance at the dealer according to the regulations. 15.000 km. For myself, I did an oil change at 7500.'
WHERE id = 4;

INSERT INTO history_owners(owners_id, car_id) VALUES (1, 1);
INSERT INTO history_owners(owners_id, car_id) VALUES (1, 2);
INSERT INTO history_owners(owners_id, car_id) VALUES (1, 3);
INSERT INTO history_owners(owners_id, car_id) VALUES (1, 4);

INSERT INTO files(id, name, path) VALUES (0, 'no_img', 'src/main/resources/files/no_img.png');