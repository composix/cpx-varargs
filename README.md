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

To get you started with cpx-varargs, suppose as an example that you are managing a shop based on the [Petstore API](https://petstore3.swagger.io). People are calling with requests whether you have a pet available in a certain category. So first thing you want to know is what are the available categories. Unfortunately, the API does not have a direct resource path to query all categories. But it is possible to find the categories by retrieving all available pets from https://petstore.swagger.io/v2/pet/findByStatus?status=available. If you click this link you will see there are a large number of pets. So collecting all the categories by hand is hard work. This is where cpx-varargs comes in:
```Java
public class MyPetstore {
    static ObjectMapper MAPPER = new ObjectMapper();

    // see also QuickStartTest::testShowListOfCategoryNames
    public static void main(String[] args) throws IOException {
        // Configure Jackson to accept case-insensitive enums
        MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

        // Url to OpenAPI spec of Petstore API
        URI apiUri = URI
            // .create("https://petstore.swagger.io/v2/pet/findByStatus?status=available");
            .create("https://petstore3.swagger.io/api/v3/openapi.json");

        // Retrieve all available Pets using Jackson
        ArgsI<Pet> pets = ArgsI.of(MAPPER
            .readValue(apiUri
                .resolve("pet/findByStatus?status=available")
                    .toURL(),
                Pet[].class
            )
        );

        // group by category with pet counts
        ArgsI<Category> categories = pets
            .groupByA(Pet::category)
            .collectA(x -> 1L, Long::sum);

        // group categories by name
        ArgsI<String> categoryNames = categories
            .groupByA(Category::name)
            .collectA(x -> 1L, Long::sum);

        // show a list of all category names
        for (String categoryName : categoryNames.columnA()) {
            System.out.println(categoryName);
        }
    }
}
```
