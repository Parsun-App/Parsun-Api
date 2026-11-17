ALTER TABLE `customers`
  ADD COLUMN `portal_access` tinyint(1) NOT NULL DEFAULT '0' AFTER `active`,
  ADD COLUMN `portal_password` varchar(255) DEFAULT NULL AFTER `portal_access`;

CREATE INDEX `idx_customers_portal_access` ON `customers` (`portal_access`);

ALTER TABLE `notifications`
  ADD COLUMN `customer_id` binary(16) DEFAULT NULL;

CREATE INDEX `idx_notifications_customer_id` ON `notifications` (`customer_id`);
CREATE INDEX `idx_notifications_customer_read` ON `notifications` (`customer_id`, `is_read`);

ALTER TABLE `payments`
  ADD COLUMN `target_invoice_id` binary(16) NOT NULL,
  ADD COLUMN `voucher_path` varchar(500) DEFAULT NULL;

ALTER TABLE `payments`
  ADD CONSTRAINT `fk_payments_target_invoice` FOREIGN KEY (`target_invoice_id`) REFERENCES `invoices` (`id`);

CREATE INDEX `idx_payments_target_invoice_id` ON `payments` (`target_invoice_id`);
