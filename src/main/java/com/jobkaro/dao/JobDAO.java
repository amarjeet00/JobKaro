package com.jobkaro.dao;

import com.jobkaro.model.Job;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class JobDAO {

    private final JdbcTemplate jdbc;

    public JobDAO(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final RowMapper<Job> ROW_MAPPER = (rs, i) -> {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setProviderId(rs.getInt("provider_id"));
        j.setProviderName(rs.getString("provider_name"));
        j.setProviderRating(rs.getDouble("provider_rating"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setCategory(rs.getString("category"));
        j.setPayment(rs.getDouble("payment"));
        j.setPaymentType(rs.getString("payment_type"));
        j.setWorkersNeeded(rs.getInt("workers_needed"));
        j.setAddress(rs.getString("address"));
        j.setCity(rs.getString("city"));
        Date d = rs.getDate("job_date");
        if (d != null) j.setJobDate(d.toLocalDate());
        j.setDuration(rs.getString("duration"));
        j.setUrgent(rs.getInt("is_urgent") == 1);
        j.setStatus(rs.getString("status"));
        j.setViews(rs.getInt("views"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) j.setCreatedAt(ts.toLocalDateTime());
        return j;
    };

    private static final String BASE = "SELECT j.*, u.name AS provider_name, u.rating AS provider_rating "
                                     + "FROM jobs j JOIN users u ON j.provider_id = u.id ";

    public List<Job> findAll() {
        return jdbc.query(BASE + "WHERE j.is_active=1 ORDER BY j.is_urgent DESC, j.created_at DESC", ROW_MAPPER);
    }

    public List<Job> findOpen() {
        return jdbc.query(BASE + "WHERE j.status='open' AND j.is_active=1 ORDER BY j.is_urgent DESC, j.created_at DESC", ROW_MAPPER);
    }

    public List<Job> findByCity(String city) {
        return jdbc.query(BASE + "WHERE j.city=? AND j.status='open' AND j.is_active=1 ORDER BY j.is_urgent DESC", ROW_MAPPER, city);
    }

    public List<Job> findByProvider(int providerId) {
        return jdbc.query(BASE + "WHERE j.provider_id=? AND j.is_active=1 ORDER BY j.created_at DESC", ROW_MAPPER, providerId);
    }

    public List<Job> findUrgent() {
        return jdbc.query(BASE + "WHERE j.is_urgent=1 AND j.status='open' AND j.is_active=1 LIMIT 6", ROW_MAPPER);
    }

    public List<Job> search(String q) {
        String like = "%" + q + "%";
        return jdbc.query(BASE + "WHERE (j.title LIKE ? OR j.category LIKE ?) AND j.status='open' AND j.is_active=1 ORDER BY j.is_urgent DESC", ROW_MAPPER, like, like);
    }

    public Job findById(int id) {
        List<Job> r = jdbc.query(BASE + "WHERE j.id=?", ROW_MAPPER, id);
        return r.isEmpty() ? null : r.get(0);
    }

    public int create(Job j) {
        return jdbc.update(
            "INSERT INTO jobs (provider_id,title,description,category,payment,payment_type,workers_needed,address,city,job_date,duration,is_urgent) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
            j.getProviderId(), j.getTitle(), j.getDescription(), j.getCategory(),
            j.getPayment(), j.getPaymentType(), j.getWorkersNeeded(), j.getAddress(),
            j.getCity(), java.sql.Date.valueOf(j.getJobDate()), j.getDuration(), j.isUrgent() ? 1 : 0
        );
    }

    public void updateStatus(int jobId, String status) {
        jdbc.update("UPDATE jobs SET status=?, updated_at=NOW() WHERE id=?", status, jobId);
    }
}
