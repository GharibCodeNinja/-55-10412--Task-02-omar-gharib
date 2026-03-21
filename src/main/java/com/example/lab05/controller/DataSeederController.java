package com.example.lab05.controller;

import com.example.lab05.model.Product;
import com.example.lab05.model.cassandra.SensorReading;
import com.example.lab05.model.cassandra.SensorReadingKey;
import com.example.lab05.model.elastic.ProductDocument;
import com.example.lab05.model.mongo.MongoProduct;
import com.example.lab05.model.neo4j.Neo4jProduct;
import com.example.lab05.model.neo4j.Person;
import com.example.lab05.model.neo4j.Purchased;
import com.example.lab05.repository.ProductRepository;
import com.example.lab05.repository.cassandra.SensorReadingRepository;
import com.example.lab05.repository.elastic.ProductSearchRepository;
import com.example.lab05.repository.mongo.MongoProductRepository;
import com.example.lab05.repository.neo4j.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Populates all 6 databases with test data.
 * Hit GET /api/seed once to populate.
 * Idempotent — if data already exists, it skips.
 */
@RestController
public class DataSeederController {

    private static final Logger log = LoggerFactory.getLogger(DataSeederController.class);

    private final ProductRepository pgRepo;
    private final MongoProductRepository mongoRepo;
    private final PersonRepository neo4jRepo;
    private final SensorReadingRepository cassandraRepo;
    private final ProductSearchRepository esRepo;

    public DataSeederController(ProductRepository pgRepo,
                                MongoProductRepository mongoRepo,
                                PersonRepository neo4jRepo,
                                SensorReadingRepository cassandraRepo,
                                ProductSearchRepository esRepo) {
        this.pgRepo = pgRepo;
        this.mongoRepo = mongoRepo;
        this.neo4jRepo = neo4jRepo;
        this.cassandraRepo = cassandraRepo;
        this.esRepo = esRepo;
    }

    @GetMapping("/api/seed")
    public Map<String, Object> seed() {
        Map<String, Object> result = new LinkedHashMap<>();

        if (pgRepo.count() > 0) {
            result.put("status", "SKIPPED");
            result.put("message", "Data already exists. Seed runs only once.");
            return result;
        }

        log.info("DataSeeder: populating all 6 databases...");

        result.put("postgresql", seedPostgres() + " products inserted");
        result.put("mongodb", seedMongo() + " products inserted");
        result.put("neo4j", seedNeo4j());
        result.put("cassandra", seedCassandra() + " activity events inserted");
        result.put("elasticsearch", seedElasticsearch() + " products indexed");

        result.put("status", "SUCCESS");
        result.put("message", "All 6 databases seeded successfully.");

        log.info("DataSeeder: done.");
        return result;
    }

    // ═══════════════════════════════════════════════════════════
    // 1. PostgreSQL — 15 products across 5 categories
    // ═══════════════════════════════════════════════════════════
    private int seedPostgres() {
        List<Product> products = List.of(
            makePgProduct("MacBook Pro 16\"",    "ELECTRONICS", 2499.00, 25),
            makePgProduct("iPhone 16 Pro",       "ELECTRONICS", 1199.00, 80),
            makePgProduct("Sony WH-1000XM5",     "ELECTRONICS",  349.00, 60),
            makePgProduct("Mechanical Keyboard",  "ELECTRONICS",  149.00, 120),
            makePgProduct("USB-C Hub",            "ELECTRONICS",   49.99, 200),
            makePgProduct("North Face Jacket",    "CLOTHING",     320.00, 40),
            makePgProduct("Nike Air Max 90",      "CLOTHING",     130.00, 90),
            makePgProduct("Levi's 501 Jeans",     "CLOTHING",      69.99, 150),
            makePgProduct("DDIA (Kleppmann)",     "BOOKS",         45.99, 300),
            makePgProduct("Clean Code (Martin)",  "BOOKS",         39.99, 250),
            makePgProduct("System Design Interview","BOOKS",       35.00, 180),
            makePgProduct("Yoga Mat",             "SPORTS",        29.99, 200),
            makePgProduct("Running Shoes",        "SPORTS",        89.99, 100),
            makePgProduct("Protein Powder 2kg",   "GROCERY",       54.99, 75),
            makePgProduct("Organic Coffee Beans", "GROCERY",       18.99, 400)
        );
        pgRepo.saveAll(products);
        return products.size();
    }

