# Reflection 1

## Clean Code Principles Applied

During the implementation of the Edit and Delete Product features, several clean code principles were applied:

### 1. Single Responsibility Principle (SRP)

Each layer has a clear and focused responsibility:

* **Controller**: Handles HTTP requests and response mapping.
* **Service**: Contains business logic abstraction.
* **Repository**: Manages data operations.
* **Model**: Represents the domain entity.

> This separation improves maintainability and readability.

### 2. Layered Architecture (Separation of Concerns)

The project follows a clear layered architecture:
`Controller` > `Service` > `Repository` > `Model`

This prevents tight coupling between components and makes future improvements (e.g., database integration) easier.

### 3. Meaningful Naming

* **Methods**: `create`, `findAll`, `findById`, `update`, and `delete` clearly describe their behavior.
* **Variables**: Names like `productRepository`, `allProducts`, and `productData` are self-explanatory.
* This reduces the need for excessive comments.

### 4. Small and Focused Methods

Each method performs a single well-defined task:

* **Controller** methods only handle routing and delegation.
* **Repository** methods only manipulate the data structure.
* **Service** methods coordinate between layers.

### 5. Avoiding Code Duplication

The edit and delete logic reuse existing service and repository structures without duplicating logic in the controller.

---

## Secure Coding Practices Applied

1. **Encapsulation**
   The `Product` model uses Lombok's `@Getter` and `@Setter`, ensuring fields remain private and controlled through accessors.
3. **No Direct Data Exposure**
   The repository does not expose the internal `List<Product>` directly. Instead, it returns an `Iterator` or controlled access through service methods, reducing the risk of unintended modifications.

---

## Identified Improvements

Although the implementation works correctly, several improvements can be made:

* **Null Handling** : Methods like `findById` return `null` if the product is not found, which can cause `NullPointerException`.
* *Improvement*: Use `Optional<Product>` and add validation in the controller.


* **Validation** : Currently, there is no validation for empty product names, negative quantities, or duplicate IDs.
* *Improvement*: Use `@Valid` and annotations such as `@NotBlank` and `@Min(0)`.


* **In-Memory Storage Limitation** : The repository uses an `ArrayList`, meaning data is lost on restart and lacks concurrency handling.
* *Improvement*: Integrate with a database.



---

## Conclusion

The implementation follows clean architecture principles and demonstrates proper separation of concerns. The code is readable, modular, and extensible. However, improvements in input validation, null safety, and persistence integration are necessary to reach production-level quality.

This exercise helped reinforce clean code practices, layered architecture design, and basic secure coding principles in **Spring Boot** applications.

---

# Reflection 2

1. **Code Coverage vs. Code Quality** Having 100% code coverage does not guarantee that the code is free of bugs or errors. Code coverage only reflects that every line of code was executed during testing; it does not verify that the logic is correct or that all possible edge cases (e.g., negative inputs, missing fields) are handled properly.

