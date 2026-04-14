package student;

import student.Connecting.DB;
import rs.ac.bg.etf.sab.operations.MoviesOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sn200255_MoviesOperations implements MoviesOperations {
    private Connection conn= DB.getInstance().getConnection();
    //Takodje kao za tag gako zanr ne postoji prvo ga insertovati u GENRE
    @Override
    public Integer addMovie(String title, Integer genreId, String director) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM GENRE WHERE IDGEN=?")) {
            stmt.setInt(1, genreId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PreparedStatement ps = conn.prepareStatement("Insert into MOVIE(title,director) Values(?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, title);
                ps.setString(2, director);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Integer id = rs.getInt(1);
                    addGenreToMovie(id, genreId);
                    return id;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateMovieTitle(Integer movieId, String title) {
        try (PreparedStatement ps=conn.prepareStatement("UPDATE MOVIE set title=? where idMov=?")){
            ps.setString(1,title);
            ps.setInt(2,movieId);
            if(ps.executeUpdate()>0) {
                return movieId;
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
//ako zanr ne posotji, prvo ga kreiraj i ubaci i ubaci u GENRE tabelu, pa nastavi
    @Override
    public Integer addGenreToMovie(Integer movieId, Integer genreId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MOVIE_GENRES WHERE IDMov=? AND IdGen=?")) {
            stmt.setInt(1,movieId);
            stmt.setInt(2,genreId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return null;
            }
            PreparedStatement ps=conn.prepareStatement("SELECT * FROM MOVIE WHERE IDMOV=?");
            ps.setInt(1,movieId);
            rs=ps.executeQuery();
            //kada se negira 8/9 movies ali -1 na public module
            //kada se ne negira 5/9 na movies ali 0 na public module
            if(!rs.next()) {//za prolaz
                return null;
            }
            ps=conn.prepareStatement("SELECT * FROM GENRE WHERE IDGEN=?");
            ps.setInt(1,genreId);
            rs=ps.executeQuery();
            if(!rs.next()) {
                return null;
            }
            ps=conn.prepareStatement("insert into MOVIE_GENRES(idMov,idgen) values(?,?)");
            ps.setInt(1,movieId);
            ps.setInt(2,genreId);
            if(ps.executeUpdate()>0) {
                return movieId;
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer removeGenreFromMovie(Integer movieId, Integer genreId) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MOVIE_GENRES WHERE IDMov=? AND IDgen=?")) {
            stmt.setInt(1,movieId);
            stmt.setInt(2,genreId);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()) {
                return null;
            }
            PreparedStatement ps=conn.prepareStatement("SELECT * FROM GENRE WHERE IDgen=?");
            ps.setInt(1,genreId);
            if(ps.executeQuery().next()) {
                ps = conn.prepareStatement("DELETE FROM MOVIE_GENRES WHERE idMov=? AND IdGen=?");
                ps.setInt(1, movieId);
                ps.setInt(2, genreId);
                if (ps.executeUpdate() > 0) {
                    return movieId;
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer updateMovieDirector(Integer movieId, String director) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MOVIE WHERE IDMOV=?")) {
            stmt.setInt(1,movieId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                 PreparedStatement ps=conn.prepareStatement("UPDATE MOVIE SET Director=? WHERE idMov=?");
                 ps.setString(1,director);
                 ps.setInt(2,movieId);
                 if(ps.executeUpdate()>0) {
                     return movieId;
                 }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Integer removeMovie(Integer movieId) {
        try(PreparedStatement stmt=conn.prepareStatement("SELECT * FROM MOVIE WHERE IDMOV=?")) {
            stmt.setInt(1,movieId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()) {
                conn.setAutoCommit(false);
                PreparedStatement ps = conn.prepareStatement("DELETE FROM MOVIE_GENRES WHERE idMov=?");
                ps.setInt(1, movieId);
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM RATINGS WHERE idMov=?");
                ps.setInt(1, movieId);
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM MOVIE WHERE idMov=?");
                ps.setInt(1, movieId);
                int res = ps.executeUpdate();
                conn.commit();
                if (res > 0) {
                    return movieId;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getMovieIds(String title, String director) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDMOV FROM MOVIE WHERE TITLE=? AND DIRECTOR=?")) {
            ps.setString(1,title);
            ps.setString(2,director);
            ResultSet rs=ps.executeQuery();
            List<Integer> list=new ArrayList<>();
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
    public List<Integer> getAllMovieIds() {
        try (PreparedStatement ps=conn.prepareStatement("SELECT IdMov from MOVIE")){
            ResultSet rs=ps.executeQuery();
            List<Integer> list=new ArrayList<>();
            while(rs.next()){
                list.add(rs.getInt(1));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getMovieIdsByGenre(Integer genreId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT idmov from MOVIE_GENRES WHERE IDgen=?")) {
            ps.setInt(1,genreId);
            ResultSet rs=ps.executeQuery();
            List<Integer> list=new ArrayList<>();
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
    public List<Integer> getGenreIdsForMovie(Integer movieId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT idGen FROM MOVIE_GENRES WHERE IDMov=?")) {
            ps.setInt(1,movieId);
            ResultSet rs=ps.executeQuery();
            List<Integer> list=new ArrayList<>();
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
    public List<Integer> getMovieIdsByDirector(String director) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT IDMOV FROM MOVIE WHERE DIRECTOR =?")) {
            ps.setString(1,director);
            ResultSet rs=ps.executeQuery();
            List<Integer> list=new ArrayList<>();
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
