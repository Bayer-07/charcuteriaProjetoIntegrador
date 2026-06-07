ALTER TABLE products
ADD CONSTRAINT chk_stock_non_negative CHECK (stock_quantity >= 0);

CREATE OR REPLACE FUNCTION prevent_negative_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.stock_quantity < 0 THEN
        RAISE EXCEPTION 'Estoque insuficiente para o produto "%" (ID: %). Estoque atual não suporta a dedução.', NEW.name, NEW.id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_negative_stock
BEFORE UPDATE OF stock_quantity ON products
FOR EACH ROW
EXECUTE FUNCTION prevent_negative_stock();
