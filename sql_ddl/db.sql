create table role_table
(
    id   serial      not null
        constraint role_table_pk
            primary key,
    name varchar(20) not null
);

create table user_table
(
    id       serial not null
        constraint user_table_pk
            primary key,
    login    varchar(50),
    password varchar(500),
    role_id  integer
        constraint user_table_role_table_id_fk
            references role_table(id)
);
create unique index user_table_login_uindex
    on user_table (login);
insert into role_table(name) values ('ROLE_ADMIN');
insert into role_table(name) values ('ROLE_USER');


DROP TABLE IF EXISTS gpu;
CREATE TABLE gpu (
                     id bigserial PRIMARY KEY NOT NULL,
                     add_power_pin varchar(100) DEFAULT NULL,
                     tdp int DEFAULT NULL,
                     avg_bench real DEFAULT NULL,
                     price decimal DEFAULT NULL,
                     name varchar(200) DEFAULT NULL
)  ;
INSERT INTO gpu(add_power_pin, tdp, avg_bench, price, name) VALUES ('8+8',600,129,1010,'GTX 1080-Ti');
INSERT INTO gpu(add_power_pin, tdp, avg_bench, price, name) VALUES ('6+8',600,95,570,'GTX 1070-Ti');
INSERT INTO gpu(add_power_pin, tdp, avg_bench, price, name) VALUES ('8',500,57,170,'RX 580');
INSERT INTO gpu(add_power_pin, tdp, avg_bench, price, name) VALUES ('6',350,58,182,'Nvidia GTX 1650S (Super)');
INSERT INTO gpu(add_power_pin, tdp, avg_bench, price, name) VALUES ('6',300,27,180,'Nvidia GTX 1050');


DROP TABLE IF EXISTS motherboard;
CREATE TABLE motherboard (
                             id bigserial PRIMARY KEY NOT NULL,
                             socket varchar(100) DEFAULT NULL,
                             max_ram int DEFAULT NULL,
                             ram_type varchar(200) DEFAULT NULL,
                             num_ram int DEFAULT NULL,
                             power_pin varchar(100) DEFAULT NULL,
                             processor_power_pin varchar(100) DEFAULT NULL,
                             chipset varchar(100) DEFAULT NULL,
                             form_factor varchar(200) DEFAULT NULL,
                             m2 boolean DEFAULT NULL,
                             price decimal DEFAULT NULL,
                             name varchar(200) DEFAULT NULL
)  ;


INSERT INTO motherboard(socket, max_ram, ram_type, num_ram, power_pin, processor_power_pin, chipset, form_factor, m2, price, name)
VALUES ('1151', 64,'ddr4', 4, '24', '8', 'z390','atx', true, 180,'MSI MAG Z390');
INSERT INTO motherboard(socket, max_ram, ram_type, num_ram, power_pin, processor_power_pin, chipset, form_factor, m2, price, name)
VALUES ('478', 2,'ddr1', 2, '20', '8', '865g','micro-atx', false, 30,'ASRock P4i65G');
INSERT INTO motherboard(socket, max_ram, ram_type, num_ram, power_pin, processor_power_pin, chipset, form_factor, m2, price, name)
VALUES ('1151', 128,'ddr4', 8, '24', '8', 'x299','atx', true, 400,'Asus WS X299');


DROP TABLE IF EXISTS ram;
CREATE TABLE ram (
                     id bigserial PRIMARY KEY NOT NULL,
                     capacity int DEFAULT NULL,
                     type varchar(200) DEFAULT NULL,
                     amount int DEFAULT NULL,
                     freq real DEFAULT NULL,
                     avg_bench real DEFAULT NULL,
                     price decimal DEFAULT NULL,
                     name varchar(200) DEFAULT NULL
);
INSERT INTO ram(capacity, type, amount, freq, avg_bench, price, name)
VALUES (16,'ddr4',2, 3200,86,165,'Corsair Vengeance RGB PRO');
INSERT INTO ram(capacity, type, amount, freq, avg_bench, price, name)
VALUES (8,'ddr4',4,2400,89,184,'Vengeance LPX');
INSERT INTO ram(capacity, type, amount, freq, avg_bench, price, name)
VALUES (8,'ddr4',2,3200,96,318,'G.SKILL Flare');
INSERT INTO ram(capacity, type, amount, freq, avg_bench, price, name)
VALUES (8,'ddr4',2,3000,82,93,'Trident Z C16');
INSERT INTO ram(capacity, type, amount, freq, avg_bench, price, name)
VALUES (8,'ddr4',2,2400,70,92,'Ripjaws 4 C15');


