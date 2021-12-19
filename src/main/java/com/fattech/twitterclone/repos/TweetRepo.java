package com.fattech.twitterclone.repos;

import com.fattech.twitterclone.models.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class TweetRepo implements EntityDAO<Tweet>{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TweetRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Tweet entity) {
        String INSERT_QUERY = "" +
                "INSERT INTO tbl_tweets(" +
                "playerId, " +
                "message, " +
                "imageUrl, " +
                "replyOf, " +
                "retweetOf, " +
                "createdAt, " +
                "lastModified) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entity.getPlayerId());
            ps.setString(2, entity.getMessage());
            ps.setString(3, entity.getImageUrl());
            ps.setLong(4, entity.getReplyOf());
            ps.setLong(5, entity.getRetweetOf());
            ps.setLong(6, entity.getCreatedAt());
            ps.setLong(7, entity.getLastModified());

            return ps;
        }, keyHolder);

        var key = (BigInteger) Objects.requireNonNull(keyHolder.getKeys()).get("GENERATED_KEY");
        return key.longValue();
    }

    @Override
    public Long update(Tweet entity, Long id) {
        String UPDATE_QUERY = "" +
                "UPDATE tbl_tweets " +
                "SET playerId=?, " +
                "message=?, " +
                "imageUrl=?, " +
                "replyOf=?, " +
                "retweetOf=?, " +
                "createdAt=?, " +
                "lastModified=? WHERE id=?";
        jdbcTemplate.update(
                UPDATE_QUERY,
                entity.getPlayerId(),
                entity.getMessage(),
                entity.getImageUrl(),
                entity.getReplyOf(),
                entity.getRetweetOf(),
                entity.getCreatedAt(),
                entity.getLastModified(),
                id
        );
        return id;
    }

    @Override
    public Long delete(Long id) {
        String DELETE_QUERY = "DELETE FROM tbl_tweets WHERE id=?";
        jdbcTemplate.update(DELETE_QUERY, id);
        return id;
    }

    @Override
    public List<Tweet> getAll() {
        String SELECT_ALL_QUERY = "SELECT * FROM tbl_tweets";
        return jdbcTemplate.query(SELECT_ALL_QUERY, new BeanPropertyRowMapper<>(Tweet.class));
    }

    @Override
    public Tweet getById(Long id) {
        String FIND_QUERY = "SELECT * FROM tbl_tweets WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(FIND_QUERY, new BeanPropertyRowMapper<>(Tweet.class), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}