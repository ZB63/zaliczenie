package edu.iis.mto.testreactor.dishwasher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import edu.iis.mto.testreactor.dishwasher.engine.Engine;
import edu.iis.mto.testreactor.dishwasher.pump.WaterPump;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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

    private RunResult buildStatus(int timeRelevantForProgram, Status statusRelevant) {
        return RunResult.builder()
                        .withStatus(statusRelevant)
                        .withRunMinutes(timeRelevantForProgram)
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
