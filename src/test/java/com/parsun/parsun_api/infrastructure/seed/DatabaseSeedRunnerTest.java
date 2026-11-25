package com.parsun.parsun_api.infrastructure.seed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

class DatabaseSeedRunnerTest {

    private TransactionTemplate transactions;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        transactions = mock(TransactionTemplate.class);
        doAnswer(invocation -> {
            Consumer<TransactionStatus> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactions).executeWithoutResult(any());
    }

    @Test
    void continuesWithRemainingStepsWhenOneFails() {
        List<String> executed = new ArrayList<>();
        SeedStep failing = step(1, () -> {
            throw new IllegalStateException("boom");
        });
        SeedStep succeeding = step(2, () -> executed.add("V02"));
        DatabaseSeedRunner runner = new DatabaseSeedRunner(
                List.of(failing, succeeding), transactions, "demo");

        assertThatCode(runner::run).doesNotThrowAnyException();
        assertThat(executed).containsExactly("V02");
    }

    @Test
    void executesEveryStepInOrder() {
        List<String> executed = new ArrayList<>();
        DatabaseSeedRunner runner = new DatabaseSeedRunner(List.of(
                step(2, () -> executed.add("V02")),
                step(1, () -> executed.add("V01"))), transactions, "demo");

        runner.run();

        assertThat(executed).containsExactly("V01", "V02");
    }

    @Test
    void loadsSharedStepsForAnySourceAndSkipsOtherSources() {
        List<String> executed = new ArrayList<>();
        DatabaseSeedRunner runner = new DatabaseSeedRunner(List.of(
                step(1, SeedSource.SHARED, () -> executed.add("shared")),
                step(2, SeedSource.DEMO, () -> executed.add("demo")),
                step(3, SeedSource.VALERY, () -> executed.add("valery"))),
                transactions, "valery");

        runner.run();

        assertThat(executed).containsExactly("shared", "valery");
    }

    @Test
    void defaultsToValeryWhenSourceIsUnknown() {
        List<String> executed = new ArrayList<>();
        DatabaseSeedRunner runner = new DatabaseSeedRunner(List.of(
                step(1, SeedSource.VALERY, () -> executed.add("valery"))),
                transactions, "not-a-source");

        runner.run();

        assertThat(executed).containsExactly("valery");
    }

    private SeedStep step(int version, Runnable action) {
        return step(version, SeedSource.DEMO, action);
    }

    private SeedStep step(int version, SeedSource source, Runnable action) {
        return new SeedStep() {
            @Override
            public int version() {
                return version;
            }

            @Override
            public SeedSource source() {
                return source;
            }

            @Override
            public void seed() {
                action.run();
            }
        };
    }
}
