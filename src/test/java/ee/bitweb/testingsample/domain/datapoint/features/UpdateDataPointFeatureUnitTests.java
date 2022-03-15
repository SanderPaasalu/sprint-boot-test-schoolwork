package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.features.create.CreateDataPointFeature;
import ee.bitweb.testingsample.domain.datapoint.features.create.CreateDataPointModel;
import ee.bitweb.testingsample.domain.datapoint.features.update.UpdateDataPointFeature;
import ee.bitweb.testingsample.domain.datapoint.features.update.UpdateDataPointModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateDataPointFeatureUnitTests {

    @InjectMocks
    private UpdateDataPointFeature updateDataPointFeature;

    @Mock
    PersistDataPointFeature persistDataPointFeature;

    @Captor
    private ArgumentCaptor<DataPoint> dataPointArgumentCaptor;

    @Test
    void onSuccessDataPointModelShouldBeUpdated() throws Exception{
        UpdateDataPointModel dataPointModel = new UpdateDataPointModel(
                "external-id-1",
                "value-1",
                "comment-1",
                1
        );
        DataPoint point = DataPointHelper.create(1L);
        doReturn(point).when(persistDataPointFeature).save(point);
        updateDataPointFeature.update(point, dataPointModel);


        assertAll(
                () -> Assertions.assertEquals("external-id-1", point.getExternalId()),
                () -> Assertions.assertEquals("value-1", point.getValue()),
                () -> Assertions.assertEquals("comment-1", point.getComment()),
                () -> Assertions.assertEquals(1, point.getSignificance())
        );
    }

    @Test
    void onValidDataModelShouldSaveAndReturn() throws  Exception {
        UpdateDataPointModel dataPointModel = new UpdateDataPointModel(
                "external-id-1",
                "value-1",
                "comment-1",
                1
        );
        DataPoint point = DataPointHelper.create(1L);
        doReturn(point).when(persistDataPointFeature).save(point);
        updateDataPointFeature.update(point, dataPointModel);
        verify(persistDataPointFeature, times(1)).save(dataPointArgumentCaptor.capture());

        assertEquals("external-id-1", dataPointArgumentCaptor.getValue().getExternalId());
        assertEquals("value-1", dataPointArgumentCaptor.getValue().getValue());
        assertEquals("comment-1", dataPointArgumentCaptor.getValue().getComment());
        assertEquals(1, dataPointArgumentCaptor.getValue().getSignificance());
    }

}
