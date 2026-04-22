CREATE OR REPLACE FUNCTION prevent_category_delete_if_active_products()
RETURNS trigger AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM products
        WHERE category_id = OLD.id
          AND is_active = TRUE
    ) THEN
        RAISE EXCEPTION 'Cannot delete category: active products exist';
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_category_delete
BEFORE DELETE ON categories
FOR EACH ROW
EXECUTE FUNCTION prevent_category_delete_if_active_products();
