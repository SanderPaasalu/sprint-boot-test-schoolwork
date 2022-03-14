package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointRepository;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointSpecification;
import ee.bitweb.testingsample.domain.datapoint.features.create.CreateDataPointFeature;
import ee.bitweb.testingsample.domain.datapoint.features.create.CreateDataPointModel;
import ee.bitweb.testingsample.domain.datapoint.features.update.UpdateDataPointFeature;
import ee.bitweb.testingsample.domain.datapoint.features.update.UpdateDataPointModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CreateDataPointFeatureUnitTests {

    @InjectMocks
    private CreateDataPointFeature createDataPointFeature;

    @Mock
    private UpdateDataPointFeature updateDataPointFeature;

    @Captor
    private ArgumentCaptor<DataPoint> dataPointArgumentCaptor;
    @Captor
    private ArgumentCaptor<UpdateDataPointModel> dataPointModelArgumentCaptor;

    @Mock
    private DataPointRepository repository;

    @Test
    void onSuccessDataPointModelShouldBeCreated() throws Exception {
        CreateDataPointModel dataPointModel = new CreateDataPointModel(
                "external-id-1",
                "value-1",
                "comment-1",
                1
        );
        DataPoint point = DataPointHelper.create(1L);
        // Mock request
        doReturn(point).when(updateDataPointFeature).update(any(), any());
        createDataPointFeature.create(dataPointModel);

        assertAll(
                () -> Assertions.assertEquals("external-id-1", point.getExternalId()),
                () -> Assertions.assertEquals("some-value-1", point.getValue()),
                () -> Assertions.assertEquals("some-comment-1", point.getComment()),
                () -> Assertions.assertEquals(1, point.getSignificance())
        );
    }

    @Test
    void onValidDataModelShouldSaveAndReturn() throws  Exception {
        CreateDataPointModel dataPointModel = new CreateDataPointModel(
                "external-id-1",
                "value-1",
                "comment-1",
                1
        );
        DataPoint point = DataPointHelper.create(1L);
        doReturn(point).when(updateDataPointFeature).update(any(), any());
        createDataPointFeature.create(dataPointModel);
        verify(updateDataPointFeature, times(1)).update(dataPointArgumentCaptor.capture(), dataPointModelArgumentCaptor.capture());

        assertEquals("external-id-1", dataPointModelArgumentCaptor.getValue().getExternalId());
        assertNull(dataPointArgumentCaptor.getValue().getExternalId());
        //System.out.println("dataPointArgumentCaptor.getValue() = " + dataPointArgumentCaptor.getValue());
        assertEquals("value-1", dataPointModelArgumentCaptor.getValue().getValue());
        assertNull(dataPointArgumentCaptor.getValue().getValue());
        assertEquals("comment-1", dataPointModelArgumentCaptor.getValue().getComment());
        assertNull(dataPointArgumentCaptor.getValue().getComment());
        assertEquals(1, dataPointModelArgumentCaptor.getValue().getSignificance());
        assertEquals(1, dataPointArgumentCaptor.getValue().getSignificance());
    }
}
