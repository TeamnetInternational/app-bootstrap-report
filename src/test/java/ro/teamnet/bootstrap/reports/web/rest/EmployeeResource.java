package ro.teamnet.bootstrap.reports.web.rest;

import org.springframework.web.bind.annotation.RestController;
import ro.teamnet.bootstrap.reports.domain.Employee;
import ro.teamnet.bootstrap.reports.service.ReportsService;

import javax.inject.Inject;

/**
 * A sample class, to be used in tests.
 *
 * @author Bogdan.Stefan
 * @version 1.0 Date: 2015-03-04
 */
@RestController
public class EmployeeResource extends AbstractReportsResource<Employee, Long> {

    private ReportsService<Employee, Long> employeeService;

    @Inject
    public EmployeeResource(ReportsService<Employee, Long>  employeeService) {
        super(employeeService);
        this.employeeService = employeeService;
    }
}
