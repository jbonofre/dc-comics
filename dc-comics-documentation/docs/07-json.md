---
sidebar_position: 7
---

# JSON support

DC Comic JSON proposes a `JsonMapper` API which intends to support `record` models.

Here the supported features list:

* Models must be records or `List<X>` of a supported type, or a `Map<String, X>` of a supported type
* Supported primitives are
** `String`
** `String`, `BigDecimal` (represented as `string` in JSON but incoming data can be a number), `{b,B}oolean`, `{d,D}ouble`, `int`/`Integer`, `{l,L}ouble`, `OffsetDateTime`, `ZonedDateTime`, `LocalDate`, `LocalDateTime`
* Generic mapper is supported, it will bind `Object` as a `Map<String, Object>` values being `String` for JSON strings, `BigDecimal` for JSON numbers, another `Map<String, Object>` for JSON objects and `List<Object>` for JSON lists
* A simple post processor prettifier (takes a JSON as input and formats it). It is used decorating the default `JsonMapper`: `com.dremio.cloud.comic.json.pretty.PrettyJsonMapper`
* The DCComic annotation processor will generate the JSON "codecs" from the code when a record is marked with `@JsonModel`, the codec will be reflection free
* You can customize the attribute names using `@JsonProperty` on the record members
* You can map all unknown attributes in a `Map<String, Object>` member marked with `@JsonOthers` annotation

---
**IMPORTANT**

Static model (annotations) are in `dc-comic-build-api` which is a `provided` bundle - build time only.

## Runtime dependency

```xml
<dependency>
	<groupId>com.dremio.cloud.comic</groupId>
	<artifactId>dc-comic-json</artifactId>
	<version>${dc-comic.version}</version>
</dependency>
```

## Example

To modelise the flow you jsut have to define a record marked with `@JsonModel`:

```java
@JsonModel
public record MyModel(
	@JsonProperty("boolean") boolean aBool,
	BigDecimal bigDecimal,
	int integer,
	Integer nullableInt,
	long lg,
	double more,
	String simplest,
	LocalDate date,
	LocalDateTime dateTime,
	OffsetDateTime offset,
	ZonedDateTime zoned,
	Object generic,
	AnotherModel nested,
	List<Boolean> booleanList,
	List<BigDecimal> bigDecimalList,
	List<Integer> intList,
	Collection<Long> longList,
	List<Double> doubleList,
	Set<String> stringList,
	List<LocalDate> dateList,
	List<LocalDateTime> dateTimeList,
	List<OffsetDateTime> offsetList,
	List<ZonedDateTime> zonedList,
	List<Object> genericList,
	List<AnotherModel> nestedList,
	Map<String, String> mapStringString,
	Map<String, Integer> mapStringInt,
	Map<String, AnotherModel> mapNested) {
}
```

Then read/write data using `JsonMapper`:

```java
@Bean
public class MyService extends HttpServlet {
	@Injection
	JsonMapper mapper;

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		try (final var out = resp.getWriter()) {
			mapper.write(createMyModelInstance(req), out);
		}
	}
}
```

## Enums

Enumerations (de)serialization behavior can be customized by using some specific methods:

```java
public enum MyEnum {
	A, B;

	public String toJsonString() { <1>
		return this == A ? "first" : "second";
	}

	public static MyEnum fromJsonString(final String v) { <2>
		return switch (v) {
			case "first" -> MyEnum.A;
			case "second" -> MyEnum.B;
			default -> throw new IllegalArgumentException("Unsupported '" + v + "'");
		};
	}
}
```

1. `toJsonString` is an instance method with no parameter used to replace `.name()` call during serialization
2. `fromJsonString` is a static method with a `String` parameter used to replace `.valueOf(String)` call during deserialization.
