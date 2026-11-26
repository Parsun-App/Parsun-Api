package com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.entity;

import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.valueobjects.RegisterId;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.valueobjects.RegisterStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CashRegister {

    private final RegisterId id;
    private final LocalDate closureDate;
    private RegisterStatus status;
    private final UUID openedBy;
    private final String openedByName;
    private final LocalDateTime openedAt;
    private UUID closedBy;
    private String closedByName;
    private LocalDateTime closedAt;
    private BigDecimal totalPayments;
    private BigDecimal totalAdvances;
    private BigDecimal totalReturns;
    private BigDecimal totalCreditNotes;
    private BigDecimal totalDebitNotes;
    private BigDecimal totalInvoices;
    private int paymentCount;
    private int advanceCount;
    private int returnCount;
    private int invoiceCount;
    private BigDecimal actualClosingBalance;
    private BigDecimal difference;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private String closingNotes;
    private int version;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<CashRegisterDetail> details;

    private CashRegister(RegisterId id, LocalDate closureDate,
            RegisterStatus status, UUID openedBy, String openedByName,
            LocalDateTime openedAt, UUID closedBy, String closedByName,
            LocalDateTime closedAt, BigDecimal totalPayments,
            BigDecimal totalAdvances,
            BigDecimal totalReturns, BigDecimal totalCreditNotes,
            BigDecimal totalDebitNotes, BigDecimal totalInvoices,
            int paymentCount, int advanceCount, int returnCount,
            int invoiceCount, BigDecimal actualClosingBalance,
            BigDecimal difference, BigDecimal totalIncome,
            BigDecimal totalExpense, String closingNotes, int version,
            LocalDateTime createdAt, LocalDateTime updatedAt,
            List<CashRegisterDetail> details) {
        this.id = id;
        this.closureDate = closureDate;
        this.status = status;
        this.openedBy = openedBy;
        this.openedByName = openedByName;
        this.openedAt = openedAt;
        this.closedBy = closedBy;
        this.closedByName = closedByName;
        this.closedAt = closedAt;
        this.totalPayments = totalPayments;
        this.totalAdvances = totalAdvances;
        this.totalReturns = totalReturns;
        this.totalCreditNotes = totalCreditNotes;
        this.totalDebitNotes = totalDebitNotes;
        this.totalInvoices = totalInvoices;
        this.paymentCount = paymentCount;
        this.advanceCount = advanceCount;
        this.returnCount = returnCount;
        this.invoiceCount = invoiceCount;
        this.actualClosingBalance = actualClosingBalance;
        this.difference = difference;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.closingNotes = closingNotes;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.details = details;
    }

    public static CashRegister open(RegisterId id, LocalDate closureDate,
            UUID openedBy, String openedByName) {
        Objects.requireNonNull(id, "RegisterId must not be null");
        Objects.requireNonNull(closureDate, "Closure date must not be null");
        Objects.requireNonNull(openedBy, "Opened by must not be null");
        Objects.requireNonNull(openedByName, "Opened by name must not be null");
        if (openedByName.isBlank()) {
            throw new IllegalArgumentException(
                    "Opened by name must not be blank");
        }

        LocalDateTime now = LocalDateTime.now();
        return new CashRegister(id, closureDate, RegisterStatus.OPEN, openedBy,
                openedByName, now, null, null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, 0,
                null, null, BigDecimal.ZERO, BigDecimal.ZERO,
                null, 0, now, now, new ArrayList<>());
    }

    public static CashRegister reconstitute(RegisterId id,
            LocalDate closureDate, RegisterStatus status, UUID openedBy,
            String openedByName, LocalDateTime openedAt, UUID closedBy,
            String closedByName, LocalDateTime closedAt,
            BigDecimal totalPayments, BigDecimal totalAdvances,
            BigDecimal totalReturns, BigDecimal totalCreditNotes,
            BigDecimal totalDebitNotes, BigDecimal totalInvoices,
            int paymentCount, int advanceCount, int returnCount,
            int invoiceCount, BigDecimal actualClosingBalance,
            BigDecimal difference, String closingNotes, int version,
            LocalDateTime createdAt,
            LocalDateTime updatedAt, List<CashRegisterDetail> details) {
        return new CashRegister(id, closureDate, status, openedBy, openedByName,
                openedAt, closedBy, closedByName, closedAt,
                totalPayments, totalAdvances, totalReturns, totalCreditNotes,
                totalDebitNotes, totalInvoices, paymentCount, advanceCount,
                returnCount, invoiceCount,
                actualClosingBalance, difference,
                totalPayments.add(totalAdvances),
                totalReturns.add(totalCreditNotes), closingNotes, version,
                createdAt, updatedAt,
                details != null ? new ArrayList<>(details) : new ArrayList<>());
    }

    public static CashRegister reconstitute(RegisterId id,
            LocalDate closureDate, RegisterStatus status, UUID openedBy,
            String openedByName, LocalDateTime openedAt, UUID closedBy,
            String closedByName, LocalDateTime closedAt,
            BigDecimal totalPayments, BigDecimal totalAdvances,
            BigDecimal totalReturns, BigDecimal totalCreditNotes,
            BigDecimal totalDebitNotes, BigDecimal totalInvoices,
            int paymentCount, int advanceCount, int returnCount,
            int invoiceCount, BigDecimal actualClosingBalance,
            BigDecimal difference, BigDecimal totalIncome,
            BigDecimal totalExpense, String closingNotes, int version,
            LocalDateTime createdAt,
            LocalDateTime updatedAt, List<CashRegisterDetail> details) {
        return new CashRegister(id, closureDate, status, openedBy, openedByName,
                openedAt, closedBy, closedByName, closedAt,
                totalPayments, totalAdvances, totalReturns, totalCreditNotes,
                totalDebitNotes, totalInvoices, paymentCount, advanceCount,
                returnCount, invoiceCount,
                actualClosingBalance, difference, totalIncome, totalExpense,
                closingNotes, version, createdAt, updatedAt,
                details != null ? new ArrayList<>(details) : new ArrayList<>());
    }

    public void applyTotals(BigDecimal totalPayments, int paymentCount,
            BigDecimal totalAdvances, int advanceCount, BigDecimal totalReturns,
            int returnCount, BigDecimal totalCreditNotes,
            BigDecimal totalDebitNotes, BigDecimal totalInvoices,
            int invoiceCount, List<CashRegisterDetail> details) {
        if (totalPayments.compareTo(BigDecimal.ZERO) < 0
                || totalAdvances.compareTo(BigDecimal.ZERO) < 0
                || totalReturns.compareTo(BigDecimal.ZERO) < 0
                || totalCreditNotes.compareTo(BigDecimal.ZERO) < 0
                || totalDebitNotes.compareTo(BigDecimal.ZERO) < 0
                || totalInvoices.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amounts must not be negative");
        }

        this.totalPayments = totalPayments;
        this.paymentCount = paymentCount;
        this.totalAdvances = totalAdvances;
        this.advanceCount = advanceCount;
        this.totalReturns = totalReturns;
        this.returnCount = returnCount;
        this.totalCreditNotes = totalCreditNotes;
        this.totalDebitNotes = totalDebitNotes;
        this.totalInvoices = totalInvoices;
        this.invoiceCount = invoiceCount;
        this.totalIncome = totalPayments.add(totalAdvances);
        this.totalExpense = totalReturns.add(totalCreditNotes);

        this.details.clear();
        if (details != null) {
            this.details.addAll(details);
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void applyMovementTotals(BigDecimal totalIncome,
            BigDecimal totalExpense) {
        Objects.requireNonNull(totalIncome, "Total income must not be null");
        Objects.requireNonNull(totalExpense, "Total expense must not be null");
        if (totalIncome.compareTo(BigDecimal.ZERO) < 0
                || totalExpense.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Movement totals must not be negative");
        }
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        if (status == RegisterStatus.CLOSED && actualClosingBalance != null) {
            this.difference = actualClosingBalance
                    .subtract(totalIncome.subtract(totalExpense));
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void close(UUID closedBy, String closedByName, String closingNotes) {
        close(closedBy, closedByName, totalIncome.subtract(totalExpense),
                closingNotes);
    }

    public void close(UUID closedBy, String closedByName,
            BigDecimal declaredBalance, String closingNotes) {
        if (status != RegisterStatus.OPEN) {
            throw new IllegalStateException("Only OPEN closures can be closed");
        }
        Objects.requireNonNull(closedBy, "Closed by must not be null");
        Objects.requireNonNull(closedByName, "Closed by name must not be null");
        if (closedByName.isBlank()) {
            throw new IllegalArgumentException(
                    "Closed by name must not be blank");
        }
        Objects.requireNonNull(declaredBalance,
                "Declared balance must not be null");
        if (declaredBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Declared balance must not be negative");
        }

        this.status = RegisterStatus.CLOSED;
        this.closedBy = closedBy;
        this.closedByName = closedByName;
        this.closedAt = LocalDateTime.now();
        this.actualClosingBalance = declaredBalance;
        this.difference = declaredBalance
                .subtract(totalIncome.subtract(totalExpense));
        this.closingNotes = closingNotes;
        this.updatedAt = LocalDateTime.now();
    }

    public RegisterId getId() {
        return id;
    }
    public LocalDate getClosureDate() {
        return closureDate;
    }
    public RegisterStatus getStatus() {
        return status;
    }
    public UUID getOpenedBy() {
        return openedBy;
    }
    public String getOpenedByName() {
        return openedByName;
    }
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }
    public UUID getClosedBy() {
        return closedBy;
    }
    public String getClosedByName() {
        return closedByName;
    }
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    public BigDecimal getTotalPayments() {
        return totalPayments;
    }
    public BigDecimal getTotalAdvances() {
        return totalAdvances;
    }
    public BigDecimal getTotalReturns() {
        return totalReturns;
    }
    public BigDecimal getTotalCreditNotes() {
        return totalCreditNotes;
    }
    public BigDecimal getTotalDebitNotes() {
        return totalDebitNotes;
    }
    public BigDecimal getTotalInvoices() {
        return totalInvoices;
    }
    public int getPaymentCount() {
        return paymentCount;
    }
    public int getAdvanceCount() {
        return advanceCount;
    }
    public int getReturnCount() {
        return returnCount;
    }
    public int getInvoiceCount() {
        return invoiceCount;
    }
    public BigDecimal getActualClosingBalance() {
        return actualClosingBalance;
    }
    public BigDecimal getDifference() {
        return difference;
    }
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }
    public BigDecimal getTotalExpense() {
        return totalExpense;
    }
    public String getClosingNotes() {
        return closingNotes;
    }
    public int getVersion() {
        return version;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public List<CashRegisterDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }
}
