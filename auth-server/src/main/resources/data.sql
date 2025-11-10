-- Admin user
INSERT INTO users (username, password) VALUES ('admin', '{noop}admin');

INSERT INTO authorities (user_id, authority)
VALUES ((SELECT id FROM users WHERE username='admin'), 'ADMIN');

-- Normal user
INSERT INTO users (username, password) VALUES ('user', '{noop}user');

INSERT INTO authorities (user_id, authority)
VALUES ((SELECT id FROM users WHERE username='user'), 'USER');
