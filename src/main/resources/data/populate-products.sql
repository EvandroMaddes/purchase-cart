INSERT INTO vat_rate(id, description, percentage)
VALUES (1, 'tax free', 0.0),
       (2, 'discounted vat', 0.22),
       (3, 'default vat', 0.70);

INSERT INTO product(id, unit_price, available_quantity, vat_rate_id)
VALUES (10, 5.00, 3, 3);

INSERT INTO product(id, unit_price, available_quantity, vat_rate_id)
VALUES (11, 3.00, 5, 2);
