package dev.morphia.test.query.experimental.filters;

import com.mongodb.client.MongoCollection;
import dev.morphia.query.FindOptions;
import dev.morphia.test.TestBase;
import dev.morphia.test.models.Budget;
import dev.morphia.test.models.User;
import org.bson.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static dev.morphia.aggregation.experimental.expressions.ComparisonExpressions.gt;
import static dev.morphia.aggregation.experimental.expressions.Expressions.field;
import static dev.morphia.query.experimental.filters.Filters.and;
import static dev.morphia.query.experimental.filters.Filters.bitsAllClear;
import static dev.morphia.query.experimental.filters.Filters.bitsAllSet;
import static dev.morphia.query.experimental.filters.Filters.bitsAnyClear;
import static dev.morphia.query.experimental.filters.Filters.bitsAnySet;
import static dev.morphia.query.experimental.filters.Filters.expr;
import static dev.morphia.query.experimental.filters.Filters.gt;
import static dev.morphia.query.experimental.filters.Filters.gte;
import static dev.morphia.query.experimental.filters.Filters.in;
import static dev.morphia.query.experimental.filters.Filters.jsonSchema;
import static dev.morphia.query.experimental.filters.Filters.lt;
import static dev.morphia.query.experimental.filters.Filters.lte;
import static dev.morphia.query.experimental.filters.Filters.nin;
import static dev.morphia.query.experimental.filters.Filters.nor;
import static dev.morphia.query.experimental.filters.Filters.or;
import static dev.morphia.query.experimental.filters.Filters.size;
import static java.util.Arrays.asList;
import static org.bson.Document.parse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class FiltersTest extends TestBase {
    @Test
    public void testAnd() {
        getDatastore().find(Budget.class)
                      .filter(and(lt("budget", 10000), gt("budget", 12)))
                      .iterator();
        getDatastore().find(Budget.class)
                      .filter(and(lte("budget", 10000), gte("budget", 12)))
                      .iterator();
    }

    @Test
    public void testBitsAllClear() {
        MongoCollection<Document> collection = getDatabase().getCollection("users");

        collection.insertMany(asList(
            new Document("a", 54).append("binaryValueofA", "00110110").append("_t", "User"),
            new Document("a", 20).append("binaryValueofA", "00010100").append("_t", "User"),
            new Document("a", 20.0).append("binaryValueofA", "00010100").append("_t", "User")));

        FindOptions options = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(options),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAllClear("a", 35)).iterator(options)
                                             .toList().size(), 2));

        lazyAssert(() -> getDatastore().getLoggedQuery(options),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAllClear("a", new int[]{1, 5})).iterator(options)
                                             .toList().size(), 2));
    }

    @Test
    public void testBitsAllSet() {
        MongoCollection<Document> collection = getDatabase().getCollection("users");

        collection.insertMany(asList(
            new Document("a", 54).append("binaryValueofA", "00110110").append("_t", "User"),
            new Document("a", 20).append("binaryValueofA", "00010100").append("_t", "User"),
            new Document("a", 20.0).append("binaryValueofA", "00010100").append("_t", "User")));

        final FindOptions options = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(options),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAllSet("a", 50)).iterator(options)
                                             .toList().size(), 1));

        final FindOptions findOptions = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(findOptions),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAllSet("a", new int[]{1, 5})).iterator(findOptions)
                                             .toList().size(), 1));
    }

    @Test
    public void testBitsAnyClear() {
        MongoCollection<Document> collection = getDatabase().getCollection("users");

        collection.insertMany(asList(
            new Document("a", 54).append("binaryValueofA", "00110110").append("_t", "User"),
            new Document("a", 20).append("binaryValueofA", "00010100").append("_t", "User"),
            new Document("a", 20.0).append("binaryValueofA", "00010100").append("_t", "User")));

        FindOptions options = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(options),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAnyClear("a", 35)).iterator(options)
                                             .toList().size(), 3));

        final FindOptions findOptions = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(findOptions),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAnyClear("a", new int[]{1, 5})).iterator(findOptions)
                                             .toList().size(), 2));
    }

    @Test
    public void testBitsAnySet() {
        MongoCollection<Document> collection = getDatabase().getCollection("users");

        collection.insertMany(asList(
            new Document("a", 54).append("binaryValueofA", "00110110").append("_t", "User"),
            new Document("a", 20).append("binaryValueofA", "00010100").append("_t", "User"),
            new Document("a", 20.0).append("binaryValueofA", "00010100").append("_t", "User")));

        FindOptions options = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(options),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAnySet("a", 35)).iterator(options)
                                             .toList().size(), 1));

        final FindOptions findOptions = new FindOptions().logQuery();

        lazyAssert(() -> getDatastore().getLoggedQuery(findOptions),
            () -> assertEquals(getDatastore().find(User.class)
                                             .disableValidation()
                                             .filter(bitsAnySet("a", new int[]{1, 5})).iterator(findOptions)
                                             .toList().size(), 1));
    }

    @Test
    public void testExpr() {
        insert("budget", asList(
            parse("{ '_id' : 1, 'category' : 'food', 'budget': 400, 'spent': 450 }"),
            parse("{ '_id' : 2, 'category' : 'drinks', 'budget': 100, 'spent': 150 }"),
            parse("{ '_id' : 3, 'category' : 'clothes', 'budget': 100, 'spent': 50 }"),
            parse("{ '_id' : 4, 'category' : 'misc', 'budget': 500, 'spent': 300 }"),
            parse("{ '_id' : 5, 'category' : 'travel', 'budget': 200, 'spent': 650 }")));

        List<Budget> budgets = getDatastore().find(Budget.class)
                                             .filter(expr(gt(field("spent"), field("budget")))).iterator()
                                             .toList();

        assertEquals(budgets.size(), 3);
    }

    @Test
    public void testIn() {
        getDatastore().find(Budget.class)
                      .filter(in("budget", asList(123, 234)))
                      .iterator();
    }

    @Test
    public void testJsonSchema() {
        insert("inventory", List.of(
            parse("{ item: 'journal', qty: NumberInt(25), size: { h: 14, w: 21, uom: 'cm' }, instock: true }"),
            parse("{ item: 'notebook', qty: NumberInt(50), size: { h: 8.5, w: 11, uom: 'in' }, instock: true }"),
            parse("{ item: 'paper', qty: NumberInt(100), size: { h: 8.5, w: 11, uom: 'in' }, instock: 1 }"),
            parse("{ item: 'planner', qty: NumberInt(75), size: { h: 22.85, w: 30, uom: 'cm' }, instock: 1 }"),
            parse("{ item: 'postcard', qty: NumberInt(45), size: { h: 10, w: 15.25, uom: 'cm' }, instock: true }"),
            parse("{ item: 'apple', qty: NumberInt(45), status: 'A', instock: true }"),
            parse("{ item: 'pears', qty: NumberInt(50), status: 'A', instock: true }")));

        Document myschema = parse("{\n"
                                  + "  required: [ 'item', 'qty', 'instock' ],\n"
                                  + "  properties: {\n"
                                  + "    item: { bsonType: 'string' },\n"
                                  + "    qty: { bsonType: 'int' },\n"
                                  + "    size: {\n"
                                  + "      bsonType: 'object',\n"
                                  + "      required: [ 'uom' ],\n"
                                  + "      properties: {\n"
                                  + "        uom: { bsonType: 'string' },\n"
                                  + "        h: { bsonType: 'double' },\n"
                                  + "        w: { bsonType: 'double' }\n"
                                  + "      }\n"
                                  + "    },\n"
                                  + "    instock: { bsonType: 'bool' }\n"
                                  + "  }\n"
                                  + "}");

        List<Document> inventory = getDatastore().find("inventory", Document.class).filter(jsonSchema(myschema))
                                                 .iterator()
                                                 .toList();

        Assert.assertFalse(inventory.isEmpty(), "Should find some matches");
    }

    @Test
    public void testNin() {
        getDatastore().find(Budget.class)
                      .filter(nin("budget", asList(123, 234)))
                      .iterator();
    }

    @Test
    public void testNor() {
        getDatastore().find(Budget.class)
                      .filter(nor(lt("budget", 10000), gt("budget", 12)))
                      .iterator();
    }

    @Test
    public void testOr() {
        getDatastore().find(Budget.class)
                      .filter(or(lt("budget", 10000), gt("budget", 12)))
                      .iterator();
    }

    @Test
    public void testSize() {
        getDatastore().save(List.of(new User("John", LocalDate.now(), "puppies", "kittens", "heavy metal"),
            new User("Janice", LocalDate.now(), "Chandler", "NYC")));

        User likes = getDatastore().find(User.class)
                                   .filter(size("likes", 3)).iterator()
                                   .next();

        assertEquals(likes.name, "John");

        likes = getDatastore().find(User.class)
                              .filter(size("likes", 2)).iterator()
                              .next();

        assertEquals(likes.name, "Janice");

        likes = getDatastore().find(User.class)
                              .filter(size("likes", 20)).iterator()
                              .tryNext();

        assertNull(likes);
    }
}