    private Product makePgProduct(String name, String category, Double price, Integer stock) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setStockQuantity(stock);
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    // 2. MongoDB — same products with rich, varying specs
    // ═══════════════════════════════════════════════════════════
    private int seedMongo() {
        List<MongoProduct> docs = List.of(
            makeMongoProduct("MacBook Pro 16\"", "ELECTRONICS", 2499.00, 4.8,
                List.of("laptop", "apple", "pro"),
                Map.of("cpu", "M3 Max", "ram", "36GB", "storage", "1TB SSD", "weight_kg", 2.14)),
            makeMongoProduct("iPhone 16 Pro", "ELECTRONICS", 1199.00, 4.7,
                List.of("phone", "apple", "5g"),
                Map.of("chip", "A18 Pro", "storage", "256GB", "camera_mp", 48, "color", "Desert Titanium")),
            makeMongoProduct("Sony WH-1000XM5", "ELECTRONICS", 349.00, 4.6,
                List.of("headphones", "wireless", "noise-cancelling"),
                Map.of("driver_mm", 30, "battery_hours", 30, "anc", true, "weight_g", 250)),
            makeMongoProduct("Mechanical Keyboard", "ELECTRONICS", 149.00, 4.5,
                List.of("keyboard", "gaming", "mechanical"),
                Map.of("switches", "Cherry MX Red", "layout", "TKL", "rgb", true, "connection", "USB-C")),
            makeMongoProduct("USB-C Hub", "ELECTRONICS", 49.99, 4.2,
                List.of("hub", "usb-c", "adapter"),
                Map.of("ports", 7, "hdmi", true, "ethernet", true, "pd_watts", 100)),
            makeMongoProduct("North Face Jacket", "CLOTHING", 320.00, 4.5,
                List.of("jacket", "winter", "outdoor"),
                Map.of("size", "L", "material", "Nylon", "waterproof", true, "fill_power", 700)),
            makeMongoProduct("Nike Air Max 90", "CLOTHING", 130.00, 4.4,
                List.of("shoes", "sneakers", "casual"),
                Map.of("size_us", 10, "color", "White/Black", "sole", "Air Max cushioning")),
            makeMongoProduct("Levi's 501 Jeans", "CLOTHING", 69.99, 4.3,
                List.of("jeans", "denim", "classic"),
                Map.of("waist", 32, "length", 32, "fit", "Original", "wash", "Medium Indigo")),
            makeMongoProduct("DDIA (Kleppmann)", "BOOKS", 45.99, 4.9,
                List.of("databases", "distributed-systems", "backend"),
                Map.of("author", "Martin Kleppmann", "pages", 616, "publisher", "O'Reilly", "year", 2017)),
            makeMongoProduct("Clean Code (Martin)", "BOOKS", 39.99, 4.6,
                List.of("clean-code", "software-engineering", "best-practices"),
                Map.of("author", "Robert C. Martin", "pages", 464, "publisher", "Pearson", "year", 2008)),
            makeMongoProduct("System Design Interview", "BOOKS", 35.00, 4.7,
                List.of("system-design", "interview", "architecture"),
                Map.of("author", "Alex Xu", "pages", 322, "publisher", "Self-published", "volume", 1)),
            makeMongoProduct("Yoga Mat", "SPORTS", 29.99, 4.3,
                List.of("yoga", "fitness", "mat"),
                Map.of("thickness_mm", 6, "material", "TPE", "length_cm", 183, "non_slip", true)),
            makeMongoProduct("Running Shoes", "SPORTS", 89.99, 4.5,
                List.of("running", "fitness", "shoes"),
                Map.of("size_us", 10, "drop_mm", 8, "weight_g", 280, "terrain", "Road")),
            makeMongoProduct("Protein Powder 2kg", "GROCERY", 54.99, 4.4,
                List.of("protein", "fitness", "supplement"),
                Map.of("flavor", "Chocolate", "servings", 66, "protein_per_serving_g", 24, "vegan", false)),
            makeMongoProduct("Organic Coffee Beans", "GROCERY", 18.99, 4.6,
                List.of("coffee", "organic", "beans"),
                Map.of("origin", "Ethiopia", "roast", "Medium", "weight_g", 1000, "whole_bean", true))
        );
        mongoRepo.saveAll(docs);
        return docs.size();
    }

