import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Product {

    private int id;
    private String name;
    private BigDecimal price;
    private Instant creationDatetime;
    private List<Category> categories;

    public Product(int id, String name, BigDecimal price, Instant creationDatetime, List<Category> categories) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.creationDatetime = creationDatetime;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}