2. **Clean Code in Functional Tests** Creating a new functional test class with the exact same setup procedures and instance variables (serverPort, testBaseUrl, baseUrl) as the previous test suites violates the DRY (Don't Repeat Yourself) principle. This duplication lowers code quality and makes maintenance difficult.

Improvement: To fix this, I should create a BaseFunctionalTest class that handles the common setup configuration. All specific functional test classes (like CreateProductFunctionalTest and HomePageFunctionalTest) should extend this base class. This removes code duplication and keeps the test files clean and focused on their specific logic.

---

## MODULE 2 : CI/CD Reflection

### 1. Code Quality Issues and Fix Strategy

During this exercise, the main code quality issue I fixed was a SonarQube warning related to workflow permissions.
The workflow was using a global permissions: read-all, which violated the principle of least privilege. 
This configuration was considered too broad because it granted unnecessary access to the workflow.

To fix this, I replaced the global permission with more specific permissions at the job level. I set contents: read and actions: read 
to ensure the workflow only had the access it actually needed. After making this change, 
the SonarQube warning was resolved and the OSSF Scorecard workflow continued to run normally in the public repository.

### 2. Evaluation of CI/CD Implementation

I think the current workflow already meets the concept of Continuous Integration because every push and 
pull request automatically runs tests and code analysis. This helps ensure that new changes are checked 
before being merged into the main branch.

It also follows Continuous Deployment since the application is automatically deployed to a PaaS after a 
successful build and test process. This means there is no need for manual deployment steps. Overall, 
the CI/CD pipeline is working as expected because testing, analysis, and deployment are all automated and connected.

---

# Reflection 3

## MODULE 3 : SOLID Principles Reflection

### 1. Principles Applied

**Single Responsibility Principle (SRP)**  
`CarController` was moved into its own separate file (`CarController.java`) instead of being bundled together with `ProductController.java`. Now each controller only handles what it's supposed to. one class, one job.

**Open/Closed Principle (OCP)**  
`CarController` no longer extends `ProductController`. This inheritance was problematic because any change to `ProductController` could silently break `CarController`. Now both are independent and can be extended without touching each other.

**Liskov Substitution Principle (LSP)**  
The `CarController extends ProductController` relationship was removed because a Car is simply not a Product. Forcing that inheritance just to reuse some behavior was semantically wrong and could lead to subtle bugs down the line.

**Dependency Inversion Principle (DIP)**  
`CarController` used to inject `CarServiceImpl` directly (a concrete class). It now depends on the `CarService` interface instead, which is the right way to do it, high-level modules shouldn't care about implementation details.

> ISP (Interface Segregation Principle) was already in good shape from the start. `CarService` and `ProductService` are separate interfaces, so neither forces irrelevant methods on their implementors.

---

### 2. Advantages of Applying SOLID Principles

- **Easier to maintain**: With SRP in place, if something breaks in the Car feature, you know exactly where to look — `CarController.java`. No need to dig through unrelated Product code.

- **Less risk of breaking things**: After fixing the OCP violation, changes to `ProductController` won't accidentally affect `CarController` anymore. Before, a small change in the parent class could break Car behavior in ways that were hard to trace.

- **Easier to test**: Since `CarController` now depends on the `CarService` interface (DIP), you can easily mock the service in unit tests without having to spin up the full `CarServiceImpl` with all its dependencies.

- **Things make sense semantically**: Removing the `extends ProductController` (LSP fix) means Car endpoints no longer accidentally inherit Product behavior. What you see in the code is what actually happens at runtime.

---

### 3. Disadvantages of Not Applying SOLID Principles

- **SRP violation** — Having both controllers in one file means the file has too many reasons to change. Updating Car routing logic forces you to touch the same file as Product logic, which increases the chance of introducing unintended bugs in unrelated features.

- **OCP & LSP violation** — `CarController extends ProductController` caused Car to silently inherit all Product mappings. If someone added a new method to `ProductController`, `CarController` would be affected without anyone realizing it. That kind of hidden dependency makes the codebase unpredictable.

- **DIP violation** — Injecting `CarServiceImpl` directly into the controller creates tight coupling. If the implementation ever needs to change (say, swapping in-memory storage for a real database), you'd have to go back and change the controller too. Depending on the interface avoids all of that.


# Reflection 4
## 1. Test-Driven Development (TDD) Workflow Reflection
the TDD workflow has proven to be highly useful, though it requires a disciplined mindset. By forcing the creation of tests before the actual implementation (the Red-Green-Refactor cycle), it naturally drove the design of the application. It made me think critically about the expected behavior, edge cases, and the interaction between the Controller, Service, and Repository layers before writing the actual business logic. Most importantly, it provided a strong safety net that gave me confidence when refactoring code later.

### Areas for Improvement:
While the flow is useful, I noticed a tendency to occasionally slip out of the strict TDD mindset. Next time I create more tests, I need to do the following:

Avoid Implementation Coupling: I need to ensure my tests evaluate the behavior and the final output rather than tightly coupling the test to the internal implementation details. Over-mocking can sometimes make tests brittle if the internal logic changes.

Strict Timeliness: I must be stricter about writing the failing test before even creating the skeleton of the production code, rather than writing them almost in parallel.

## 2. F.I.R.S.T. Principle Reflection
Fast: Achieved. By utilizing mocking frameworks (like Mockito) for the Repository and Service layers, the unit tests execute in milliseconds. They do not rely on spinning up a real database or the full application context.

Isolated/Independent: Mostly Achieved. Each test method is generally designed to run independently without relying on the state of previous tests.

Repeatable: Achieved. The tests yield the exact same results regardless of the environment (whether on a local machine or in the automated CI/CD pipeline) because they are decoupled from external infrastructure.

Self-validating: Achieved. The tests use clear, definitive assertions (e.g., assertEquals, assertTrue) to determine success or failure automatically, requiring no manual log inspection.

Timely: Needs Improvement. As noted in the TDD reflection, while the intent was there, some tests were written slightly after the implementation skeleton was already drafted.

### Areas for Improvement:
To fully satisfy the F.I.R.S.T. principles in future implementations, I need to:

Strict Isolation Management: I will be extra careful with shared test fixtures and setup configurations. I must ensure all mock data is freshly instantiated before every single test (using @BeforeEach) to prevent any accidental data leakage or state persistence between test cases.

Enforce Timeliness: I will commit fully to the TDD cycle to ensure tests are written exactly when they are needed—immediately before the production code that is meant to make them pass.