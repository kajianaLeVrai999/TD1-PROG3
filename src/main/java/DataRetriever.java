import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection db;

    public DataRetriever() {
        this.db = new DBConnection();
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM product_category";

        try (Connection conn = db.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                categories.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public List<Product> getProductList(int page, int size) {
        List<Product> products = new ArrayList<>();
        int offset = (page - 1) * size;

        String sql = "SELECT p.id, p.name, p.price, p.creation_datetime " +
                "FROM product p ORDER BY p.id LIMIT ? OFFSET ?";

        try (Connection conn = db.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        getCategoriesForProduct(conn, rs.getInt("id"))
                );
                products.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    private List<Category> getCategoriesForProduct(Connection conn, int productId) throws SQLException {
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT id, name FROM product_category WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        }

        return categories;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName,
                                               Instant creationMin, Instant creationMax) {

        List<Product> products = new ArrayList<>();

        String sql =
                "SELECT DISTINCT p.id, p.name, p.price, p.creation_datetime " +
                        "FROM product p " +
                        "LEFT JOIN product_category c ON p.id = c.product_id " +
                        "WHERE 1=1 ";

        List<Object> params = new ArrayList<>();

        if (productName != null) {
            sql += "AND p.name ILIKE ? ";
            params.add("%" + productName + "%");
        }
        if (categoryName != null) {
            sql += "AND c.name ILIKE ? ";
            params.add("%" + categoryName + "%");
        }
        if (creationMin != null) {
            sql += "AND p.creation_datetime >= ? ";
            params.add(Timestamp.from(creationMin));
        }
        if (creationMax != null) {
            sql += "AND p.creation_datetime <= ? ";
            params.add(Timestamp.from(creationMax));
        }

        try (Connection conn = db.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getTimestamp("creation_datetime").toInstant(),
                        getCategoriesForProduct(conn, rs.getInt("id"))
                );
                products.add(p);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    public List<Product> getProductsByCriteria(String productName, String categoryName,
                                               Instant creationMin, Instant creationMax,
                                               int page, int size) {

        List<Product> filtered = getProductsByCriteria(productName, categoryName, creationMin, creationMax);

        int offset = (page - 1) * size;
        int end = Math.min(offset + size, filtered.size());

        if (offset >= filtered.size()) {
            return new ArrayList<>();
        }

        return filtered.subList(offset, end);
    }
}