package com.cashly.cashly_api.infrastructure.seed.steps;

import static com.cashly.cashly_api.infrastructure.seed.SeedIds.*;

import com.cashly.cashly_api.finance.treasury.cash_register.sessions.infrastructure.persistence.entities.CashRegisterJpaEntity;
import com.cashly.cashly_api.infrastructure.seed.SeedStep;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V20CashRegistersSeed implements SeedStep {

    private static final String ADMIN_NAME = "Parsun Administrator";
    private static final String OPERATOR_NAME = "Sales Operator";

    private final EntityManager entityManager;

    public V20CashRegistersSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private record Session(String key, LocalDate date, String status,
            UUID openedBy, String openedByName, String closingNotes,
            String difference, int closeHour, int closeMinute) {
    }

    private static final List<Session> SESSIONS = List.of(
            new Session("month-2025-12", LocalDate.of(2025, 12, 31), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de diciembre 2025", "0.00", 17, 0),
            new Session("month-2026-01", LocalDate.of(2026, 1, 31), "CLOSED",
                    OPERATOR_USER, OPERATOR_NAME,
                    "Cierre de enero 2026", "-0.50", 17, 15),
            new Session("month-2026-02", LocalDate.of(2026, 2, 28), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de febrero 2026", "0.25", 17, 0),
            new Session("month-2026-03", LocalDate.of(2026, 3, 31), "CLOSED",
                    OPERATOR_USER, OPERATOR_NAME,
                    "Cierre de marzo 2026", "0.00", 17, 30),
            new Session("month-2026-04", LocalDate.of(2026, 4, 30), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de abril 2026", "-1.00", 17, 0),
            new Session("month-2026-05", LocalDate.of(2026, 5, 31), "CLOSED",
                    OPERATOR_USER, OPERATOR_NAME,
                    "Cierre de mayo 2026", "0.00", 17, 10),
            new Session("month-2026-06", LocalDate.of(2026, 6, 30), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de junio 2026", "0.00", 18, 0),
            new Session("month-2026-07", LocalDate.of(2026, 7, 31), "CLOSED",
                    OPERATOR_USER, OPERATOR_NAME,
                    "Cierre de julio 2026", "0.30", 17, 0),
            new Session("month-2026-08", LocalDate.of(2026, 8, 31), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de agosto 2026", "-0.50", 17, 0),
            new Session("month-2026-09", LocalDate.of(2026, 9, 30), "CLOSED",
                    OPERATOR_USER, OPERATOR_NAME,
                    "Cierre de septiembre 2026", "0.00", 17, 15),
            new Session("month-2026-10", LocalDate.of(2026, 10, 31), "CLOSED",
                    ADMIN_USER, ADMIN_NAME,
                    "Cierre de octubre 2026", "0.00", 17, 0),
            new Session("open", LocalDate.of(2026, 11, 1), "OPEN",
                    ADMIN_USER, ADMIN_NAME, null, null, 0, 0));

    @Override
    public int version() {
        return 20;
    }

    @Override
    public void seed() {
        for (Session session : SESSIONS) {
            register(session);
        }
    }

    private void register(Session session) {
        var registerId = id("cash-register." + session.key());
        if (entityManager.find(CashRegisterJpaEntity.class, registerId) != null) {
            return;
        }
        boolean closed = "CLOSED".equals(session.status());
        var register = new CashRegisterJpaEntity();
        register.setId(registerId);
        register.setClosureDate(session.date());
        register.setStatus(session.status());
        register.setOpenedBy(session.openedBy());
        register.setOpenedByName(session.openedByName());
        register.setOpenedAt(session.date().atTime(8, 0));
        register.setTotalPayments(BigDecimal.ZERO);
        register.setTotalAdvances(BigDecimal.ZERO);
        register.setTotalReturns(BigDecimal.ZERO);
        register.setTotalCreditNotes(BigDecimal.ZERO);
        register.setTotalDebitNotes(BigDecimal.ZERO);
        register.setTotalInvoices(BigDecimal.ZERO);
        register.setPaymentCount(0);
        register.setAdvanceCount(0);
        register.setReturnCount(0);
        register.setInvoiceCount(0);
        register.setTotalIncome(BigDecimal.ZERO);
        register.setTotalExpense(BigDecimal.ZERO);
        register.setCreatedAt(session.date().atTime(8, 0));
        if (closed) {
            var targetDifference = new BigDecimal(session.difference());
            register.setClosedBy(session.openedBy());
            register.setClosedByName(session.openedByName());
            register.setClosedAt(
                    session.date().atTime(session.closeHour(),
                            session.closeMinute()));
            register.setClosingNotes(session.closingNotes());
            register.setDifference(targetDifference);
            register.setActualClosingBalance(targetDifference);
            register.setUpdatedAt(
                    session.date().atTime(session.closeHour(),
                            session.closeMinute()));
        } else {
            register.setUpdatedAt(session.date().atTime(8, 0));
        }
        entityManager.persist(register);
    }
}
