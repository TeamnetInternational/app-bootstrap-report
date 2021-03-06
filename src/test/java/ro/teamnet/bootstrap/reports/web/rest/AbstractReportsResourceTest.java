package ro.teamnet.bootstrap.reports.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ro.teamnet.bootstrap.extend.Filters;
import ro.teamnet.bootstrap.reports.config.ReportsTestConstants;
import ro.teamnet.bootstrap.reports.domain.Employee;
import ro.teamnet.bootstrap.reports.domain.resolve.ReportableArgumentResolver;
import ro.teamnet.bootstrap.reports.repository.EmployeeRepository;
import ro.teamnet.bootstrap.reports.service.EmployeeServiceImpl;
import ro.teamnet.bootstrap.reports.service.ReportsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class AbstractReportsResourceTest {

    private EmployeeResource employeeResource;
    private MockMvc mockMvc;

    private static List<Employee> createEmployeesDataSource() {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(new Employee("Sad", "Panda"));
        employees.add(new Employee("Gigi", "Petrescu"));

        return employees;
    }

    @Before
    public void setUp() throws Exception {
        List<Employee> employeesList = createEmployeesDataSource();
        EmployeeRepository mockEmployeeRepository = mock(EmployeeRepository.class);
        when(mockEmployeeRepository.findAll(any(Filters.class), any(Sort.class))).thenReturn(employeesList);
        when(mockEmployeeRepository.findAll()).thenReturn(employeesList); // We're interested only in the metadata
        when(mockEmployeeRepository.findAll((Filters) isNull(), (Sort) isNull())).thenReturn(employeesList);  // Explicitly call method with this signature
        ReportsService employeeService = new EmployeeServiceImpl(mockEmployeeRepository);
        employeeResource = new EmployeeResource(employeeService);
        mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
    }

    @Test
    public void shouldExportAPdfWhenReportJsonContainsNoFiltersAndSort() throws Exception {

        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON_NO_FILTERS_AND_SORT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/pdf"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }


    @Test
    public void shouldExportAPdfWhenReportJsonContainsFiltersAndSort() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/pdf"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }


    @Test
    public void shouldPassIfContentIsNotSetPDF() throws Exception {

        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/pdf")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void shouldPassIfContentIsInvalidPDF() throws Exception {

        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_MALFORMED_BODY_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void testExportToXlsWhenReportJsonContainsNoFiltersAndSort() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/xls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON_NO_FILTERS_AND_SORT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/vnd.ms-xls"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void shouldExportAXlsWhenReportJsonContainsFiltersAndSort() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/xls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/vnd.ms-xls"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void shouldPassIfContentIsNotSetXLS() throws Exception {

        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/xls")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void shouldPassIfContentIsInvalidXLS() throws Exception {

        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/xls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_MALFORMED_BODY_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void shoudPassIfContentTypeIsInvalid() throws Exception{
        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON_NO_FILTERS_AND_SORT))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void testingAlternativeMethodToExportToPdfWhenReportJsonContainsNoFiltersAndSort() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/pdf/alternative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON_NO_FILTERS_AND_SORT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/pdf"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void testingAlternativeMethodToExportToXls() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/xls/alternative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_BODY_JSON_NO_FILTERS_AND_SORT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.OK.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("application/vnd.ms-xls"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void testingAlternativeMethodToExportToXlsWithEmptyJson() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/xls/alternative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_EMPTY_BODY_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("text/plain;charset=ISO-8859-1"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void testingAlternativeMethodToExportToPdfWithEmptyJson() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/reports/pdf/alternative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_EMPTY_BODY_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());
        assertTrue("Wrong value for 'Content-Type' attribute.",
                result.getResponse().getContentType().equals("text/plain;charset=ISO-8859-1"));
        assertNotNull(result.getResponse().getContentAsByteArray());
    }

    @Test
    public void testingMainExportToXlsMethodWithEmptyJson() throws Exception {
        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/xls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_EMPTY_BODY_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void testingMainExportToPdfMethodWithEmptyJson() throws Exception {
        MockMvc mockMvc = standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(
                        new ReportableArgumentResolver())
                .build();
        MvcResult result = mockMvc.perform(
                post("/reports/pdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ReportsTestConstants.REPORT_REQUEST_EMPTY_BODY_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue("Got wrong HTTP Status in response.",
                result.getResponse().getStatus() == HttpStatus.BAD_REQUEST.value());

    }
}
