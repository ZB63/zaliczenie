package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.engine.EngineException;
import edu.iis.mto.testreactor.dishwasher.pump.PumpException;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DishWasherTest {

    DishWasher dishWasher;
    ProgramConfiguration programConfiguration;

    @Mock
    WaterPump waterPump;
    @Mock
    Engine engine;
    @Mock
    DirtFilter dirtFilter;
    @Mock
    Door door;

    @BeforeEach
    public void setup() {
        dishWasher = new DishWasher(waterPump, engine, dirtFilter, door);
    }
    
    // STATE TESTS -------------------------

    @Test
    public void properProgramWithoutTabletsShouldResultInSuccess() {
        WashingProgram washingProgramNotRelevant = WashingProgram.ECO;
        FillLevel fillLevelNotRelevant = FillLevel.HALF;
        boolean tabletsUsedRelevant = false;

        programConfiguration = buildProgramConfiguration(washingProgramNotRelevant,
                fillLevelNotRelevant, tabletsUsedRelevant);

        when(door.closed()).thenReturn(true);

        RunResult runResult = dishWasher.start(programConfiguration);

        int timeRelevantForProgram = 90;
        Status statusRelevant = Status.SUCCESS;
        RunResult expectedRunResult = buildStatus(timeRelevantForProgram, statusRelevant);

        assertTrue(runResult.getStatus().equals(expectedRunResult.getStatus()));
        assertTrue(runResult.getRunMinutes() == expectedRunResult.getRunMinutes());
    }

    @Test
    public void properProgramWithTabletsShouldResultInSuccess() {
        WashingProgram washingProgramNotRelevant = WashingProgram.ECO;
        FillLevel fillLevelNotRelevant = FillLevel.HALF;
        boolean tabletsUsedRelevant = true;

        programConfiguration = buildProgramConfiguration(washingProgramNotRelevant,
                fillLevelNotRelevant, tabletsUsedRelevant);

        when(door.closed()).thenReturn(true);
        when(dirtFilter.capacity()).thenReturn(100.0d);

        RunResult runResult = dishWasher.start(programConfiguration);

        int timeRelevantForProgram = 90;
        Status statusRelevant = Status.SUCCESS;
        RunResult expectedRunResult = buildStatus(timeRelevantForProgram, statusRelevant);

        assertTrue(runResult.getStatus().equals(expectedRunResult.getStatus()));
        assertTrue(runResult.getRunMinutes() == expectedRunResult.getRunMinutes());
    }

    @Test
    public void unlockedDoorsShouldResultInDoorOpenError() {
        WashingProgram washingProgramNotRelevant = WashingProgram.ECO;
        FillLevel fillLevelNotRelevant = FillLevel.HALF;
        boolean tabletsUsedNotRelevant = false;

        programConfiguration = buildProgramConfiguration(washingProgramNotRelevant,
                fillLevelNotRelevant, tabletsUsedNotRelevant);

        when(door.closed()).thenReturn(false);

        RunResult runResult = dishWasher.start(programConfiguration);

        Status statusRelevant = Status.DOOR_OPEN;
        RunResult expectedRunResult = buildErrorStatus(statusRelevant);

        assertTrue(runResult.getStatus().equals(expectedRunResult.getStatus()));
    }

    @Test
    public void smallFilterCapacityShouldResultInFilterError() {
        WashingProgram washingProgramNotRelevant = WashingProgram.ECO;
        FillLevel fillLevelNotRelevant = FillLevel.HALF;
        boolean tabletsUsedRelevant = true;

        programConfiguration = buildProgramConfiguration(washingProgramNotRelevant,
                fillLevelNotRelevant, tabletsUsedRelevant);

        when(dirtFilter.capacity()).thenReturn(12.0d);
        when(door.closed()).thenReturn(true);

        RunResult runResult = dishWasher.start(programConfiguration);

        Status statusRelevant = Status.ERROR_FILTER;
        RunResult expectedRunResult = buildErrorStatus(statusRelevant);

        assertTrue(runResult.getStatus().equals(expectedRunResult.getStatus()));
    }

    // BEHAVIOUR TESTS -----------------------------

    @Test
    public void properProgramShouldResultInRightOrderOfMethods() throws PumpException, EngineException {
        WashingProgram washingProgramNotRelevant = WashingProgram.ECO;
        FillLevel fillLevelNotRelevant = FillLevel.HALF;
        boolean tabletsUsedRelevant = true;

        programConfiguration = buildProgramConfiguration(washingProgramNotRelevant,
                fillLevelNotRelevant, tabletsUsedRelevant);

        when(dirtFilter.capacity()).thenReturn(100.0d);
        when(door.closed()).thenReturn(true);

        dishWasher.start(programConfiguration);

        InOrder inOrder = inOrder(door, waterPump, engine);

        inOrder.verify(door).closed();
        inOrder.verify(waterPump).pour(any(FillLevel.class));
        inOrder.verify(engine).runProgram(any(WashingProgram.class));
        inOrder.verify(waterPump).drain();
        inOrder.verify(door).unlock();
    }

    private RunResult buildStatus(int timeRelevantForProgram, Status statusRelevant) {
        return RunResult.builder()
                        .withStatus(statusRelevant)
                        .withRunMinutes(timeRelevantForProgram)
                        .build();
    }

    private RunResult buildErrorStatus(Status statusRelevant) {
        return RunResult.builder()
                .withStatus(statusRelevant)
                .build();
    }

    private ProgramConfiguration buildProgramConfiguration(WashingProgram washingProgramNotRelevant, FillLevel fillLevelNotRelevant, boolean tabletsUsedRelevant) {
        return ProgramConfiguration.builder()
                                    .withProgram(washingProgramNotRelevant)
                                    .withFillLevel(fillLevelNotRelevant)
                                    .withTabletsUsed(tabletsUsedRelevant)
                                    .build();
    }

}
