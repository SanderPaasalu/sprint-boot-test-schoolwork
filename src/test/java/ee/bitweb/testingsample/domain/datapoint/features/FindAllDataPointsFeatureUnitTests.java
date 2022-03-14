package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class FindAllDataPointsFeatureUnitTests {

    @InjectMocks
    FindAllDataPointsFeature findAllDataPointsFeature;

    @Mock
    private DataPointRepository repository;

    @Captor
    private ArgumentCaptor<DataPoint> dataPointArgumentCaptor;

    @Test
    void onFindingAllDataPointsShouldReturnArrayOfDataPoints() throws Exception {
        DataPoint point1 = DataPointHelper.create(1L);
        DataPoint point2 = DataPointHelper.create(2L);

        //when(repository.findAll()).thenReturn(List.of(point1, point2));

        verify(findAllDataPointsFeature, times(1)).find();
        List<DataPoint> dataPoints = findAllDataPointsFeature.find();

        assertEquals(2L, dataPoints.size());
        assertEquals("some-comment-1", dataPoints.get(0).getComment());
        assertEquals("some-value-1", dataPoints.get(0).getValue());
        assertEquals(1, dataPoints.get(0).getSignificance());
        assertEquals("some-comment-2", dataPoints.get(1).getComment());
        assertEquals("some-value-2", dataPoints.get(1).getValue());
        assertEquals(0, dataPoints.get(1).getSignificance());

    }

}
