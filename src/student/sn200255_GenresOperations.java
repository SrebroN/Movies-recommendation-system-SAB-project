package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.GenresOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sn200255_GenresOperations implements GenresOperations {
    private Connection conn = DB.getInstance().getConnection();
    @Override
    public Integer addGenre(String name) {
        if(doesGenreExist(name)){
            return null;
        }
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO GENRE (NAME) VALUES (?)",PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateGenre(Integer genreId, String name) {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE GENRE SET NAME=? WHERE idGen=?")) {
            ps.setString(1, name);
            ps.setInt(2, genreId);
            if(ps.executeUpdate()>0){
                return genreId;
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer removeGenre(Integer genreId) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps=conn.prepareStatement("DELETE FROM MOVIE_GENRES WHERE idGen=?");
            ps.setInt(1, genreId);
            ps.executeUpdate();
            ps=conn.prepareStatement("DELETE FROM GENRE WHERE idGen=?");
            ps.setInt(1, genreId);
            conn.commit();
            if(ps.executeUpdate()>0){
                return genreId;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean doesGenreExist(String name) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDGEN FROM GENRE WHERE NAME = ?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getGenreId(String name) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDGEN FROM GENRE WHERE Name=?")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt("IDGEN");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getAllGenreIds() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDGEN FROM GENRE")){
            ResultSet rs = ps.executeQuery();
            List<Integer> genreIds = new ArrayList<>();
            while (rs.next()) {
                genreIds.add(rs.getInt("IDGEN"));
            }
            return genreIds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
