package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.common.exception.persistence.ConflictException;
import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointRepository;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointSpecification;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint_;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersistDatapointFeatureUnitTests {

    @InjectMocks
    PersistDataPointFeature persistDataPointFeature;

    @Mock
    private DataPointRepository repository;

    @Captor
    private ArgumentCaptor<DataPoint> dataPointArgumentCaptor;

    @Test
    void onValidDataPointShouldSaveAndReturn() throws Exception {
        DataPoint point = DataPointHelper.create(1L);
        persistDataPointFeature.save(point);

        verify(repository, times(1)).save(dataPointArgumentCaptor.capture());

        assertEquals("external-id-1", dataPointArgumentCaptor.getValue().getExternalId());
        assertEquals("some-value-1", dataPointArgumentCaptor.getValue().getValue());
        assertEquals("some-comment-1", dataPointArgumentCaptor.getValue().getComment());
        assertEquals(1, dataPointArgumentCaptor.getValue().getSignificance());
    }

    @Test
    void onInvalidDatapointShouldThrowError() throws Exception {
        DataPoint point = DataPointHelper.create(1L);

        persistDataPointFeature.save(point);
        persistDataPointFeature.save(point);

        doThrow(DataIntegrityViolationException.class).when(repository).save(any());

        assertThrows(ConflictException.class, () -> {
            persistDataPointFeature.save(point);
        });
    }
}
