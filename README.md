# ComPosiX VarArgs

ComPosiX VarArgs (aka ***cpx-varargs***) is a powerful framework of classes and interfaces designed to provide an advanced alternative to traditional data processing utilities. It seamlessly integrates with existing frameworks such as the Java Collections Framework (JCF) and Java Streams (`java.util.stream` package). The central idea of cpx-varargs is to organize and manipulate data in a table-like form, enabling complex data operations with a fluent and type-safe API.

## Key Features

- **Fluent API for Data Manipulation**: Perform complex data operations such as grouping, collecting, and joining with a concise and readable syntax.
- **Seamless Integration**: Works seamlessly with Java Collections and Streams, allowing you to leverage existing data structures and operations.
- **Advanced Grouping and Aggregation**: Group data by specified keys and aggregate values using custom functions.
- **Flexible Joining Capabilities**: Join datasets in one-to-one and one-to-many relationships based on matching keys.
- **Type Safety**: Ensure type safety with generics and type parameters, reducing the risk of runtime errors.
- **Custom Collectors and Comparators**: Use custom collector functions and comparators for flexible and powerful data manipulation.

## Getting Started

To help you get started with cpx-varargs, let's consider an example where you're managing a shop using the [Petstore API](https://petstore3.swagger.io). In this scenario, customers are calling to inquire whether you have pets available in a certain category.

The first thing you'll need is a list of available categories. Unfortunately, the API doesn't provide a direct resource path to query all categories. However, you can retrieve the categories indirectly by fetching all available pets from [this endpoint](https://petstore.swagger.io/v2/pet/findByStatus?status=available). If you click the link, you'll notice there is a large number of pets listed. Manually collecting all the categories from this data would be a tedious task, which is where *cpx-varargs* comes in handy.
```Java
public class MyPetstore {
    // Select the APIs to use (Swagger Petstore API URLs)
    static Api petStoreApi = Api.select(ArgsI
        // Title for the Swagger Petstore API (OpenAPI 3.0 version)
        .of("title", "Swagger Petstore - OpenAPI 3.0")  
        
        // Alternatively, use the older Swagger Petstore v2 API by uncommenting the next line:
        // .of("title", "Swagger Petstore")  // Title for the Swagger Petstore v2 API (OpenAPI 2.0)

        .andOf(
            "https://petstore.swagger.io/v2/swagger.json",  // Swagger v2 API JSON
            "https://petstore3.swagger.io/api/v3/openapi.json"  // Swagger v3 API JSON
        )
    );
    
    // see also QuickStartTest::testShowListOfCategoryNames
    public static void main(String[] args) throws IOException {
        // Retrieve all available pets from the API
        ArgsI<Pet> pets = petStoreApi.resource(        
            "/pet/findByStatus",  // API endpoint path
            Pet[].class           // Data transfer object (DTO) for pets        
        ).get("?status=available");

        // Group pets by category and count them
        ArgsI<Category> categories = pets
            .groupByA(Pet::category)
            .collectA(x -> 1L, Long::sum);

        // Group categories by name and count them
        ArgsI<String> categoryNames = categories
            .groupByA(Category::name)
            .collectA(x -> 1L, Long::sum);

        // Output all category names with their counts
        System.out.println("List of available categories:");

        for (Row row : categoryNames.rows()) {
            System.out.println(row);
        }
    }
}
```