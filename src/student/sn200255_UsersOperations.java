package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.UsersOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sn200255_UsersOperations implements UsersOperations {
    private Connection conn= DB.getInstance().getConnection();
    @Override
    public Integer addUser(String username) {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO [USER] (username,rewards) VALUES(?,0)",PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,username);
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateUser(Integer userId, String username) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM [USER] WHERE IDUse=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE [USER] SET Username=? WHERE IDUse=?");
                ps.setString(1, username);
                ps.setInt(2, userId);
                if (ps.executeUpdate() > 0) {
                    return userId;
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer removeUser(Integer userId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM [USER] WHERE IDUse=?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement("DELETE FROM WATCHLIST WHERE IDUse=?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM RATINGS WHERE IDUse=?");
                ps.setInt(1, userId);
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM [USER] WHERE IDUse=?");
                ps.setInt(1, userId);
                conn.commit();
                if (ps.executeUpdate() > 0) {
                    return userId;
                }
            }
            return null;

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean doesUserExist(String username) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDUSE FROM [USER] WHERE USERNAME=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getUserId(String username) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDUSE FROM [USER] WHERE USERNAME=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("IDUSE");
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getAllUserIds() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDUSE FROM [USER]")) {
            ResultSet rs = ps.executeQuery();
            List<Integer> users = new ArrayList<>();
            while (rs.next()) {
                users.add(rs.getInt("IDUSE"));
            }
            return users;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getRecommendedMoviesFromFavoriteGenres(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IdMov" +
                "FROM (SELECT DISTINCT " +
                "        M.IdMov, ISNULL(AvgRatings.AvgRating, 0) AS AvgRating" +
                "    FROM MOVIE M JOIN MOVIE_GENRES MG " +
                "        ON M.IdMov = MG.IdMov" +
                "    LEFT JOIN (" +
                "        SELECT IdMov, AVG(Rating) AS AvgRating" +
                "        FROM RATINGS" +
                "        GROUP BY IdMov" +
                "    ) AS AvgRatings " +
                "        ON M.IdMov = AvgRatings.IdMov" +
                "    WHERE M.IdMov NOT IN (SELECT W.IDMov FROM WATCHLIST W WHERE W.IdUse = ?)" +
                "      AND M.IdMov NOT IN (SELECT R.IDMov FROM RATINGS R WHERE R.IdUse = ?)" +
                "      AND MG.IdGen IN (" +
                "            SELECT MG.IdGen" +
                "            FROM RATINGS R" +
                "            JOIN MOVIE_GENRES MG ON R.IdMov = MG.IdMov" +
                "            WHERE R.IdUse = ?" +
                "            GROUP BY MG.IdGen" +
                "            HAVING AVG(R.Rating) >= 8)" +
                ") AS X\n" +
                "ORDER BY X.AvgRating DESC, X.IdMov ASC;\n")) {
            ps.setInt(1, userId);
            ps.setInt(2,userId);
            ps.setInt(3,userId);
            ResultSet rs = ps.executeQuery();
            List<Integer> movies = new ArrayList<>();
            while (rs.next()) {
                movies.add(rs.getInt("IdMov"));
            }
            return movies;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer getRewards(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT Rewards FROM [USER] WHERE IDUse=?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Rewards");
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getThematicSpecializations(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT T.Tag FROM TAG T WHERE T.IDTag IN\n " +
                " (SELECT MT.IdTag\n " +
                " FROM RATINGS R\n " +
                " JOIN MOVIE_TAGS MT ON R.IdMov = MT.IdMov\n " +
                " WHERE R.IdUse =?\n " +
                "   AND R.Rating >= 8\n " +
                " GROUP BY MT.IdTag\n " +
                " HAVING COUNT(*) >= 2)\n ")) {
            //razmotriti HAVING COUNT DISTINCT IDMOV
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<String> tags = new ArrayList<>();
            while (rs.next()) {
                tags.add(rs.getString("Tag"));
            }
            return tags;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //       try (PreparedStatement ps = conn.prepareStatement("SELECT " +
    //                        "(SELECT COUNT(DISTINCT(MT.idTag)) FROM MOVIE_TAGS MT WHERE MT.IDMOV IN" +
    //                "(SELECT R.IdMov FROM RATINGS R WHERE idUse=?)) AS Tags"+
    //                "(SELECT COUNT(DISTINCT(R.IdMov)) FROM RATINGS R WHERE idUse=?) AS Movies")) {
    @Override
    public String getUserDescription(Integer userId) {
        try (PreparedStatement ps = conn.prepareStatement( "SELECT " +
                "  (SELECT COUNT(DISTINCT mt.IDTag) " +
                "     FROM MOVIE_TAGS mt " +
                "     WHERE mt.IDMov IN (SELECT r.IDMov FROM RATINGS r WHERE r.IDUse = ?)) AS Tags, " +
                "  (SELECT COUNT(DISTINCT r.IDMov) " +
                "     FROM RATINGS r WHERE r.IDUse = ?) AS Movies")) {
            ps.setInt(1, userId);
            ps.setInt(2,userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Integer tag = rs.getInt("Tags");
                Integer mov = rs.getInt("Movies");
                if(mov>=10 && tag>=10){
                    return "curious";
                }
                else if(mov>=10){
                    return "focused";
                }
                else {
                    return "undefined";
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
