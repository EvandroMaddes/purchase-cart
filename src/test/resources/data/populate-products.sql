INSERT INTO vat_rate(id, description, percentage)
VALUES (1, 'tax free', 0.00),
       (2, 'discounted vat', 0.185),
       (3, 'default vat', 0.22);

INSERT INTO product(id, unit_price, available_quantity, vat_rate_id)
VALUES (12, 5.00, 3, 2);

INSERT INTO product(id, unit_price, available_quantity, vat_rate_id)
VALUES (13, 3.00, 5, 3);
