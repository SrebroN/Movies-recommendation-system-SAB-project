package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.RatingsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sn200255_RatingsOperations implements RatingsOperations {
    private Connection conn= DB.getInstance().getConnection();
    @Override
    public boolean addRating(Integer userId, Integer movieId, Integer score) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RATINGS WHERE IDUSE=? AND IDMov=?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO RATINGS (IdUse,IdMov,Rating)VALUES(?,?,?)");
                ps.setInt(1, userId);
                ps.setInt(2, movieId);
                ps.setInt(3, score);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean updateRating(Integer userId, Integer movieId, Integer score) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RATINGS WHERE IDUSE=? AND IDMOV=?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE RATINGS SET Rating=? WHERE idUse=? AND idMov=?");
                ps.setInt(1, score);
                ps.setInt(2, userId);
                ps.setInt(3, movieId);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean removeRating(Integer userId, Integer movieId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RATINGS WHERE IDUSE=? AND IDMOV=?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM RATINGS WHERE IDUse=? AND IDmov=?");
                ps.setInt(1, userId);
                ps.setInt(2, movieId);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Integer getRating(Integer userId, Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT R.Rating FROM RATINGS R WHERE R.idUse=? AND R.IdMov=?")){
            ps.setInt(1, userId);
            ps.setInt(2, movieId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt(1);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getRatedMoviesByUser(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT R.idMov FROM RATINGS R WHERE idUse=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Integer> list = new ArrayList<>();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getUsersWhoRatedMovie(Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT R.idUse FROM RATINGS R WHERE idMov=?")) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            List<Integer> list = new ArrayList<>();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
