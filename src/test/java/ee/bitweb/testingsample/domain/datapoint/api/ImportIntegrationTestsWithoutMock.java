package ee.bitweb.testingsample.domain.datapoint.api;

import ee.bitweb.testingsample.common.trace.TraceIdCustomizerImpl;
import ee.bitweb.testingsample.domain.datapoint.DataPointHelper;
import ee.bitweb.testingsample.domain.datapoint.MockServerHelper;
import ee.bitweb.testingsample.domain.datapoint.common.DataPoint;
import ee.bitweb.testingsample.domain.datapoint.common.DataPointRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static ee.bitweb.testingsample.domain.datapoint.api.model.DataPointMapper.toResponse;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"data-points.external.baseUrl=http://localhost:12347/"}
)
public class ImportIntegrationTestsWithoutMock {
    private static final String URI = "/data-points/import";
    private static final String REQUEST_ID = "ThisIsARequestId";
    private static ClientAndServer externalService;

    @BeforeAll
    static void setup() {
        externalService = ClientAndServer.startClientAndServer(12347);
    }

    @BeforeEach
    public void beforeEach() {
        externalService.reset();
    }

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private DataPointRepository repository;

    @Test
    @Transactional
    void onRequestShouldRequestDataPointsFromExternalServiceAndPersist() throws Exception {
        repository.save(DataPointHelper.create(1L));
        MockServerHelper.SetupGetMockRouteWithString(
                externalService,
                "/data-points",
                200,
                1,
                createExternalServiceResponse(
                        List.of(
                                createExternalServiceResponse(1L),
                                createExternalServiceResponse(2L),
                                createExternalServiceResponse(3L)
                        )
                ).toString()
        );
        // http://localhost:12347/data-points

        mockmvc.perform(createDefaultRequest())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[0].externalId", is("external-id-1")))
                .andExpect(jsonPath("$[1].externalId", is("external-id-2")))
                .andExpect(jsonPath("$[2].externalId", is("external-id-3")))
                .andExpect(jsonPath("$[0].value", is("value-1")))
                .andExpect(jsonPath("$[1].value", is("value-2")))
                .andExpect(jsonPath("$[2].value", is("value-3")))
                .andExpect(jsonPath("$[0].comment", is("comment-1")))
                .andExpect(jsonPath("$[1].comment", is("comment-2")))
                .andExpect(jsonPath("$[2].comment", is("comment-3")))
                .andExpect(jsonPath("$[0].significance", is(1)))
                .andExpect(jsonPath("$[1].significance", is(0)))
                .andExpect(jsonPath("$[2].significance", is(1)));

        List<DataPoint> dataPoints = repository.findAll();

        assertEquals("external-id-1", dataPoints.get(0).getExternalId());
        assertEquals("external-id-2", dataPoints.get(1).getExternalId());
        assertEquals("external-id-3", dataPoints.get(2).getExternalId());
        assertEquals("value-1", dataPoints.get(0).getValue());
        assertEquals("value-2", dataPoints.get(1).getValue());
        assertEquals("value-3", dataPoints.get(2).getValue());
        assertEquals("comment-1", dataPoints.get(0).getComment());
        assertEquals("comment-2", dataPoints.get(1).getComment());
        assertEquals("comment-3", dataPoints.get(2).getComment());
        assertEquals(1, dataPoints.get(0).getSignificance());
        assertEquals(0, dataPoints.get(1).getSignificance());
        assertEquals(1, dataPoints.get(2).getSignificance());

        externalService.verify(request().withMethod("GET").withPath("/data-points"));
    }

    private DataPoint createDataPoint(Long id) {
        DataPoint p = new DataPoint();

        return p;
    }

    JSONArray createExternalServiceResponse(Collection<JSONObject> objects) {
        JSONArray array = new JSONArray();
        objects.forEach(array::put);

        return array;
    }

    JSONObject createExternalServiceResponse(Long id) {
        JSONObject element = new JSONObject();

        element.put("externalId", "external-id-" + id);
        element.put("value", "value-" + id);
        element.put("comment", "comment-" + id);
        element.put("significance", (id % 2));

        return element;
    }

    private MockHttpServletRequestBuilder createDefaultRequest() {
        return post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(TraceIdCustomizerImpl.DEFAULT_HEADER_NAME, REQUEST_ID);
    }
}
