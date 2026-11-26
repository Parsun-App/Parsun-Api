-- Remove cash register opening/expected balances from the persisted model.
ALTER TABLE `cash_registers`
    DROP COLUMN `opening_balance`,
    DROP COLUMN `expected_closing_balance`;
