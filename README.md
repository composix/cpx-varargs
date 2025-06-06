# ComPosiX VarArgs

ComPosiX VarArgs (*cpx-varargs*) is a framework that simplifies advanced data processing in Java. It provides a fluent and type-safe API for manipulating and organizing data, with powerful utilities for grouping, collecting, joining, and aggregating data. Seamlessly integrating into the Java Collections and Streams APIs, *cpx-varargs* enables more concise data operations across your applications.

Designed to organize data in a table-like form, *cpx-varargs* enables complex operations with ease. Whether you're processing large datasets, interacting with REST APIs, or working with complex data structures, this library integrates smoothly into your codebase, offering a clean and intuitive API.

## Key Features

- **Fluent API for Data Manipulation**: Perform complex data operations such as grouping, collecting, and joining with a concise and readable syntax - enables easy-to-maintain code for performing complex data operations.
- **Seamless Integration**: Works seamlessly with Java Collections and Streams, allowing you to leverage existing data structures and operations.
- **Advanced Grouping and Aggregation**: Group data by specified keys and aggregate values using custom functions, providing a more efficient and flexible approach than the traditional Stream API grouping.
- **Flexible Joining Capabilities**: Join datasets in one-to-one and one-to-many relationships based on matching keys, enabling SQL-like joins in NoSQL environments such as a collection of REST APIs.
- **Type Safety**: Ensure type safety with generics and type parameters, reducing the risk of runtime errors.
- **Custom Collectors and Comparators**: Use custom collector functions and comparators for flexible and powerful data manipulation.

If you find this project useful, consider starring it on GitHub. For more details, check out the JavaDoc or contribute to the project!

## Getting Started

To help you get started with *cpx-varargs*, let's consider an example where you're managing a shop using the [Petstore API](https://petstore3.swagger.io). In this scenario, customers are calling to inquire whether you have pets available in a certain category.

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
        .withHeaders();
    );
    
    // Main method to retrieve and process pet store data
    public static void main(String[] args) throws IOException {
    // Retrieve all available pets from the API based on their status
        ArgsI<Pet> pets = petStoreApi
        .resource(
            (CharSequence) "/pet/findByStatus", // API endpoint to fetch pets by status
            Pet.class                           // Pet class as the DTO
        )
        .get("?status=available");              // Fetch pets with the status "available"

        // Extract unique category names from the list of pets
        ArgsI<String> categoryNames = pets
            .groupByA(Pet::category)    // get unique categories
            .collect()                  // collect into ArgsI<Category>
            .groupByA(Category::name)   // get unique names
            .collect();                 // collect again

        // Output all category names
        System.out.println("List of available categories:");
        for (CharSequence categoryName : categoryNames.columnA()) {
            System.out.println(categoryName);
        }
    }
}
```