DROP TABLE IF EXISTS processor;
CREATE TABLE processor (
                           id bigserial PRIMARY KEY NOT NULL,
                           socket varchar(200) DEFAULT NULL,
                           tdp int DEFAULT NULL,
                           core8pts real DEFAULT NULL,
                           price decimal DEFAULT NULL,
                           name varchar(200) DEFAULT NULL
) ;
INSERT INTO processor(socket, tdp, core8pts, price, name) VALUES ('1151-V2',95,833,440,'i7 8700k');
INSERT INTO processor(socket, tdp, core8pts, price, name) VALUES ('1151',80,766,219,'i5 9600k');
INSERT INTO processor(socket, tdp, core8pts, price, name) VALUES ('1151',65,648,275,'i5-8500');
INSERT INTO processor(socket, tdp, core8pts, price, name) VALUES ('1151',60,726,240,'i5-8600K');
INSERT INTO processor(socket, tdp, core8pts, price, name) VALUES ('1151',60,487,230,'Xeon E3-1240');


DROP TABLE IF EXISTS power_supply;
CREATE TABLE power_supply (
                              id bigserial PRIMARY KEY NOT NULL,
                              motherboard_power_pin varchar(45) DEFAULT NULL,
                              gpu_add_power_pin varchar(45) DEFAULT NULL,
                              processor_power_pin varchar(45) DEFAULT NULL,
                              power int DEFAULT NULL,
                              price decimal DEFAULT NULL,
                              name varchar(200) DEFAULT NULL
);
INSERT INTO power_supply(motherboard_power_pin, gpu_add_power_pin, processor_power_pin, power, price, name) VALUES ('20+4','4*6+2','4+4',650,100,'Seasonic CORE GC-650 Gold');
INSERT INTO power_supply(motherboard_power_pin, gpu_add_power_pin, processor_power_pin, power, price, name) VALUES ('20+4','4*6+2','4+4',1000,400,'Seasonic PRIME ULTRA Titanium');
INSERT INTO power_supply(motherboard_power_pin, gpu_add_power_pin, processor_power_pin, power, price, name) VALUES ('20+4','1*4','2',400,15,'Logicpower ATX-400W');


DROP TABLE IF EXISTS motherboard_interface;
CREATE TABLE motherboard_interface (
                                       id bigserial PRIMARY KEY NOT NULL,
                                       power_supply_pin varchar(200) NOT NULL,
                                       motherboard_interface_value varchar(200) NOT NULL
);
INSERT INTO motherboard_interface (power_supply_pin, motherboard_interface_value) VALUES ('20+4', '24');
INSERT INTO motherboard_interface (power_supply_pin, motherboard_interface_value) VALUES ('20+4', '20');


DROP TABLE IF EXISTS gpu_interface;
CREATE TABLE gpu_interface(
                              id              bigserial PRIMARY KEY NOT NULL,
                              power_supply_pin varchar(200)    NOT NULL,
                              gpu_interface_value   varchar(200) NOT NULL
);
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '6');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '12');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '18');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '24');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '8');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '14');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '20');
INSERT INTO gpu_interface (power_supply_pin, gpu_interface_value) VALUES ('4*6+2', '26');


DROP TABLE IF EXISTS processor_interface;
CREATE TABLE processor_interface (
                                     id bigserial PRIMARY KEY NOT NULL,
                                     power_supply_pin varchar(200) NOT NULL,
                                     processor_interface_value varchar(200) NOT NULL
);
INSERT INTO processor_interface (power_supply_pin, processor_interface_value) VALUES ('4+4', '8');
INSERT INTO processor_interface (power_supply_pin, processor_interface_value) VALUES ('4+4', '4');
INSERT INTO processor_interface (power_supply_pin, processor_interface_value) VALUES ('2', '2');


DROP TABLE IF EXISTS pc;
CREATE TABLE pc (
                    id serial PRIMARY KEY NOT NULL,
                    power_supply_id bigint NOT NULL,
                    motherboard_id bigint NOT NULL,
                    gpu_id bigint NOT NULL,
                    processor_id bigint NOT NULL,
                    ram_id bigint NOT NULL,
                    name bigint NOT NULL,
                    price decimal NOT NULL
);
create table supported_cpu
(
    id             bigserial PRIMARY KEY NOT NULL,
    motherboard_id bigint                NOT NULL,
    cpu_generation varchar(100)          NOT NULL
);

ALTER TABLE supported_cpu ADD CONSTRAINT motherboard_id_fk0 FOREIGN KEY (motherboard_id) REFERENCES motherboard(id);
INSERT INTO supported_cpu(motherboard_id, cpu_generation) VALUES (1, 'i7');


ALTER TABLE pc ADD CONSTRAINT pc_fk0 FOREIGN KEY (power_supply_id) REFERENCES power_supply(id);
ALTER TABLE pc ADD CONSTRAINT pc_fk1 FOREIGN KEY (motherboard_id) REFERENCES motherboard(id);
ALTER TABLE pc ADD CONSTRAINT pc_fk2 FOREIGN KEY (gpu_id) REFERENCES gpu(id);
ALTER TABLE pc ADD CONSTRAINT pc_fk3 FOREIGN KEY (processor_id) REFERENCES processor(id);
ALTER TABLE pc ADD CONSTRAINT pc_fk4 FOREIGN KEY (ram_id) REFERENCES ram(id);

