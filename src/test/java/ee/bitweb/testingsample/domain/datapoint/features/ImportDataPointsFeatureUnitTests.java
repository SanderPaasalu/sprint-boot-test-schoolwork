package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.common.exception.persistence.EntityNotFoundException;
import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.external.ExternalService;
import ee.bitweb.testingsample.domain.datapoint.external.ExternalServiceApi;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImportDataPointsFeatureUnitTests {

    @InjectMocks
    private ImportDataPointsFeature importDataPointsFeature;

    @Mock
    private CreateDataPointFeature createFeature;

    @Mock
    private UpdateDataPointFeature updateFeature;

    @Mock
    private GetDataPointByExternalIdFeature getDataPointByExternalIdFeature;

    @Mock
    private ExternalService externalService;

    @Captor
    private ArgumentCaptor<DataPoint> dataPointArgumentCaptor;
    @Captor
    private ArgumentCaptor<UpdateDataPointModel> dataPointModelArgumentCaptor;
    @Captor
    private ArgumentCaptor<CreateDataPointModel> dataPointModelCArgumentCaptor;

    @Test
    void onValidExternalIdShouldUpdateListOfDataPointsFromApi() throws Exception {
        DataPoint point = DataPointHelper.create(1L);
        ExternalServiceApi.DataPointResponse dPResponse = new ExternalServiceApi.DataPointResponse();
        dPResponse.setExternalId("external-id-1");
        dPResponse.setValue("value-1");
        dPResponse.setComment("comment-1");
        dPResponse.setSignificance(1);

        doReturn(List.of(dPResponse)).when(externalService).getAll();
        doReturn(point).when(getDataPointByExternalIdFeature).get(any());
        importDataPointsFeature.execute();
        verify(updateFeature, times(1)).update(dataPointArgumentCaptor.capture(),dataPointModelArgumentCaptor.capture());


        assertAll(
                () -> Assertions.assertEquals("external-id-1", dPResponse.getExternalId()),
                () -> Assertions.assertEquals("external-id-1", point.getExternalId()),
                () -> Assertions.assertEquals("external-id-1", dataPointModelArgumentCaptor.getValue().getExternalId()),
                () -> Assertions.assertEquals("external-id-1", dataPointArgumentCaptor.getValue().getExternalId()),
                () -> Assertions.assertEquals("value-1", dPResponse.getValue()),
                () -> Assertions.assertEquals("value-1", dataPointModelArgumentCaptor.getValue().getValue()),
                () -> Assertions.assertEquals("some-value-1", dataPointArgumentCaptor.getValue().getValue()),
                () -> Assertions.assertEquals("some-value-1", point.getValue()),
                () -> Assertions.assertEquals("comment-1", dPResponse.getComment()),
                () -> Assertions.assertEquals("some-comment-1", dataPointArgumentCaptor.getValue().getComment()),
                () -> Assertions.assertEquals("comment-1", dataPointModelArgumentCaptor.getValue().getComment()),
                () -> Assertions.assertEquals("some-comment-1", point.getComment()),
                () -> Assertions.assertEquals(1, dPResponse.getSignificance()),
                () -> Assertions.assertEquals(1, dataPointModelArgumentCaptor.getValue().getSignificance()),
                () -> Assertions.assertEquals(1, dataPointArgumentCaptor.getValue().getSignificance()),
                () -> Assertions.assertEquals(1, point.getSignificance())
        );
    }

    @Test
    void onInvalidExternalIdShouldLogNotFoundAndCreateNewDataPoint() throws Exception {
        ExternalServiceApi.DataPointResponse dPResponse = new ExternalServiceApi.DataPointResponse();
        dPResponse.setExternalId("external-id-1");
        dPResponse.setValue("value-1");
        dPResponse.setComment("comment-1");
        dPResponse.setSignificance(1);
        DataPoint point = DataPointHelper.create(1L);

        doReturn(List.of(dPResponse)).when(externalService).getAll();
        doReturn(point).when(createFeature).create(any());

        doThrow(EntityNotFoundException.class).when(getDataPointByExternalIdFeature).get(anyString());
        importDataPointsFeature.execute();
        verify(createFeature, times(1)).create(dataPointModelCArgumentCaptor.capture());

        assertAll(
                () -> Assertions.assertEquals("external-id-1", dataPointModelCArgumentCaptor.getValue().getExternalId()),
                () -> Assertions.assertEquals("external-id-1", point.getExternalId()),
                () -> Assertions.assertEquals("external-id-1", dPResponse.getExternalId()),
                () -> Assertions.assertEquals("value-1", dataPointModelCArgumentCaptor.getValue().getValue()),
                () -> Assertions.assertEquals("some-value-1", point.getValue()),
                () -> Assertions.assertEquals("value-1", dPResponse.getValue()),
                () -> Assertions.assertEquals("comment-1", dataPointModelCArgumentCaptor.getValue().getComment()),
                () -> Assertions.assertEquals("some-comment-1", point.getComment()),
                () -> Assertions.assertEquals("comment-1", dPResponse.getComment()),
                () -> Assertions.assertEquals(1, dataPointModelCArgumentCaptor.getValue().getSignificance())
        );
    }
}
