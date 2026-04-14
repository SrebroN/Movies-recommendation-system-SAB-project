package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.TagsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sn200255_TagsOperations implements TagsOperations {
    private Connection conn= DB.getInstance().getConnection();
    //namestiti da ako tag ne postoji u tag tabeli da se prvo insertuje u tag tabelu
    //pa onda da se doda u MOVIE_TAGS
    @Override
    public Integer addTag(Integer movieId, String tag) {
        if(hasTag(movieId, tag))return null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT idMov FROM MOVIE WHERE IDMov=?")) {
            ps.setInt(1, movieId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            Integer idTag=null;
            PreparedStatement stmt=conn.prepareStatement("SELECT T.IDTag FROM TAG T WHERE T.TAG=?");
            stmt.setString(1, tag);
            ResultSet rs2 = stmt.executeQuery();
            if(!rs2.next()) {
                PreparedStatement stmt2=conn.prepareStatement("INSERT INTO TAG (Tag) VALUES (?)",PreparedStatement.RETURN_GENERATED_KEYS);
                stmt2.setString(1, tag);
                int n = stmt2.executeUpdate();
                if (n == 0) return null;
                try (ResultSet keys = stmt2.getGeneratedKeys()) {
                    if (!keys.next()) return null;
                    idTag = keys.getInt(1);
                }
            }else{
                idTag=rs2.getInt(1);
            }

            stmt=conn.prepareStatement("INSERT INTO MOVIE_TAGS (IDMov,IdTag) VALUES (?,?)");
            stmt.setInt(1, movieId);
            stmt.setInt(2, idTag);
            if(stmt.executeUpdate()>0) {
                return movieId;
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer removeTag(Integer movieId, String Tag) {
        if(!hasTag(movieId, Tag))return null;
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM MOVIE_TAGS WHERE IDMov=? AND IDTAG " +
                " IN (SELECT T.IDTag FROM TAG T WHERE T.Tag=?)")) {
            ps.setInt(1, movieId);
            ps.setString(2, Tag);
            if(ps.executeUpdate()>0)return movieId;
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int removeAllTagsForMovie(Integer movieId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MOVIE WHERE IDMov=?")) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {return 0;}
            PreparedStatement ps = conn.prepareStatement("DELETE FROM MOVIE_TAGS WHERE IDMov=?");
            ps.setInt(1, movieId);
            return ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasTag(Integer movieId, String tag) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT MT.IDTAG FROM MOVIE_TAGS MT WHERE MT.IDmov=? " +
                "AND Mt.IdTag IN(SELECT T.IDTAG FROM TAG T WHERE T.TAG=?)")) {
            ps.setInt(1, movieId);
            ps.setString(2, tag);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getTagsForMovie(Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT T.Tag FROM TAG T WHERE T.IDTag " +
                "IN (SELECT MT.IDTag From MOVIE_TAGS MT WHERE MT.IDMov=?)")) {
            ps.setInt(1, movieId);
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

    @Override
    public List<Integer> getMovieIdsByTag(String tag) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT MT.IdMov FROM MOVIE_TAGS MT WHERE MT.Idtag IN" +
                " (SELECT T.IDTag FROM TAG T WHERE T.Tag=?)")) {
            ps.setString(1, tag);
            ResultSet rs = ps.executeQuery();
            List<Integer> list = new ArrayList<>();
            while(rs.next()) {
                list.add(rs.getInt(1));
            }
            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAllTags() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT T.Tag FROM TAG T")) {
            ResultSet rs = ps.executeQuery();
            List<String> list = new ArrayList<>();
            while(rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
