CREATE TABLE IF NOT EXISTS users (
user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
user_name VARCHAR(320) NOT NULL,
user_email VARCHAR(255) NOT NULL,
CONSTRAINT UQ_USER_EMAIL UNIQUE (user_email)
);

CREATE TABLE IF NOT EXISTS items (
item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
sharer_user_id BIGINT NOT NULL,
item_name VARCHAR(255) NOT NULL,
item_description VARCHAR(1023),
item_available boolean NOT NULL,
item_request BIGINT,
CONSTRAINT fk_items_to_users FOREIGN KEY(sharer_user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS booking (
booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
booking_start TIMESTAMP WITHOUT TIME ZONE,
booking_end TIMESTAMP WITHOUT TIME ZONE,
booking_item BIGINT,
booker BIGINT,
status varchar(16),
CONSTRAINT fk_booking_to_items FOREIGN KEY(booking_item) REFERENCES items(item_id),
CONSTRAINT fk_booking_to_users FOREIGN KEY(booker) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS requests (
request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
request_description VARCHAR(1023),
requester BIGINT,
request_created TIMESTAMP WITHOUT TIME ZONE,
CONSTRAINT fk_requests_to_users FOREIGN KEY(requester) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS comments (
comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
comment_text VARCHAR(1023),
comment_item BIGINT,
comment_author BIGINT,
comment_created TIMESTAMP WITHOUT TIME ZONE,
CONSTRAINT fk_comments_to_users FOREIGN KEY(comment_author) REFERENCES users(user_id)
);