    private MongoProduct makeMongoProduct(String name, String category, Double price, Double rating,
                                          List<String> tags, Map<String, Object> specs) {
        MongoProduct p = new MongoProduct();
        p.setName(name);
        p.setCategory(category);
        p.setPrice(price);
        p.setRating(rating);
        p.setTags(new ArrayList<>(tags));
        p.setSpecifications(new HashMap<>(specs));
        return p;
    }

    // ═══════════════════════════════════════════════════════════
    // 3. Neo4j — 6 people, follow relationships, some purchases
    // ═══════════════════════════════════════════════════════════
    private String seedNeo4j() {
        Person alice = new Person("Alice");
        Person bob   = new Person("Bob");
        Person carol = new Person("Carol");
        Person dave  = new Person("Dave");
        Person eve   = new Person("Eve");
        Person frank = new Person("Frank");

        alice.getFollowing().addAll(List.of(bob, carol));
        bob.getFollowing().addAll(List.of(carol, dave));
        carol.getFollowing().add(eve);
        dave.getFollowing().addAll(List.of(eve, frank));
        eve.getFollowing().add(frank);

        Neo4jProduct macbook = new Neo4jProduct("MacBook Pro 16\"", 2499.00);
        Neo4jProduct iphone  = new Neo4jProduct("iPhone 16 Pro", 1199.00);
        Neo4jProduct sony    = new Neo4jProduct("Sony WH-1000XM5", 349.00);
        Neo4jProduct ddia    = new Neo4jProduct("DDIA (Kleppmann)", 45.99);
        Neo4jProduct shoes   = new Neo4jProduct("Running Shoes", 89.99);

        bob.getPurchases().add(new Purchased(macbook, 1));
        bob.getPurchases().add(new Purchased(ddia, 1));
        carol.getPurchases().add(new Purchased(macbook, 1));
        carol.getPurchases().add(new Purchased(sony, 1));
        dave.getPurchases().add(new Purchased(iphone, 1));
        dave.getPurchases().add(new Purchased(ddia, 1));
        eve.getPurchases().add(new Purchased(shoes, 2));
        eve.getPurchases().add(new Purchased(sony, 1));
        frank.getPurchases().add(new Purchased(macbook, 1));

        neo4jRepo.saveAll(List.of(alice, bob, carol, dave, eve, frank));
        return "6 persons, 5 products, 8 FOLLOWS edges, 9 PURCHASED edges";
    }

    // ═══════════════════════════════════════════════════════════
    // 4. Cassandra — activity events over the last 7 days
    // ═══════════════════════════════════════════════════════════
    private int seedCassandra() {
        String[] users = {"Alice", "Bob", "Carol", "Dave", "Eve", "Frank"};
        String[] actions = {"VIEW_PRODUCT", "SEARCH", "ADD_TO_CART", "LOGIN", "VIEW_PRODUCT"};
        Random rand = new Random(42);
        int count = 0;

        for (String user : users) {
            for (int i = 0; i < 10; i++) {
                SensorReadingKey key = new SensorReadingKey();
                key.setSensorId("user-activity-" + user.toLowerCase());
                key.setReadingTime(Instant.now().minus(rand.nextInt(7 * 24 * 60), ChronoUnit.MINUTES));

                SensorReading event = new SensorReading();
                event.setKey(key);
                event.setTemperature((double) rand.nextInt(15) + 1);
                event.setHumidity(0.0);
                event.setLocation(actions[rand.nextInt(actions.length)]);

                cassandraRepo.save(event);
                count++;
            }
        }
        return count;
    }

