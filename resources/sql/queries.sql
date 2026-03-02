-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name create-comment! :! :n
-- :doc creates a new comment record
INSERT INTO comments
(post_slug, author, message)
VALUES (:post_slug, :author, :message)

-- :name get-comments-by-slug :? :*
-- :doc retrieves all comments for a given blog post slug
SELECT * FROM comments
WHERE post_slug = :post_slug
ORDER BY timestamp ASC
