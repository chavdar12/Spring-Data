-- Task 14. Needs MySQL 8.0.29+ for IF NOT EXISTS. Statements are separated by $$ because the body contains ';'.
CREATE PROCEDURE IF NOT EXISTS usp_get_books_count_by_author(IN p_first_name VARCHAR(255), IN p_last_name VARCHAR(255))
BEGIN
    SELECT COUNT(*)
    FROM books AS b
             JOIN authors AS a ON a.id = b.author_id
    WHERE a.first_name = p_first_name
      AND a.last_name = p_last_name;
END
$$
