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
        programConfiguration = ProgramConfiguration.builder()
                                                .withProgram(WashingProgram.ECO)
                                                .withFillLevel(FillLevel.HALF)
                                                .withTabletsUsed(false)
                                                .build();

        when(door.closed()).thenReturn(true);

        RunResult runResult = dishWasher.start(programConfiguration);

        RunResult expectedRunResult = RunResult.builder()
                                                .withStatus(Status.SUCCESS)
                                                .withRunMinutes(90)
                                                .build();

        assertTrue(runResult.getStatus().equals(expectedRunResult.getStatus()));
        assertTrue(runResult.getRunMinutes() == expectedRunResult.getRunMinutes());
    }

}
