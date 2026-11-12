import entities.*;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** One method per task of "Exercise: Introduction to Hibernate". Each returns what the task prints. */
public class Engine {

    private final EntityManager em;

    public Engine(EntityManager em) {
        this.em = em;
    }

    public String run(int task, String input) {
        return switch (task) {
            case 2 -> changeCasing();
            case 3 -> containsEmployee(input);
            case 4 -> employeesWithSalaryOver50000();
            case 5 -> employeesFromDepartment();
            case 6 -> addNewAddressAndUpdateEmployee(input);
            case 7 -> addressesWithEmployeeCount();
            case 8 -> employeeWithProjects(Integer.parseInt(input.trim()));
            case 9 -> latest10Projects();
            case 10 -> increaseSalaries();
            case 11 -> employeesByFirstName(input);
            case 12 -> employeesMaximumSalaries();
            case 13 -> removeTowns(input);
            default -> "Invalid task number. Choose 2-13.";
        };
    }

    /** 2. Detach towns with names longer than 5 symbols, uppercase the rest. */
    String changeCasing() {
        em.getTransaction().begin();
        List<Town> towns = em.createQuery("SELECT t FROM Town t", Town.class).getResultList();
        towns.stream().filter(t -> t.getName().length() > 5).forEach(em::detach);
        towns.stream().filter(em::contains).forEach(t -> t.setName(t.getName().toUpperCase()));
        em.getTransaction().commit();
        return "";
    }

    /** 3. Is there an employee with this full name. */
    String containsEmployee(String fullName) {
        Long count = em.createQuery("SELECT COUNT(e) FROM Employee e WHERE CONCAT(e.firstName, ' ', e.lastName) = :name", Long.class)
                .setParameter("name", fullName.trim())
                .getSingleResult();
        return count > 0 ? "Yes" : "No";
    }

    /** 4. First names of employees with salary over 50 000. */
    String employeesWithSalaryOver50000() {
        return em.createQuery("SELECT e.firstName FROM Employee e WHERE e.salary > 50000", String.class)
                .getResultList().stream()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 5. Employees from "Research and Development", by salary then id. */
    String employeesFromDepartment() {
        return em.createQuery("SELECT e FROM Employee e WHERE e.department.name = 'Research and Development' " +
                              "ORDER BY e.salary, e.id", Employee.class)
                .getResultList().stream()
                .map(e -> "%s %s from %s - $%.2f".formatted(e.getFirstName(), e.getLastName(), e.getDepartment().getName(), e.getSalary()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 6. Create the address "Vitoshka 15" in Sofia and move the employee with this last name to it. */
    String addNewAddressAndUpdateEmployee(String lastName) {
        List<Employee> employees = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = :name", Employee.class)
                .setParameter("name", lastName.trim())
                .getResultList();
        if (employees.isEmpty()) {
            return "There is no such employee.";
        }

        em.getTransaction().begin();
        Town sofia = em.createQuery("SELECT t FROM Town t WHERE t.name = 'Sofia'", Town.class)
                .getResultList().stream().findFirst().orElse(null);
        Address address = new Address("Vitoshka 15", sofia);
        em.persist(address);
        employees.forEach(e -> e.setAddress(address));
        em.getTransaction().commit();
        return "";
    }

    /** 7. Top 10 addresses by number of employees. */
    String addressesWithEmployeeCount() {
        return em.createQuery("SELECT a FROM Address a ORDER BY SIZE(a.employees) DESC, a.town.id, a.id", Address.class)
                .setMaxResults(10)
                .getResultList().stream()
                .map(a -> "%s, %s - %d employees".formatted(a.getText(), a.getTown() == null ? "" : a.getTown().getName(), a.getEmployees().size()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 8. Employee with their projects ordered by name. */
    String employeeWithProjects(int id) {
        Employee employee = em.find(Employee.class, id);
        if (employee == null) {
            return "No such employee.";
        }
        StringBuilder out = new StringBuilder("%s %s - %s".formatted(employee.getFirstName(), employee.getLastName(), employee.getJobTitle()));
        employee.getProjects().stream()
                .map(Project::getName)
                .sorted()
                .forEach(name -> out.append(System.lineSeparator()).append("\t").append(name));
        return out.toString();
    }

    /** 9. The 10 most recently started projects, ordered by name. */
    String latest10Projects() {
        return em.createQuery("SELECT p FROM Project p ORDER BY p.startDate DESC", Project.class)
                .setMaxResults(10)
                .getResultList().stream()
                .sorted(Comparator.comparing(Project::getName))
                .map(p -> String.join(System.lineSeparator(),
                        "Project name: " + p.getName(),
                        " \tProject Description: " + p.getDescription(),
                        " \tProject Start Date:" + p.getStartDate(),
                        " \tProject End Date: " + p.getEndDate()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 10. +12% salary for Engineering, Tool Design, Marketing and Information Services. */
    String increaseSalaries() {
        List<String> departments = List.of("Engineering", "Tool Design", "Marketing", "Information Services");

        em.getTransaction().begin();
        List<Employee> employees = em.createQuery("SELECT e FROM Employee e WHERE e.department.name IN :departments ORDER BY e.id", Employee.class)
                .setParameter("departments", departments)
                .getResultList();
        employees.forEach(e -> e.setSalary(e.getSalary().multiply(new BigDecimal("1.12"))));
        em.getTransaction().commit();

        return employees.stream()
                .map(e -> "%s %s ($%.2f)".formatted(e.getFirstName(), e.getLastName(), e.getSalary()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 11. Employees whose first name starts with the given pattern (case-insensitive). */
    String employeesByFirstName(String pattern) {
        return em.createQuery("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE :pattern", Employee.class)
                .setParameter("pattern", pattern.trim().toLowerCase() + "%")
                .getResultList().stream()
                .map(e -> "%s %s - %s - ($%.2f)".formatted(e.getFirstName(), e.getLastName(), e.getJobTitle(), e.getSalary()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 12. Departments whose max salary is not between 30 000 and 70 000. */
    String employeesMaximumSalaries() {
        return em.createQuery("SELECT e.department.name, MAX(e.salary) FROM Employee e GROUP BY e.department.id, e.department.name " +
                              "HAVING MAX(e.salary) NOT BETWEEN 30000 AND 70000 ORDER BY e.department.id", Object[].class)
                .getResultList().stream()
                .map(row -> "%s %.2f".formatted(row[0], row[1]))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /** 13. Delete a town and all its addresses; employees living there lose their address. */
    String removeTowns(String townName) {
        Town town = em.createQuery("SELECT t FROM Town t WHERE t.name = :name", Town.class)
                .setParameter("name", townName.trim())
                .getResultList().stream().findFirst().orElse(null);
        if (town == null) {
            return "No such town.";
        }

        em.getTransaction().begin();
        List<Address> addresses = em.createQuery("SELECT a FROM Address a WHERE a.town = :town", Address.class)
                .setParameter("town", town)
                .getResultList();
        addresses.forEach(a -> {
            a.getEmployees().forEach(e -> e.setAddress(null));
            em.remove(a);
        });
        em.remove(town);
        em.getTransaction().commit();

        return "%d address%s in %s deleted".formatted(addresses.size(), addresses.size() == 1 ? "" : "es", town.getName());
    }
}
