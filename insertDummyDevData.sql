-- Script to insert dummy dev data into the database.

-- You first need to register two users into the system before running this scirpt.

-- Replace the id here with the first user id you want to have ownership of the orders.
DECLARE @userId1 AS INT = 8;
-- Replace the id here with the second user id you want to have ownership of the orders.
DECLARE @userId2 AS INT = 9;

DELETE FROM web_order_quantities;
DELETE FROM web_order;
DELETE FROM inventory;
DELETE FROM product;
DELETE FROM address;

INSERT INTO product ([name], short_description, long_description, price) VALUES ('Product #1', 'Product one short description.', 'This is a very long description of product #1.', 5.50);
INSERT INTO product ([name], short_description, long_description, price) VALUES ('Product #2', 'Product two short description.', 'This is a very long description of product #2.', 10.56);
INSERT INTO product ([name], short_description, long_description, price) VALUES ('Product #3', 'Product three short description.', 'This is a very long description of product #3.', 2.74);
INSERT INTO product ([name], short_description, long_description, price) VALUES ('Product #4', 'Product four short description.', 'This is a very long description of product #4.', 15.69);
INSERT INTO product ([name], short_description, long_description, price) VALUES ('Product #5', 'Product five short description.', 'This is a very long description of product #5.', 42.59);

DECLARE @product1 INT, @product2 INT, @product3 INT, @product4 INT, @product5 AS INT;

SELECT @product1 = id FROM product WHERE [name] = 'Product #1';
SELECT @product2 = id FROM product WHERE [name] = 'Product #2';
SELECT @product3 = id FROM product WHERE [name] = 'Product #3';
SELECT @product4 = id FROM product WHERE [name] = 'Product #4';
SELECT @product5 = id FROM product WHERE [name] = 'Product #5';

INSERT INTO inventory (product_id, quantity) VALUES (@product1, 5);
INSERT INTO inventory (product_id, quantity) VALUES (@product2, 8);
INSERT INTO inventory (product_id, quantity) VALUES (@product3, 12);
INSERT INTO inventory (product_id, quantity) VALUES (@product4, 73);
INSERT INTO inventory (product_id, quantity) VALUES (@product5, 2);

INSERT INTO address (address_line_1, city, country, user_id) VALUES ('123 Tester Hill', 'Testerton', 'England', @userId1);
INSERT INTO address (address_line_1, city, country, user_id) VALUES ('312 Spring Boot', 'Hibernate', 'England', @userId2);

DECLARE @address1 INT, @address2 INT;

SELECT TOP 1 @address1 = id FROM address WHERE user_id = @userId1 ORDER BY id DESC;
SELECT TOP 1 @address2 = id FROM address WHERE user_id = @userId2 ORDER BY id DESC;

INSERT INTO web_order (address_id, user_id) VALUES (@address1, @userId1);
INSERT INTO web_order (address_id, user_id) VALUES (@address1, @userId1);
INSERT INTO web_order (address_id, user_id) VALUES (@address1, @userId1);
INSERT INTO web_order (address_id, user_id) VALUES (@address2, @userId2);
INSERT INTO web_order (address_id, user_id) VALUES (@address2, @userId2);

DECLARE @order1 INT, @order2 INT, @order3 INT, @order4 INT, @order5 INT;

SELECT TOP 1 @order1 = id FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC
SELECT @order2 = id FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC OFFSET 1 ROW FETCH FIRST 1 ROW ONLY
SELECT  @order3 = id FROM web_order WHERE address_id = @address1 AND user_id = @userId1 ORDER BY id DESC OFFSET 2 ROW FETCH FIRST 1 ROW ONLY
SELECT TOP 1 @order4 = id FROM web_order WHERE address_id = @address2 AND user_id = @userId2 ORDER BY id DESC
SELECT @order5 = id FROM web_order WHERE address_id = @address2 AND user_id = @userId2 ORDER BY id DESC OFFSET 1 ROW FETCH FIRST 1 ROW ONLY

INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order1, @product1, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order1, @product2, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order2, @product3, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order2, @product2, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order2, @product5, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order3, @product3, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order4, @product4, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order4, @product2, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order5, @product3, 5);
INSERT INTO web_order_quantities (order_id, product_id, quantity) VALUES (@order5, @product1, 5);