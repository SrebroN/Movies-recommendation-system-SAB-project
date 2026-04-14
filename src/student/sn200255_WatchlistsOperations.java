package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.WatchlistsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sn200255_WatchlistsOperations implements WatchlistsOperations {
    private Connection conn= DB.getInstance().getConnection();

    @Override
    public boolean addMovieToWatchlist(Integer userId, Integer movieId) {
        if(isMovieInWatchlist(userId, movieId)) {return false;}
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO WATCHLIST(IdUse,IdMov) VALUES(?,?)")) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            if(ps.executeUpdate()>0){
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean removeMovieFromWatchlist(Integer userId, Integer movieId) {
        if(!isMovieInWatchlist(userId, movieId)) {return false;}
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM WATCHLIST WHERE IdUse=? AND IdMov=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            return ps.executeUpdate() > 0;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMovieInWatchlist(Integer userId, Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM WATCHLIST WHERE IDUse=? AND IDMov=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getMoviesInWatchlist(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IdMov FROM WATCHLIST WHERE IDUse=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Integer> movies = new ArrayList<>();
            while(rs.next()) {
                movies.add(rs.getInt("IdMov"));
            }
            return movies;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getUsersWithMovieInWatchlist(Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IdUse FROM WATCHLIST WHERE IdMov=?")) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            List<Integer> users = new ArrayList<>();
            while(rs.next()) {
                users.add(rs.getInt("IdUse"));
            }
            return users;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
