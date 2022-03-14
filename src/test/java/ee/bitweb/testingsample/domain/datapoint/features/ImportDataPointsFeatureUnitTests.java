package ee.bitweb.testingsample.domain.datapoint.features;

import ee.bitweb.testingsample.domain.datapoint.external.ExternalService;
import ee.bitweb.testingsample.domain.datapoint.features.create.CreateDataPointFeature;
import ee.bitweb.testingsample.domain.datapoint.features.update.UpdateDataPointFeature;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ImportDataPointsFeatureUnitTests {

    @InjectMocks
    ImportDataPointsFeature importDataPointsFeature;

    @Mock
    CreateDataPointFeature createFeature;

    @Mock
    UpdateDataPointFeature updateDataPointFeature;

    @Mock
    GetDataPointByExternalIdFeature getDataPointByExternalIdFeature;

    @Mock
    ExternalService externalService;
}