    // ═══════════════════════════════════════════════════════════
    // 5. Elasticsearch — all 15 products indexed for search
    // ═══════════════════════════════════════════════════════════
    private int seedElasticsearch() {
        List<ProductDocument> docs = List.of(
            makeEsDoc("1", "MacBook Pro 16\"", "Professional laptop with M3 Max chip and 36GB RAM", "ELECTRONICS", 2499.00, List.of("laptop", "apple", "pro"), true),
            makeEsDoc("2", "iPhone 16 Pro", "Flagship smartphone with A18 Pro chip and 48MP camera", "ELECTRONICS", 1199.00, List.of("phone", "apple", "5g"), true),
            makeEsDoc("3", "Sony WH-1000XM5", "Premium wireless noise-cancelling headphones with 30hr battery", "ELECTRONICS", 349.00, List.of("headphones", "wireless", "noise-cancelling"), true),
            makeEsDoc("4", "Mechanical Keyboard", "RGB mechanical gaming keyboard with Cherry MX switches", "ELECTRONICS", 149.00, List.of("keyboard", "gaming", "mechanical"), true),
            makeEsDoc("5", "USB-C Hub", "7-port USB-C hub with HDMI, Ethernet, and 100W PD", "ELECTRONICS", 49.99, List.of("hub", "usb-c", "adapter"), true),
            makeEsDoc("6", "North Face Jacket", "Waterproof 700-fill puffer jacket for winter outdoor use", "CLOTHING", 320.00, List.of("jacket", "winter", "outdoor"), true),
            makeEsDoc("7", "Nike Air Max 90", "Classic sneakers with Air Max cushioning", "CLOTHING", 130.00, List.of("shoes", "sneakers", "casual"), true),
            makeEsDoc("8", "Levi's 501 Jeans", "Original fit medium indigo denim jeans", "CLOTHING", 69.99, List.of("jeans", "denim", "classic"), true),
            makeEsDoc("9", "DDIA (Kleppmann)", "Designing Data-Intensive Applications — the bible of distributed systems", "BOOKS", 45.99, List.of("databases", "distributed-systems", "backend"), true),
            makeEsDoc("10", "Clean Code (Martin)", "A handbook of agile software craftsmanship", "BOOKS", 39.99, List.of("clean-code", "software-engineering", "best-practices"), true),
            makeEsDoc("11", "System Design Interview", "An insider's guide to system design interviews", "BOOKS", 35.00, List.of("system-design", "interview", "architecture"), true),
            makeEsDoc("12", "Yoga Mat", "Non-slip TPE yoga mat 6mm thick", "SPORTS", 29.99, List.of("yoga", "fitness", "mat"), true),
            makeEsDoc("13", "Running Shoes", "Lightweight road running shoes with 8mm drop", "SPORTS", 89.99, List.of("running", "fitness", "shoes"), true),
            makeEsDoc("14", "Protein Powder 2kg", "Chocolate whey protein with 24g per serving", "GROCERY", 54.99, List.of("protein", "fitness", "supplement"), true),
            makeEsDoc("15", "Organic Coffee Beans", "Ethiopian medium roast whole bean coffee", "GROCERY", 18.99, List.of("coffee", "organic", "beans"), true)
        );
        esRepo.saveAll(docs);
        return docs.size();
    }

    private ProductDocument makeEsDoc(String id, String name, String desc, String category,
                                       Double price, List<String> tags, Boolean inStock) {
        ProductDocument d = new ProductDocument();
        d.setId(id);
        d.setName(name);
        d.setDescription(desc);
        d.setCategory(category);
        d.setPrice(price);
        d.setTags(tags);
        d.setInStock(inStock);
        return d;
    }
}
