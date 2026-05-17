package com.jobkaro.dao;

import com.jobkaro.model.User;
import com.jobkaro.util.HashUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbc;
    public UserDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final RowMapper<User> ROW_MAPPER = (rs, i) -> {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setCity(rs.getString("city"));
        u.setAvatarPath(rs.getString("avatar_path"));
        u.setActive(rs.getInt("is_active") == 1);
        u.setRating(rs.getDouble("rating"));
        u.setTotalRatings(rs.getInt("total_ratings"));
        u.setCompletedJobs(rs.getInt("completed_jobs"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    };

    public User authenticate(String email, String password) {
        List<User> r = jdbc.query("SELECT * FROM users WHERE email=? AND is_active=1", ROW_MAPPER, email.toLowerCase().trim());
        if (r.isEmpty()) return null;
        User u = r.get(0);
        return HashUtil.verifyPassword(password, u.getPassword()) ? u : null;
    }

    public User findById(int id) {
        List<User> r = jdbc.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public User findByEmail(String email) {
        List<User> r = jdbc.query("SELECT * FROM users WHERE email=? AND is_active=1", ROW_MAPPER, email.toLowerCase());
        return r.isEmpty() ? null : r.get(0);
    }

    public boolean emailExists(String email) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE email=?", Integer.class, email.toLowerCase());
        return count != null && count > 0;
    }

    public User create(User user) {
        String hash = HashUtil.hashPassword(user.getPassword());
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (name,email,phone,password,role,city) VALUES (?,?,?,?,?,?)",
                new String[]{"id"}
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().toLowerCase());
            ps.setString(3, user.getPhone());
            ps.setString(4, hash);
            ps.setString(5, user.getRole());
            ps.setString(6, user.getCity());
            return ps;
        }, keys);
        user.setId(keys.getKey().intValue());
        user.setPassword(hash);
        return user;
    }

    public void updateProfile(int id, String name, String city) {
        jdbc.update("UPDATE users SET name=?, city=?, updated_at=NOW() WHERE id=?", name, city, id);
    }
}
